package tutorial.modtutorial.domain.dto.forum.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;


@Getter
@Setter
public class SolutionPublicDTO {
    private String id;
    private String date;
    private String content;
    private AuthorDTO author;
    private int likeCount;
    private boolean likedByUser;
}
