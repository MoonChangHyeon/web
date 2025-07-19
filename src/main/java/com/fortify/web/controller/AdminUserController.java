package com.fortify.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fortify.web.domain.Role;
import com.fortify.web.domain.User;
import com.fortify.web.dto.UserCreateDto;
import com.fortify.web.dto.UserEditDto;
import com.fortify.web.repository.RoleRepository;
import com.fortify.web.service.AdminUserService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    private final AdminUserService adminUserService;
    private final RoleRepository roleRepository;

    public AdminUserController(AdminUserService adminUserService, RoleRepository roleRepository) {
        this.adminUserService = adminUserService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String listUsers(@RequestParam(value = "search", required = false) String search,
                            @PageableDefault(size = 10) Pageable pageable, Model model) {
        logger.info("Accessing /admin/users");
        Page<User> usersPage = adminUserService.findAllUsers(pageable, search);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("search", search);

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserCreateDto());
        }
        model.addAttribute("allRoles", roleRepository.findAll());

        int totalPages = usersPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/list";
    }

    

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("user") UserCreateDto userCreateDto,
                             BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            redirectAttributes.addFlashAttribute("user", userCreateDto);
            redirectAttributes.addFlashAttribute("showCreateUserModal", true);
            return "redirect:/admin/users";
        }
        adminUserService.createUser(userCreateDto);
        redirectAttributes.addFlashAttribute("message", "새 유저가 성공적으로 생성되었습니다.");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = adminUserService.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + id));
        UserEditDto userEditDto = new UserEditDto();
        userEditDto.setId(user.getId());
        userEditDto.setUsername(user.getUsername());
        userEditDto.setEmail(user.getEmail());
        userEditDto.setStatus(user.getStatus());
        userEditDto.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));

        model.addAttribute("userEditDto", userEditDto);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("userEditDto") UserEditDto userEditDto,
                             BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/edit";
        }
        userEditDto.setId(id); // URL 경로의 ID를 DTO에 설정
        adminUserService.saveUser(userEditDto);
        redirectAttributes.addFlashAttribute("message", "유저 정보가 성공적으로 업데이트되었습니다.");
        return "redirect:/admin/users/{id}";
    }

    @GetMapping("/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        User user = adminUserService.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + id));
        model.addAttribute("user", user);
        return "users/detail";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminUserService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "유저가 성공적으로 삭제되었습니다.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User updatedUser = adminUserService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("message", "유저 상태가 " + updatedUser.getStatus() + "로 변경되었습니다.");
        return "redirect:/admin/users/{id}";
    }
}