package tutorial.modtutorial.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.Difficulty;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "special_block")
public class SpecialBlock extends BaseEntity {
    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "block_code")
    private String code;

    @Column(name = "subject_id")
    private String subjectId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "subject_id", insertable = false, updatable = false)
    private Subject subject;

    @Column(name = "author_id")
    private String authorId;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.EASY;

    @Column(name = "analysed")
    private boolean analysed;

    @Column(name = "score_sum")
    private long scoreSum;

    @Column(name = "solved_count")
    private int solvedCount;

    @Column(name = "visible")
    private boolean visible;
}
