package tutorial.modtutorial.domain.dto.forum.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.request.BaseFilter;

import java.util.List;


@Getter
@Setter
public class ProblemFilter extends BaseFilter {
    @NotNull
    @NotEmpty
    @Size(min = 36, max = 36, message = "forumId should be supplied")
    private String forumId;
    private List<String> tagsId;
    private String authorId;
}
