package tutorial.modtutorial.domain.dto.article.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ArticleContentDTO {
    private String articleId;
    private String content;
}
