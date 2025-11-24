package tutorial.modtutorial.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "solution")
public class Solution extends BaseEntity {
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "problem_id")
    private String problemId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "problem_id", insertable = false, updatable = false)
    private Problem problem;

    @Column(name = "like_count")
    private int likeCount;

    @ManyToMany
    @JoinTable(
            name = "solutions_users_liked",
            joinColumns = {@JoinColumn(name = "solution_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    private List<User> likedBy = new ArrayList<>();

    @Column(name = "visible")
    private boolean visible;
}
