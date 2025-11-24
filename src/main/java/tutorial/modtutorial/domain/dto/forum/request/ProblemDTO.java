package tutorial.modtutorial.domain.dto.forum.request;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.List;


@Getter
@Setter
public class ProblemDTO {
    private String title;
    private String source;
    private String forumId;
    private List<String> tagsId;
    private String content;
    private String shareUrl;
    private AccessType accessType;

    public void setSource(String source) {
        if (source != null && source.isBlank()) {
            source = null;
        }

        this.source = source;
    }
}
