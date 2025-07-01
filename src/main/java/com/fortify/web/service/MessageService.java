package com.fortify.web.service;

import jakarta.annotation.PostConstruct;
import com.fortify.web.domain.Message;
import com.fortify.web.dto.MessageDto;
import com.fortify.web.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @PostConstruct
    public void init() {
        // 애플리케이션 시작 시 기본 메시지 저장
        if (messageRepository.count() == 0) {
            Message message = new Message();
            message.setText("Database Message!");
            messageRepository.save(message);
        }
    }

    public Page<MessageDto> findAllMessages(Pageable pageable) {
        Page<Message> messagesPage = messageRepository.findAll(pageable);
        List<MessageDto> messageDtos = messagesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(messageDtos, pageable, messagesPage.getTotalElements());
    }

    public Page<MessageDto> searchMessages(String searchText, Pageable pageable) {
        Page<Message> messagesPage = messageRepository.findByTextContainingIgnoreCase(searchText, pageable);
        List<MessageDto> messageDtos = messagesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(messageDtos, pageable, messagesPage.getTotalElements());
    }

    public MessageDto findMessageById(Long id) {
        return messageRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));
    }

    public void saveMessage(MessageDto messageDto) {
        Message message = convertToEntity(messageDto);
        messageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    private MessageDto convertToDto(Message message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setText(message.getText());
        return messageDto;
    }

    private Message convertToEntity(MessageDto messageDto) {
        Message message = new Message();
        message.setId(messageDto.getId()); // ID는 업데이트 시에만 사용
        message.setText(messageDto.getText());
        return message;
    }
}
