package tutorial.modtutorial.domain.dto.user.request;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.user.ContactDTO;

import java.util.List;


@Getter
@Setter
public class ProfileDTO {
    private String firstName;
    private String lastName;
    private String speciality;
    private String bio;
    private List<ContactDTO> contacts;
}
