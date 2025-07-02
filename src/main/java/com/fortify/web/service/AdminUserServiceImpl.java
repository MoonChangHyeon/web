package com.fortify.web.service;

import com.fortify.web.domain.Role;
import com.fortify.web.domain.User;
import com.fortify.web.dto.UserEditDto;
import com.fortify.web.repository.RoleRepository;
import com.fortify.web.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable, String search) {
        if (search != null && !search.isEmpty()) {
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User saveUser(UserEditDto userEditDto) {
        User user;
        if (userEditDto.getId() != null) {
            user = userRepository.findById(userEditDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + userEditDto.getId()));
        } else {
            user = new User();
            user.setPassword(passwordEncoder.encode("password")); // 새 유저 생성 시 기본 비밀번호 설정
        }

        user.setUsername(userEditDto.getUsername());
        user.setEmail(userEditDto.getEmail());
        user.setStatus(userEditDto.getStatus());

        if (userEditDto.getRoleIds() != null) {
            Set<Role> roles = userEditDto.getRoleIds().stream()
                    .map(roleRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            user.setRoles(new HashSet<>()); // 역할이 없으면 빈 Set으로 설정
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + id));
        user.setStatus(user.getStatus().equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
        return userRepository.save(user);
    }
}
