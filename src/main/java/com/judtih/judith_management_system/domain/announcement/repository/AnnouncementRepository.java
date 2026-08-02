package com.judtih.judith_management_system.domain.announcement.repository;

import com.judtih.judith_management_system.domain.announcement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findBySeasonIdOrderByIsPinnedDescCreatedAtDesc(Long seasonId);
}
