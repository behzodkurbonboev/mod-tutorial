package tutorial.modtutorial.domain.dto.general.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthorDTO {
    private String id;
    private String fullName;
    private String imageUrl;

    public AuthorDTO(String id, String firstName, String lastName, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;

        // construct fullName
        if (firstName != null && lastName != null) {
            this.fullName = firstName + " " + lastName;
        } else if (firstName != null) {
            this.fullName = firstName;
        } else if (lastName != null) {
            this.fullName = lastName;
        } else {
            this.fullName = "Foydalanuvchi";
        }
    }
}
