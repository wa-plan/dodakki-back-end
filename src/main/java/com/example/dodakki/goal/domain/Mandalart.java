package com.example.dodakki.goal.domain;

import com.example.dodakki.Photo.domain.Photo;
import com.example.dodakki.user.domain.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mandalart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length=10)
    private String color;

    @Column(nullable = false, length = 80)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Bookmark bookmark;

    @OneToMany(mappedBy = "mandalart", cascade = CascadeType.ALL)
    private List<SecondGoal> secondGoalList = new ArrayList<SecondGoal>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_date_id")
    private GoalDate goalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "mandalart", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Photo> photoList = new ArrayList<>();

    @Column(nullable = false)
    private Long attainmentCount;

    @Column(nullable = false)
    private Long goalCount;

    @OneToMany(mappedBy = "mandalart", cascade = CascadeType.ALL)
    private List<Goal> goals;

    public Mandalart(final User user, final String name, final String description, final String color, final Long attainmentCount, final Long goalCount) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.color = color;
        this.status = Status.IN_PROGRESS;
        this.bookmark = Bookmark.UNBOOKMARK;
        this.attainmentCount = attainmentCount;
        this.goalCount = goalCount;
    }

    public void addPhoto(final Photo photo){
        photoList.add(photo);
        photo.setMandalart(this);
    }
}
