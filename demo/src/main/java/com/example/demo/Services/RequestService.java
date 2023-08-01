package com.example.demo.Services;

import com.example.demo.Objects.Project;
import com.example.demo.Objects.RequestStatus;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Objects.VolunteerRequest;
import com.example.demo.Repositories.ProjectRepository;
import com.example.demo.Repositories.RequestRepository;
import com.example.demo.Repositories.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;


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

    public boolean isVolunteerSender(VolunteerRequest request, Volunteer volunteer) {

        return request.getRequestSender().getId().equals(volunteer.getId());
    }

    public boolean isVolunteerReceiver(VolunteerRequest request, Volunteer volunteer) {

        return request.getRequestReceiver().getId().equals(volunteer.getId());
    }

    public boolean isVolunteerSenderOrReceiver(VolunteerRequest request, Volunteer volunteer) {

        return isVolunteerReceiver(request, volunteer) || isVolunteerSender(request, volunteer);
    }

    public boolean hasPendingStatus(VolunteerRequest request) {

        return request.getStatus() == RequestStatus.PENDING;
    }

    public boolean hasAcceptedStatus(VolunteerRequest request) {

        return request.getStatus() == RequestStatus.ACCEPTED;
    }

    public boolean hasDeclinedStatus(VolunteerRequest request) {

        return request.getStatus() == RequestStatus.DECLINED;
    }

    public VolunteerRequest createRequest(Volunteer projectOwner, Volunteer loggedUser, Project foundProject) {

        VolunteerRequest newRequest = VolunteerRequest.builder()
                .requestReceiver(projectOwner)
                .requestSender(loggedUser)
                .requestedProject(foundProject)
                .status(RequestStatus.PENDING)
                .build();

        requestRepository.save(newRequest);

        return newRequest;
    }

    public void declineRequest(VolunteerRequest request, Volunteer volunteer, Project project) {

        project.removeVolunteerFromProject(volunteer);
        request.setStatus(RequestStatus.DECLINED);

        projectRepository.save(project);
        requestRepository.save(request);
    }

    public void acceptRequest(VolunteerRequest request, Volunteer volunteer, Project project) {

        project.addVolunteerToProject(volunteer);
        request.setStatus(RequestStatus.ACCEPTED);

        projectRepository.save(project);
        requestRepository.save(request);
    }
}
