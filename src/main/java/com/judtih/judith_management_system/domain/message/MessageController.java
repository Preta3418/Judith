package com.judtih.judith_management_system.domain.message;

import com.judtih.judith_management_system.domain.message.dto.MessageResult;
import com.judtih.judith_management_system.domain.message.dto.SendMessageRequest;
import com.judtih.judith_management_system.domain.message.entity.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageRepository messageRepository;



    @PostMapping("/send-message")
    public ResponseEntity<MessageResult> sendMessage (@RequestBody @Valid SendMessageRequest messageRequest) {

        MessageResult result = messageService.sendMessage(messageRequest.getMessageContent());
        return ResponseEntity.ok(result);

    }

    @GetMapping("/history-all")
    public Page<Message> getMessageHistory(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageDetail(@PathVariable Long id) {
        return messageService.getMessageDetail(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
