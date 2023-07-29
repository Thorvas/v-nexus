package com.example.demo.Objects;

import jakarta.persistence.*;
import lombok.*;

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
}
