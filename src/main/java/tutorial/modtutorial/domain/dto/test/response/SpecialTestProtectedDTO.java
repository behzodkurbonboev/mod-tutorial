package tutorial.modtutorial.domain.dto.test.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.domain.enums.Difficulty;


@Getter
@Setter
public class SpecialTestProtectedDTO {
    private String id;
    private int number;
    private SubjectDTO subject;
    private String text;
    private String question;
    private String trueAnswer;
    private String falseAnswer1;
    private String falseAnswer2;
    private String falseAnswer3;
    private String solution;
    private Difficulty difficulty;
    private boolean analysed;
    private String createdDate;
    private String updatedDate;
    private AuthorDTO createdBy;
    private AuthorDTO updatedBy;
    private int usageCount;
}
