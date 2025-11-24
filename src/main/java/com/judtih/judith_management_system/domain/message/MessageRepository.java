package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.failures WHERE m.id = :id")
    Optional<Message> findByIdWithFailures(@Param("id") Long id);
}
