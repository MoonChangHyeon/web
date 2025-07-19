package com.fortify.web.service;

import com.fortify.web.domain.Project;
import com.fortify.web.dto.ProjectDto;
import com.fortify.web.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = new Project();
        project.setProjectName(projectDto.getProjectName());
        project.setRepoUrl(projectDto.getRepoUrl());
        project.setBranch(projectDto.getBranch());
        project.setCredentialsId(projectDto.getCredentialsId());
        project.setProjectSubdirectory(projectDto.getProjectSubdirectory());
        Project savedProject = projectRepository.save(project);
        return convertToDto(savedProject);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        project.setProjectName(projectDto.getProjectName());
        project.setRepoUrl(projectDto.getRepoUrl());
        project.setBranch(projectDto.getBranch());
        project.setCredentialsId(projectDto.getCredentialsId());
        project.setProjectSubdirectory(projectDto.getProjectSubdirectory());
        Project updatedProject = projectRepository.save(project);
        return convertToDto(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setRepoUrl(project.getRepoUrl());
        dto.setBranch(project.getBranch());
        dto.setCredentialsId(project.getCredentialsId());
        dto.setProjectSubdirectory(project.getProjectSubdirectory());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }
}
