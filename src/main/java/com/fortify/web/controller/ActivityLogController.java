package com.fortify.web.controller;

import com.fortify.web.domain.ActivityLog;
import com.fortify.web.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/activity-logs")
@PreAuthorize("hasRole('ADMIN')")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public String listActivityLogs(@RequestParam(value = "search", required = false) String search,
                                   @PageableDefault(size = 10, sort = "timestamp") Pageable pageable, Model model) {
        Page<ActivityLog> activityLogsPage = activityLogService.findAllActivityLogs(pageable, search);
        model.addAttribute("activityLogsPage", activityLogsPage);
        model.addAttribute("search", search);

        int totalPages = activityLogsPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "activity-logs/list";
    }
}
