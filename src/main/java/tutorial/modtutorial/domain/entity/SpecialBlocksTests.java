package tutorial.modtutorial.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "special_blocks_tests")
public class SpecialBlocksTests extends BaseEntity {
    @Column(name = "block_id")
    private String blockId;

    @Column(name = "test_id")
    private String testId;

    @Column(name = "number")
    private int number; // test number in block

    @Column(name = "rule", length = 4)
    private String rule; // = shuffle([0, 1, 2, 3])
}
