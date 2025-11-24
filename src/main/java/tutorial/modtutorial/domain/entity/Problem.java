package tutorial.modtutorial.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.AccessType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "problem")
public class Problem extends BaseEntity {
    @Column(name = "forum_id")
    private String forumId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "forum_id", insertable = false, updatable = false)
    private Forum forum;

    @ManyToMany
    @JoinTable(
            name = "problems_tags",
            joinColumns = {@JoinColumn(name = "problem_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")}
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "solution_count")
    private int solutionCount;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "source")
    private String source;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "share_url")
    private String shareUrl;

    @Column(name = "access_type")
    @Enumerated(EnumType.STRING)
    private AccessType accessType = AccessType.BASIC;

    @ManyToMany
    @JoinTable(
            name = "problems_users_liked",
            joinColumns = {@JoinColumn(name = "problem_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    private List<User> likedBy = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "problems_users_saved",
            joinColumns = {@JoinColumn(name = "problem_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    private List<User> savedBy = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "problems_users_seen",
            joinColumns = {@JoinColumn(name = "problem_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    private List<User> seenBy = new ArrayList<>();

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "save_count")
    private int saveCount;

    @Column(name = "seen_count")
    private int seenCount;

    @Column(name = "share_count")
    private int shareCount;

    @Column(name = "visible")
    private boolean visible;

    @Column(name = "sort_date")
    private LocalDateTime sortDate;
}
