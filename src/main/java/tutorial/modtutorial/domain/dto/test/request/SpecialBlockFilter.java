package tutorial.modtutorial.domain.dto.test.request;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.request.BaseFilter;

import java.util.List;


@Getter
@Setter
public class SpecialBlockFilter extends BaseFilter {
    private String authorId;
    private List<String> subjectsId;
}
