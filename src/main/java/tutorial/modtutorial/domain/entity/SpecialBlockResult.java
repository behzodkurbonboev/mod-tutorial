package tutorial.modtutorial.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@Entity
@Table(name = "special_block_result")
public class SpecialBlockResult extends BaseEntity {
    @Column(name = "block_id")
    private String blockId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "score")
    private int score;

    @Convert(converter = MapAttributeConverter.class)
    @Column(name = "key", columnDefinition = "TEXT")
    private Map<Integer, Integer> key;
}
