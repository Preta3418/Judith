package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.dto.MessageResult;
import com.judtih.judith_management_system.domain.message.dto.SendMessageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    public ResponseEntity<MessageResult> sendMessage (@RequestBody @Valid SendMessageRequest messageRequest) {

        MessageResult result = messageService.sendMessage(messageRequest.getMessageContent())
;
        return ResponseEntity.ok(result);

    }


}
