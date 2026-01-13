package com.judtih.judith_management_system.domain.season;

import com.judtih.judith_management_system.domain.season.dto.CountdownResponse;
import com.judtih.judith_management_system.domain.season.dto.SeasonRequest;
import com.judtih.judith_management_system.domain.season.dto.SeasonResponse;
import com.judtih.judith_management_system.domain.season.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository seasonRepository;


    @Transactional
    public SeasonResponse createSeason(String name) {

        if(seasonRepository.existsByStatusNot(Status.CLOSED)) {
            throw new AlreadyActiveSeasonException("Cannot create season when one is already active", 409, "Conflict" );
        }

        Season season = new Season(name, LocalDate.now());
        seasonRepository.save(season);

        return createSeasonResponse(season);

    }

    @Transactional
    public SeasonResponse activateSeason(long id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new NoSeasonFoundException("no season was found with id: " + id, 404, "Not Found"));

        if (season.getStatus() == Status.CLOSED) throw new SeasonClosedException("This season is a closed season", 409, "Conflict");
        else if (season.getStatus() == Status.ACTIVE) throw new AlreadyActiveSeasonException("This season is already active", 409, "Conflict");

        season.activateSeason();

        return createSeasonResponse(season);
    }

    public SeasonResponse getCurrentSeason() {
        Season season = seasonRepository.findByStatus(Status.ACTIVE)
                .orElseThrow(() -> new NoActiveSeasonException("there is no active season", 404, "Not Found"));

        return createSeasonResponse(season);
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

        season.updateSeason(request.getName(), request.getStartDate(), request.getEventDate());

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
