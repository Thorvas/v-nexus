package com.example.demo.Services;

import com.example.demo.Objects.VolunteerRequest;
import com.example.demo.Repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    public VolunteerRequest saveRequest(VolunteerRequest request) {

        return requestRepository.save(request);
    }
}
