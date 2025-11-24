package tutorial.modtutorial.domain.dto.user.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.user.ContactDTO;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.List;


@Getter
@Setter
public class ProfilePublicDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String imageUrl;

    private String fullName;
    private String speciality;
    private String bio;
    private List<ContactDTO> contacts;
    private int articlesCount;
    private int postsCount;
    private int blocksCount;
    private String enrolDate;
    private AccessType accessType;

    // [phone, balance] are only visible to the owner
    private String phone;
    private double balance;
}
