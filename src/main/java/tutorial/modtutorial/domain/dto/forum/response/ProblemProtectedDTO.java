package tutorial.modtutorial.domain.dto.forum.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.dto.general.response.TagDTO;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.List;


@Getter
@Setter
public class ProblemProtectedDTO {
    private String id;
    private String forumId;
    private String title;
    private String source;
    private String content;
    private String shareUrl;
    private List<TagDTO> tags;
    private boolean visible;
    private String createdDate;
    private String updatedDate;
    private AuthorDTO createdBy;
    private AuthorDTO updatedBy;
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
