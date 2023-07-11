package com.example.demo.Services;

import com.example.demo.DummyObject.Volunteer;
import com.example.demo.Repositories.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {

    @Autowired
    private VolunteerRepository repository;

    public List<Volunteer> searchVolunteers() {

        return repository.findAll();
    }

    public Optional<Volunteer> findVolunteer(Long id) {

        return repository.findById(id);
    }

    public void saveVolunteer(Volunteer volunteer) {
        repository.save(volunteer);
    }

    public void deleteVolunteer(Volunteer volunteer) {
        repository.delete(volunteer);
    }
}
