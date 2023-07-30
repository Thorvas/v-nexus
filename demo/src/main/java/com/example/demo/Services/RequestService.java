package com.example.demo.Services;

import com.example.demo.Objects.VolunteerRequest;
import com.example.demo.Repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    public VolunteerRequest saveRequest(VolunteerRequest request) {

        return requestRepository.save(request);
    }

    public List<VolunteerRequest> searchAllRequests() {

        return requestRepository.findAll();
    }

    public Optional<VolunteerRequest> findRequest(Long id) {

        return requestRepository.findById(id);
    }

    public void deleteRequest(VolunteerRequest request) {

        requestRepository.delete(request);
    }
}
