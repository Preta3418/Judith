package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.config.AligoConfig;
import com.judtih.judith_management_system.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final AligoConfig aligoConfig;





}
