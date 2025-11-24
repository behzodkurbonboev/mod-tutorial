package tutorial.modtutorial.domain.dto.forum.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ForumDTO {
    private String name;
    private String description;
    private String imageUrl;
    private String backgroundImageUrl;
    private String subjectId;
}
