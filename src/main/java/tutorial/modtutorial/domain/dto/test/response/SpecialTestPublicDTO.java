package tutorial.modtutorial.domain.dto.test.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecialTestPublicDTO {
    private String id;
    private int number;
    private String text;
    private String question;
    private String choice0;
    private String choice1;
    private String choice2;
    private String choice3;
    private String solution;
    private Integer indexU;
    private Integer indexC;
}
