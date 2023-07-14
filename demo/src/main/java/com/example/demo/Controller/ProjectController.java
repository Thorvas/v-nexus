package com.example.demo.Controller;


import com.example.demo.DummyObject.Opinion;
import com.example.demo.DummyObject.Project;
import com.example.demo.DummyObject.Volunteer;
import com.example.demo.Services.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Project> getProject(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));

        return new ResponseEntity<>(foundProject, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}/volunteers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Volunteer>> getVolunteers(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));

        return new ResponseEntity<>(foundProject.getProjectVolunteers(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Volunteer> getOwner(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));

        return new ResponseEntity<>(foundProject.getOwnerVolunteer(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/skills", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getSkills(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));

        return new ResponseEntity<>(foundProject.getRequiredSkills(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getTasks(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));

        return new ResponseEntity<>(foundProject.getTasks(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/opinions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Opinion>> getOpinions(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Requested project could not  be found."));

        return new ResponseEntity<>(foundProject.getProjectOpinions(), HttpStatus.OK);
    }

    @GetMapping(value = "/location/{location}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Project>> getProjectsWithLocation(@PathVariable String location) {

        List<Project> foundProjects = projectService.searchProjectsWithLocation(location);

        return new ResponseEntity<>(foundProjects, HttpStatus.OK);
    }

    @GetMapping(value = "/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Project>> getProjectsWithDate(@PathVariable LocalDate date) {

        List<Project> foundProjects = projectService.searchProjectsWithDate(date);

        return new ResponseEntity<>(foundProjects, HttpStatus.OK);
    }
}
