package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.user.User;
import com.judtih.judith_management_system.domain.user.UserService;
import com.judtih.judith_management_system.shared.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.SplittableRandom;

@Service
@RequiredArgsConstructor
public class MessageService {

    AwsConfig awsConfig;
    UserService userService;


protected String phoneNumberConverter (User user) {
    String phoneNum = user.getPhoneNumber();

    if (phoneNum == null) {
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
