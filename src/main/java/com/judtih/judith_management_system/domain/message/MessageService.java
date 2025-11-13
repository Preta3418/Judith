package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.user.UserService;
import com.judtih.judith_management_system.shared.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    AwsConfig awsConfig;
    UserService userService;




}
