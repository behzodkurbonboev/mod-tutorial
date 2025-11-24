package tutorial.modtutorial.domain.dto.test.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorProfileDTO;
import tutorial.modtutorial.domain.enums.Difficulty;


@Getter
@Setter
public class SpecialBlockPublicDTO {
    private String id;
    private String date;
    private String code;
    private String subject;
    private Difficulty difficulty;
    private AuthorProfileDTO author;
    private boolean analysed;
    private String averageScore;
    private int solvedCount;
    private boolean solvedByUser;
    private int userScore;
}
