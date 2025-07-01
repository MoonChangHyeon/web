package com.fortify.web.controller;

import com.fortify.web.dto.MessageDto;
import com.fortify.web.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Controller
public class HomeController {

    private final MessageService messageService;

    public HomeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "search", required = false) String search,
                       @PageableDefault(size = 5) Pageable pageable, Model model) {
        Page<MessageDto> messagesPage;
        if (search != null && !search.isEmpty()) {
            messagesPage = messageService.searchMessages(search, pageable);
        } else {
            messagesPage = messageService.findAllMessages(pageable);
        }
        model.addAttribute("messagesPage", messagesPage);
        model.addAttribute("newMessage", new MessageDto()); // 폼을 위한 빈 객체
        model.addAttribute("search", search); // 검색어 유지

        int totalPages = messagesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "home";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public String addMessage(@Valid @ModelAttribute("newMessage") MessageDto newMessage, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // 오류 발생 시 현재 페이지의 메시지 목록을 다시 로드
            Page<MessageDto> messagesPage = messageService.findAllMessages(Pageable.unpaged()); // 페이징 없이 모든 메시지 로드
            model.addAttribute("messagesPage", messagesPage);
            return "home";
        }
        messageService.saveMessage(newMessage);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        MessageDto message = messageService.findMessageById(id);
        model.addAttribute("message", message);
        return "updateForm";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateMessage(@PathVariable Long id, @Valid @ModelAttribute("message") MessageDto messageDto, BindingResult result) {
        if (result.hasErrors()) {
            return "updateForm";
        }
        messageDto.setId(id); // 경로 변수에서 받은 id를 messageDto 객체에 설정
        messageService.saveMessage(messageDto);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/fortify/scan/direct")
    public String runFortifyScanDirectly(Model model) {
        StringBuilder output = new StringBuilder();
        try {
            // gradlew 실행 경로를 'web' 디렉토리로 설정
            ProcessBuilder processBuilder = new ProcessBuilder("./gradlew", "fortifyScan", "-PbuildId=web_app_scan");
            processBuilder.directory(new java.io.File("web")); // Set working directory to 'web'
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            output.append("Exited with error code : ").append(exitCode).append("\n");

            if (exitCode == 0) {
                model.addAttribute("message", "Fortify SCA scan initiated successfully! Check console for details.");
            } else {
                model.addAttribute("message", "Fortify SCA scan failed! Error: " + output.toString());
            }

        } catch (IOException | InterruptedException e) {
            output.append("Error executing Fortify scan: ").append(e.getMessage()).append("\n");
            model.addAttribute("message", "Error executing Fortify SCA scan: " + e.getMessage());
            e.printStackTrace();
        }
        return "home"; // 스캔 결과 메시지를 home.html에 표시
    }
}
