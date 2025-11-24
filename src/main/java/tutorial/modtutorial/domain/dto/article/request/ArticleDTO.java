package tutorial.modtutorial.domain.dto.article.request;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ArticleDTO {
    private String subjectId;
    private List<String> tagsId = new ArrayList<>();
    private String title;
    private String description;
    private String content;
    private String shareUrl;
    private AccessType accessType;
}
