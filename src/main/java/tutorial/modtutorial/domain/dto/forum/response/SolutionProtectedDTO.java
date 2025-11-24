package tutorial.modtutorial.domain.dto.forum.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;


@Getter
@Setter
public class SolutionProtectedDTO {
    private String id;
    private String problemId;
    private String content;
    private boolean visible;
    private String createdDate;
    private String updatedDate;
    private AuthorDTO createdBy;
    private AuthorDTO updatedBy;
    private int likeCount;
    private boolean likedByUser;
}
