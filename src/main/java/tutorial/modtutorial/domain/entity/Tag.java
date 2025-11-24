package tutorial.modtutorial.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@Entity
@Table(name = "tag")
@AllArgsConstructor
@NoArgsConstructor
public class Tag extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "subject_id")
    private String subjectId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", updatable = false, insertable = false)
    private Subject subject;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) && Objects.equals(subjectId, tag.subjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subjectId);
    }
}
