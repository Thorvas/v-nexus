package com.example.demo.Services;

import com.example.demo.DummyObject.Project;
import com.example.demo.Repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Optional<Project> findProject(Long id) {

        return projectRepository.findById(id);
    }

    public List<Project> searchProjectsWithDate(LocalDate date) {

        return projectRepository.findWithDate(date);
    }

    public List<Project> searchAllProjects() {

        return projectRepository.findAll();
    }

    public List<Project> searchProjectsWithLocation(String location) {

        return projectRepository.findWithLocation(location);
    }

    public List<Project> searchProjectsWithStatus(boolean status) {

        return projectRepository.findWithStatus(status);
    }
}
