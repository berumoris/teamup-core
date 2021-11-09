package ru.team.up.core.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EVENT")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EVENT_NAME", nullable = false)
    private String eventName;

    @Column(name = "DESCRIPTION_EVENT", nullable = false)
    private String descriptionEvent;

    @Column(name = "PLACE_EVENT", nullable = false)
    private String placeEvent;

    @Column(name = "TIME_EVENT", nullable = false)
    private LocalDateTime timeEvent;

    @Column(name = "EVENT_UPDATE_DATE")
    private LocalDate eventUpdateDate;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @Column(name = "PARTICIPANTS_EVENT")
    private List<User> participantsEvent;

    @OneToOne(optional=false, cascade=CascadeType.MERGE, fetch=FetchType.LAZY)
    @JoinColumn(name = "EVENT_TYPE_ID")
    private EventType eventType;

    @OneToOne(optional=false,cascade=CascadeType.MERGE, fetch=FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User authorId;

    @OneToMany(cascade=CascadeType.MERGE, fetch=FetchType.LAZY
            , mappedBy = "event")
    @Column(name = "INTERESTS_EVENT", nullable = false)
    private Set<Interests> eventInterests;

   @OneToOne(cascade=CascadeType.MERGE, fetch=FetchType.LAZY)
   @JoinColumn(name = "STATUS_ID")
    private Status status;

}
