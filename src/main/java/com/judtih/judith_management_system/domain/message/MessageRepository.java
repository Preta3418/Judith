package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

}
