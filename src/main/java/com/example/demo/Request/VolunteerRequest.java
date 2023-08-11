package com.example.demo.Request;

import com.example.demo.Project.Project;
import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.*;

/**
 * Class representing request for joining to a specific project
 *
 * @author Thorvas
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "volunteer_requests")
public class VolunteerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_volunteer")
    private Volunteer requestSender;

    @ManyToOne
    @JoinColumn(name = "receiver_volunteer")
    private Volunteer requestReceiver;

    @ManyToOne
    @JoinColumn(name = "requested_project")
    private Project requestedProject;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus status;
}
