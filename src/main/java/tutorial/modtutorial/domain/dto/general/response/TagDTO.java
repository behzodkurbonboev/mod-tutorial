package tutorial.modtutorial.domain.dto.general.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TagDTO {
    private String id;
    private String name;
    private String subjectId;
    private int count;
}
