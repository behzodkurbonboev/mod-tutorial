package tutorial.modtutorial.domain.dto.test.request;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.Difficulty;


@Getter
@Setter
public class SpecialTestDTO {
    private String subjectId;
    private String text;
    private String question;
    private String trueAnswer;
    private String falseAnswer1;
    private String falseAnswer2;
    private String falseAnswer3;
    private String solution;
    private boolean analysed;
    private Difficulty difficulty;

    public void setSolution(String solution) {
        if (solution != null && solution.isBlank()) {
            solution = null;
        }

        this.solution = solution;
    }

    public void setText(String text) {
        if (text != null && text.isBlank()) {
            text = null;
        }

        this.text = text;
    }
}
