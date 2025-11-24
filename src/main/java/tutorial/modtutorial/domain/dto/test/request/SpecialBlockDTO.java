package tutorial.modtutorial.domain.dto.test.request;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.Difficulty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class SpecialBlockDTO {
    private LocalDateTime date;
    private String subjectId;
    private Difficulty difficulty;
    private List<String> testsId = new ArrayList<>();
    private String authorId;
    private boolean analysed;
}
