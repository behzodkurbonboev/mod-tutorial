package tutorial.modtutorial.domain.dto.article.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.List;


@Getter
@Setter
public class ArticlePublicDTO {
    private String id;
    private String subject;
    private List<String> tags;
    private String date;
    private AuthorDTO author;
    private String description;
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
