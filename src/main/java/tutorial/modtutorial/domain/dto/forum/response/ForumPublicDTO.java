package tutorial.modtutorial.domain.dto.forum.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ForumPublicDTO {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String backgroundImageUrl;
    private String subjectId;
    private String style;
    private int numOfPosts;
}
