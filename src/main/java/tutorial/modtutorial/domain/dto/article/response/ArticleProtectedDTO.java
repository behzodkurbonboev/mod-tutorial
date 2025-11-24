package tutorial.modtutorial.domain.dto.article.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.domain.dto.general.response.TagDTO;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.List;


@Getter
@Setter
public class ArticleProtectedDTO {
    private String id;
    private SubjectDTO subject;
    private List<TagDTO> tags;
    private String title;
    private String description;
    private String content;
    private boolean visible;
    private String createdDate;
    private String updatedDate;
    private AuthorDTO createdBy;
    private AuthorDTO updatedBy;
    private int likeCount;
    private int saveCount;
    private int seenCount;
    private int shareCount;
    private String shareUrl;
    private boolean likedByUser;
    private boolean savedByUser;
    private boolean seenByUser;
    private AccessType accessType;
}
