package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.dto.MessageResult;
import com.judtih.judith_management_system.domain.message.entity.Message;
import com.judtih.judith_management_system.domain.message.entity.MessageFailure;
import com.judtih.judith_management_system.domain.user.User;
import com.judtih.judith_management_system.domain.user.UserService;

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
    private final UserService userService;
    private final SnsClient snsClient;


    //https://docs.aws.amazon.com/ko_kr/sns/latest/dg/sms_sending-overview.html#sms_publish-to-phone
    public MessageResult sendMessage(String messageContent) {

        int successCount = 0;
        int failureCount = 0;

        List<User> gradUser = userService.getGraduatedUsers();
        List<MessageFailure> failureList = new ArrayList<>();

        Message message = new Message();
        message.setMessageContent(messageContent);

        for (User user : gradUser) {
            try {

                String phoneNum = phoneNumberConverter(user);

                PublishRequest request = PublishRequest.builder()
                        .message(messageContent)
                        .phoneNumber(phoneNum)
                        .build();

                PublishResponse result = snsClient.publish(request);

                System.out.println(result); // i have no idea what this is but i want to see.

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

        message.setTotalSent(successCount + failureCount);
        message.setFailedAttempt(failureCount);

        message.setFailures(failureList);

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
