package com.fortify.web.service;

import com.fortify.web.domain.ActivityLog;
import com.fortify.web.domain.User;
import com.fortify.web.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void logUserActivity(String action, User targetUser, String details) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityType("User");
        log.setEntityId(targetUser.getId());
        log.setDetails(details);

        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.setActor(authentication.getName()); // 로그인한 사용자명
        } else {
            log.setActor("SYSTEM"); // 로그인하지 않은 경우 (예: 초기 사용자 생성)
        }
        activityLogRepository.save(log);
    }

    public Page<ActivityLog> findAllActivityLogs(Pageable pageable, String search) {
        if (search != null && !search.isEmpty()) {
            return activityLogRepository.findByActorContainingIgnoreCaseOrActionContainingIgnoreCaseOrEntityTypeContainingIgnoreCaseOrDetailsContainingIgnoreCase(
                    search, search, search, search, pageable);
        } else {
            return activityLogRepository.findAll(pageable);
        }
    }
}
