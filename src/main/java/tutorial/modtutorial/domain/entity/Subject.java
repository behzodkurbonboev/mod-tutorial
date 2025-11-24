package tutorial.modtutorial.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "subject")
public class Subject extends BaseEntity {
    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.PERSIST)
    private List<Tag> tags = new ArrayList<>();
}
