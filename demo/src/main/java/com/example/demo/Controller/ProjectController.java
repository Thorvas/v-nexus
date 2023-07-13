package com.example.demo.Controller;


import com.example.demo.DummyObject.Project;
import com.example.demo.Services.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "/project/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Project> getProject(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));

        return new ResponseEntity<>(foundProject, HttpStatus.OK);

    }

    @GetMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Project>> getProjectsWithDate(@RequestParam(name = "date", required = false) Optional<LocalDate> date,
                                                     @RequestParam(name = "status", required = false) Optional<Boolean> status) {

        List<Project> foundProjects = null;

        if (date.isPresent()) {
           foundProjects = projectService.searchProjectsWithDate(date.get());
        }
        else if (status.isPresent()) {
            foundProjects = projectService.searchProjectsWithStatus(status.get());
        }

        return new ResponseEntity<>(foundProjects, HttpStatus.OK);

    }
}
