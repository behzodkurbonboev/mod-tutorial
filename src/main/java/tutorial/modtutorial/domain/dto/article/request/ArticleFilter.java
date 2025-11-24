package tutorial.modtutorial.domain.dto.article.request;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.request.BaseFilter;

import java.util.List;


@Getter
@Setter
public class ArticleFilter extends BaseFilter {
    private List<String> subjectsId;
    private List<String> tagsId;
    private String authorId;
}
