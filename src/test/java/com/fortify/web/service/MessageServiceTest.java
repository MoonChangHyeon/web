package com.fortify.web.service;

import com.fortify.web.domain.Message;
import com.fortify.web.dto.MessageDto;
import com.fortify.web.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Message message1;
    private Message message2;
    private MessageDto messageDto1;
    private MessageDto messageDto2;

    @BeforeEach
    void setUp() {
        message1 = new Message();
        message1.setId(1L);
        message1.setText("Test Message 1");

        message2 = new Message();
        message2.setId(2L);
        message2.setText("Test Message 2");

        messageDto1 = new MessageDto();
        messageDto1.setId(1L);
        messageDto1.setText("Test Message 1");

        messageDto2 = new MessageDto();
        messageDto2.setId(2L);
        messageDto2.setText("Test Message 2");

    }

    @Test
    void findAllMessages_shouldReturnAllMessagesPaged() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message1, message2), pageable, 2);
        when(messageRepository.findAll(pageable)).thenReturn(messagePage);

        Page<MessageDto> result = messageService.findAllMessages(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getText()).isEqualTo("Test Message 1");
        verify(messageRepository, times(1)).findAll(pageable);
    }

    @Test
    void searchMessages_shouldReturnFilteredMessagesPaged() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message1), pageable, 1);
        when(messageRepository.findByTextContainingIgnoreCase("Test Message 1", pageable)).thenReturn(messagePage);

        Page<MessageDto> result = messageService.searchMessages("Test Message 1", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getText()).isEqualTo("Test Message 1");
        verify(messageRepository, times(1)).findByTextContainingIgnoreCase("Test Message 1", pageable);
    }

    @Test
    void findMessageById_shouldReturnMessageWhenFound() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message1));

        MessageDto result = messageService.findMessageById(1L);

        assertThat(result.getText()).isEqualTo("Test Message 1");
        verify(messageRepository, times(1)).findById(1L);
    }

    @Test
    void findMessageById_shouldThrowExceptionWhenNotFound() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> messageService.findMessageById(99L));
        verify(messageRepository, times(1)).findById(99L);
    }

    @Test
    void saveMessage_shouldSaveNewMessage() {
        MessageDto newMessageDto = new MessageDto();
        newMessageDto.setText("New Message");

        // save 메서드가 호출될 때 어떤 Message 객체든 반환하도록 스텁
        when(messageRepository.save(any(Message.class))).thenReturn(message1);

        messageService.saveMessage(newMessageDto);

        // save 메서드가 정확히 한 번 호출되었는지 확인
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void deleteMessage_shouldDeleteMessage() {
        messageService.deleteMessage(1L);

        verify(messageRepository, times(1)).deleteById(1L);
    }
}