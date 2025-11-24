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


@Getter
@Setter
@Entity
@Table(name = "special_test")
public class SpecialTest extends BaseEntity {
    @Column(name = "subject_id")
    private String subjectId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "subject_id", insertable = false, updatable = false)
    private Subject subject;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.EASY;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @Column(name = "true_answer", columnDefinition = "TEXT")
    private String trueAnswer;

    @Column(name = "false_answer1", columnDefinition = "TEXT")
    private String falseAnswer1;

    @Column(name = "false_answer2", columnDefinition = "TEXT")
    private String falseAnswer2;

    @Column(name = "false_answer3", columnDefinition = "TEXT")
    private String falseAnswer3;

    @Column(name = "solution", columnDefinition = "TEXT")
    private String solution;

    @Column(name = "analysed")
    private boolean analysed;

    @Column(name = "usage_count")
    private int usageCount;
}
