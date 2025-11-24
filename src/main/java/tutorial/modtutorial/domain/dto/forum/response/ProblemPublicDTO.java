package tutorial.modtutorial.domain.dto.forum.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.List;


@Getter
@Setter
public class ProblemPublicDTO {
    private String id;
    private String title;
    private String source;
    private String date;
    private AuthorDTO author;
    private String content;
    private String shareUrl;
    private List<String> tags;
    private int solutionCount;
    private int likeCount;
    private int saveCount;
    private int seenCount;
    private int shareCount;
    private boolean likedByUser;
    private boolean savedByUser;
    private boolean seenByUser;
    private AccessType accessType;
}
