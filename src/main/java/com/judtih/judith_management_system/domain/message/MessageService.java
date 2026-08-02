package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.dto.MessageResult;
import com.judtih.judith_management_system.domain.message.entity.Message;
import com.judtih.judith_management_system.domain.message.entity.MessageFailure;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Handles bulk SMS delivery to INACTIVE (alumni) users via AWS SNS and persists the audit record. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SnsClient snsClient;


    // Sends to INACTIVE users only — those are alumni targeted for recruitment or announcements.
    // See: https://docs.aws.amazon.com/ko_kr/sns/latest/dg/sms_sending-overview.html#sms_publish-to-phone
    public MessageResult sendMessage(String messageContent) {

        int successCount = 0;
        int failureCount = 0;

        List<User> gradUser = userRepository.findByStatus(UserStatus.INACTIVE);
        List<MessageFailure> failureList = new ArrayList<>();

        log.info("sendMessage: starting bulk SMS to {} alumni", gradUser.size());
        Message message = Message.builder().messageContent(messageContent).build();

        for (User user : gradUser) {
            try {

                String phoneNum = phoneNumberConverter(user);

                PublishRequest request = PublishRequest.builder()
                        .message(messageContent)
                        .phoneNumber(phoneNum)
                        .build();

                PublishResponse result = snsClient.publish(request);

                log.info("sendMessage: published to {} ({}), messageId={}", user.getName(), phoneNum, result.messageId());
                successCount++;

            } catch (SnsException e) {
                log.warn("sendMessage: SNS error for {} ({}): {}", user.getName(), user.getPhoneNumber(), e.awsErrorDetails().errorMessage());
                MessageFailure failure = MessageFailure.builder()
                        .userId(user.getId())
                        .message(message)
                        .userName(user.getName())
                        .phoneNumber(user.getPhoneNumber())
                        .errorMessage(e.awsErrorDetails().errorMessage())
                        .build();

                failureList.add(failure);
                failureCount++;

            } catch (RuntimeException e) {
                log.error("sendMessage: unexpected error for {}: {}", user.getName(), e.getMessage());
                failureCount++;
            }
        }
        log.info("sendMessage: done — success={}, failure={}", successCount, failureCount);

        message.updateMessage(null, successCount + failureCount, failureCount, failureList);

        messageRepository.save(message);

        return MessageResult.builder()
                .failureCount(failureCount)
                .successCount(successCount)
                .totalAttempted(successCount+failureCount)
                .failures(failureList)
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<Message> getMessageDetail(long id) {
        return messageRepository.findByIdWithFailures(id);
    }




    /** Converts a Korean domestic number (010-xxxx-xxxx or 010xxxxxxxx) to E.164 format (+82). */
    private String phoneNumberConverter (User user) {
        String phoneNum = user.getPhoneNumber();

        if (phoneNum == null || phoneNum.isEmpty()) {
            throw new RuntimeException("no phone number found for user : " + user.getName());
        } else if (phoneNum.startsWith("+82")) {
            return phoneNum;
        }
        phoneNum = phoneNum.replace("-", "");
        phoneNum = phoneNum.substring(1); // drop leading 0
        phoneNum = "+82" + phoneNum;

        return phoneNum;
}

}
