package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.dto.MessageResult;
import com.judtih.judith_management_system.domain.message.entity.Message;
import com.judtih.judith_management_system.domain.message.entity.MessageFailure;
import com.judtih.judith_management_system.domain.user.entity.User;
import com.judtih.judith_management_system.domain.user.enums.UserStatus;
import com.judtih.judith_management_system.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SnsClient snsClient;


    //https://docs.aws.amazon.com/ko_kr/sns/latest/dg/sms_sending-overview.html#sms_publish-to-phone
    public MessageResult sendMessage(String messageContent) {

        int successCount = 0;
        int failureCount = 0;

        List<User> gradUser = userRepository.findByStatus(UserStatus.INACTIVE);
        List<MessageFailure> failureList = new ArrayList<>();

        Message message = Message.builder().messageContent(messageContent).build();

        for (User user : gradUser) {
            try {

                String phoneNum = phoneNumberConverter(user);

                PublishRequest request = PublishRequest.builder()
                        .message(messageContent)
                        .phoneNumber(phoneNum)
                        .build();

                PublishResponse result = snsClient.publish(request);

                System.out.println(result);

                successCount++;

            } catch (SnsException e) {
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
                System.err.println(user.getName() + ": " + e.getMessage());
                failureCount++;
            }
        }

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




    private String phoneNumberConverter (User user) {
        String phoneNum = user.getPhoneNumber();

        if (phoneNum == null || phoneNum.isEmpty()) {
            throw new RuntimeException("no phone number found for user : " + user.getName());
        } else if (phoneNum.startsWith("+82")) {
            return phoneNum;
        }
        phoneNum = phoneNum.replace("-", "");
        phoneNum = phoneNum.substring(1);
        phoneNum = "+82" + phoneNum;

        return phoneNum;
}

}
