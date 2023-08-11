package com.example.demo.Request;

import com.example.demo.Project.Project;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for request operations
 */
@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Searches for requests
     *
     * @return List of found requests
     */
    public List<VolunteerRequest> searchAllRequests() {

        return requestRepository.findAll();
    }

    /**
     * Searches for specific request based on id parameter
     *
     * @param id Id of searched request
     * @return Optional containing result of search
     */
    public Optional<VolunteerRequest> findRequest(Long id) {

        return requestRepository.findById(id);
    }

    /**
     * Deletes requests
     *
     * @param request Deleted request
     */
    public void deleteRequest(VolunteerRequest request) {

        requestRepository.delete(request);
    }

    /**
     * Checks whether volunteer is sending a request
     *
     * @param request   Inspected request
     * @param volunteer Volunteer that is checked to be a sender of request
     * @return Boolean value containing result of check
     */
    public boolean isVolunteerSender(VolunteerRequest request, Volunteer volunteer) {

        return request.getRequestSender().getId().equals(volunteer.getId());
    }

    /**
     * Checks whether volunteer is receiving a request
     *
     * @param request   Inspected request
     * @param volunteer Volunteer that is checked to be a receiver of request
     * @return Boolean value containing result of check
     */
    public boolean isVolunteerReceiver(VolunteerRequest request, Volunteer volunteer) {

        return request.getRequestReceiver().getId().equals(volunteer.getId());
    }

    /**
     * Checks whether volunteer is receiving OR sending a request
     *
     * @param request   Inspected request
     * @param volunteer Volunteer that is checked to be a receiver or sender of request
     * @return Boolean value containing result of check
     */
    public boolean isVolunteerSenderOrReceiver(VolunteerRequest request, Volunteer volunteer) {

        return isVolunteerReceiver(request, volunteer) || isVolunteerSender(request, volunteer);
    }

    /**
     * Checks whether request has a pending status
     *
     * @param request Inspected request
     * @return Boolean value containing result of check
     */
    public boolean hasPendingStatus(VolunteerRequest request) {

        return request.getStatus() == RequestStatus.PENDING;
    }

    /**
     * Creates request
     *
     * @param projectOwner Volunteer object assumed to receive request
     * @param loggedUser   Volunteer object assumed to send request
     * @param foundProject Project that is regarded in request
     * @return Created request
     */
    public VolunteerRequest createRequest(Volunteer projectOwner, Volunteer loggedUser, Project foundProject) {

        VolunteerRequest newRequest = VolunteerRequest.builder().requestReceiver(projectOwner).requestSender(loggedUser).requestedProject(foundProject).status(RequestStatus.PENDING).build();

        requestRepository.save(newRequest);

        return newRequest;
    }

    /**
     * Declines request
     *
     * @param request Request that will be declined
     * @return Optional value that contains result of operation
     */
    public Optional<VolunteerRequest> declineRequest(VolunteerRequest request) {

        if (this.hasPendingStatus(request)) {

            request.setStatus(RequestStatus.DECLINED);
            requestRepository.save(request);

            return Optional.of(request);
        } else {

            return Optional.empty();
        }
    }

    /**
     * Accepts request
     *
     * @param request   Request that will be accepted
     * @param volunteer Volunteer that will be added to project
     * @param project   Project that will be modified
     * @return Optional value that contains result of operation
     */
    public Optional<VolunteerRequest> acceptRequest(VolunteerRequest request, Volunteer volunteer, Project project) {

        if (this.hasPendingStatus(request)) {

            project.addVolunteerToProject(volunteer);
            request.setStatus(RequestStatus.ACCEPTED);

            projectRepository.save(project);
            requestRepository.save(request);

            return Optional.of(request);
        } else {

            return Optional.empty();
        }
    }
}
