package com.judtih.judith_management_system.domain.season;

import com.judtih.judith_management_system.domain.season.dto.CountdownResponse;
import com.judtih.judith_management_system.domain.season.dto.SeasonMemberRequest;
import com.judtih.judith_management_system.domain.season.dto.SeasonRequest;
import com.judtih.judith_management_system.domain.season.dto.SeasonResponse;
import com.judtih.judith_management_system.domain.season.exception.*;
import com.judtih.judith_management_system.domain.user.dto.UserSeasonRequest;
import com.judtih.judith_management_system.domain.user.entity.UserSeason;
import com.judtih.judith_management_system.domain.user.enums.UserRole;
import com.judtih.judith_management_system.domain.user.exception.NoUserFoundException;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;
import com.judtih.judith_management_system.domain.user.repository.UserSeasonRepository;
import com.judtih.judith_management_system.domain.user.service.UserSeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final UserRepository userRepository;
    private final UserSeasonRepository userSeasonRepository;
    private final UserSeasonService userSeasonService;


    @Transactional
    public SeasonResponse createSeason(SeasonRequest request) {


        if(seasonRepository.existsByStatusNot(Status.CLOSED)) {
            throw new AlreadyActiveSeasonException("Cannot create season when one is already active", 409, "Conflict" );
        }


        if(request.getMembers() == null) {
            throw new NoUserFoundException("there is no member", 404, "Not Found");
        }

        // At least one member has to have full access role
        boolean hasFullAccess = false;

        for (SeasonMemberRequest member : request.getMembers()) {
            if (member.getRoles() != null && !Collections.disjoint(member.getRoles(), UserRole.FULL_ACCESS_ROLES)) {
                hasFullAccess = true;
                break;
            }
        }

        if (!hasFullAccess) {
            throw new NoFullAccessMemberFound("At least one member must have a full access role", 400, "Bad Request");
        }



        Season season = new Season(request.getName());
        seasonRepository.save(season);

        for (SeasonMemberRequest member : request.getMembers()) {
            userSeasonService.addUserToSeason(new UserSeasonRequest(member.getUserId(), season.getId(), member.getRoles()));
        }


        return createSeasonResponse(season);

    }

    @Transactional
    public SeasonResponse activateSeason(long id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new NoSeasonFoundException("no season was found with id: " + id, 404, "Not Found"));

        if (season.getStatus() == Status.CLOSED) throw new SeasonClosedException("This season is a closed season", 409, "Conflict");
        else if (season.getStatus() == Status.ACTIVE) throw new AlreadyActiveSeasonException("This season is already active", 409, "Conflict");


        // All User must have a role to activate
        List<UserSeason> userSeasons = userSeasonRepository.findBySeasonId(id);
        List<UserSeason> noRoleUser = new ArrayList<>();

        for(UserSeason us : userSeasons) {
            if (us.getUserRoles() == null || us.getUserRoles().isEmpty()) {
                noRoleUser.add(us);
            }
        }

        if (!noRoleUser.isEmpty()) {
            List<String> names = noRoleUser.stream()
                    .map(us -> us.getUser().getName())
                    .toList();
            throw new NoRoleAssignedException("No roles assigned for: " + names, 400, "Bad Request");
        }

        // At least one user have to be full access member
        boolean hasFullAccess = false;

        for (UserSeason us : userSeasons) {
            if (!Collections.disjoint(us.getUserRoles(), UserRole.FULL_ACCESS_ROLES)) {
                hasFullAccess = true;
                break;
            }
        }
        if (!hasFullAccess) {
            throw new NoFullAccessMemberFound("At least one member must have a full access role", 400, "Bad Request");
        }


        season.activateSeason();

        return createSeasonResponse(season);
    }

    public SeasonResponse getCurrentSeason() {
        Season season = seasonRepository.findByStatus(Status.ACTIVE)
                .orElseThrow(() -> new NoActiveSeasonException("there is no active season", 404, "Not Found"));

        return createSeasonResponse(season);
    }

    public Optional<Season> findCurrentSeason() {
        return seasonRepository.findByStatus(Status.ACTIVE)
                .or(() -> seasonRepository.findByStatus(Status.PREPARING));
    }


    public SeasonResponse getSeason(long id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new NoSeasonFoundException("GetSeason : There is no season with id: " + id, 404, "Not Found"));

        return createSeasonResponse(season);
    }


    public List<SeasonResponse> getAllSeason() {

        return seasonRepository.findAll().stream()
                .map(this::createSeasonResponse)
                .toList();
    }

    @Transactional
    public SeasonResponse updateSeason(SeasonRequest request) {
        Season season = seasonRepository.findById(request.getId())
                .orElseThrow(() -> new NoSeasonFoundException("UpdateSeason: went wrong somewhere. No Event was found with id: " + request.getId(), 404, "Not Found")) ;

        season.updateSeason(request.getName(), request.getEventDate());

        return createSeasonResponse(season);
    }

    @Transactional
    public SeasonResponse closeSeason(long id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new NoSeasonFoundException("closeSeason: went wrong somewhere. No Event was found with id: " + id, 404, "Not Found"));

        if(season.getStatus() == Status.CLOSED) throw new SeasonClosedException("Season is already closed with id: " + id, 409, "Conflict");
        if(season.getStatus() == Status.PREPARING) throw new SeasonPreparingException("Season is in Preparing state, which is not closable id: " + id, 409, "Conflict");

        season.closeSeason();

        return createSeasonResponse(season);

    }

    @Transactional
    public void deleteSeason(Long id) {
        //will not have codes yet
        //needs proper connection to verify
        //nothing should be connected to season for it to be possible to delete. will come back.
    }


    /*
    this method is specific method for finding the effective season at the moment.
    it will search for first season that is ACTIVE, and then PREPARING, than finally last CLOSED season.
    Major user for this method at the moment is Full Access Member logic. When there is no active season, it will use the last CLOSED season to
    search for the current full access member.
     */
    public Optional<Season> findEffectiveSeasonForAccess() {
        return seasonRepository.findByStatus(Status.ACTIVE) //currently active season
                .or(() -> seasonRepository.findByStatus(Status.PREPARING)) //currently preparing season
                .or(() -> seasonRepository.findTopByStatusOrderByCreatedAtDesc(Status.CLOSED)); //last closed season
    }


    public CountdownResponse getCountDown() {
        Season season = seasonRepository.findByStatus(Status.ACTIVE)
                .orElseThrow(() -> new NoActiveSeasonException("There is no active season", 404, "Not Found"));

        if(season.getEventDate() == null) {
            throw new FieldNullException("End Date is Null", 404, "Not Found");
        }

        // d-day code. no idea what ChronoUnit is. check later.
        int countdown = (int)ChronoUnit.DAYS.between(LocalDate.now(), season.getEventDate());

        return CountdownResponse.builder()
                .countdown(countdown)
                .eventDate(season.getEventDate())
                .build();
    }


    private SeasonResponse createSeasonResponse(Season season) {
        return SeasonResponse.builder()
                .id(season.getId())
                .name(season.getName())
                .status(season.getStatus())
                .startDate(season.getStartDate())
                .endDate(season.getEndDate())
                .eventDate(season.getEventDate())
                .build();
    }
}
