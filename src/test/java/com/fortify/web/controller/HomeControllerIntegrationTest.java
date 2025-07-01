package com.fortify.web.controller;

import com.fortify.web.domain.Message;
import com.fortify.web.domain.Role;
import com.fortify.web.repository.MessageRepository;
import com.fortify.web.repository.RoleRepository;
import com.fortify.web.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // 테스트를 위한 역할 추가
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        roleRepository.save(userRole);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        // 테스트를 위한 사용자 추가
        com.fortify.web.domain.User user = new com.fortify.web.domain.User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        com.fortify.web.domain.User admin = new com.fortify.web.domain.User();
        admin.setUsername("testadmin");
        admin.setPassword(passwordEncoder.encode("adminpass"));
        admin.setRoles(Set.of(adminRole, userRole));
        userRepository.save(admin);

        // 테스트를 위한 메시지 추가
        Message message = new Message();
        message.setText("Initial Message");
        messageRepository.save(message);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void homePage_shouldReturnMessagesForUser() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("messagesPage"));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void addMessage_shouldAllowAdmin() throws Exception {
        mockMvc.perform(post("/")
                        .param("text", "New Message from Admin")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void addMessage_shouldDenyUser() throws Exception {
        mockMvc.perform(post("/")
                        .param("text", "New Message from User")
                        .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void deleteMessage_shouldAllowAdmin() throws Exception {
        Message messageToDelete = new Message();
        messageToDelete.setText("To Be Deleted");
        messageRepository.save(messageToDelete);

        mockMvc.perform(post("/delete/{id}", messageToDelete.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteMessage_shouldDenyUser() throws Exception {
        Message messageToDelete = new Message();
        messageToDelete.setText("To Be Deleted by User");
        messageRepository.save(messageToDelete);

        mockMvc.perform(post("/delete/{id}", messageToDelete.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void updateMessage_shouldAllowAdmin() throws Exception {
        Message messageToUpdate = new Message();
        messageToUpdate.setText("Original Text");
        messageRepository.save(messageToUpdate);

        mockMvc.perform(post("/update/{id}", messageToUpdate.getId())
                        .param("text", "Updated Text")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateMessage_shouldDenyUser() throws Exception {
        Message messageToUpdate = new Message();
        messageToUpdate.setText("Original Text by User");
        messageRepository.save(messageToUpdate);

        mockMvc.perform(post("/update/{id}", messageToUpdate.getId())
                        .param("text", "Updated Text by User")
                        .with(csrf()))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    void loginPage_shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
