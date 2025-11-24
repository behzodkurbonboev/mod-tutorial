package tutorial.modtutorial.domain.dto.test.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.domain.enums.Difficulty;

import java.time.LocalDateTime;


@Getter
@Setter
public class SpecialBlockProtectedDTO {
    private String id;
    private LocalDateTime date;
    private String code;
    private SubjectDTO subject;
    private Difficulty difficulty;
    private AuthorDTO author;
    private boolean analysed;
    private String createdDate;
    private String updatedDate;
    private AuthorDTO createdBy;
    private AuthorDTO updatedBy;
    private String averageScore;
    private int solvedCount;
    private boolean visible;
}
