package tutorial.modtutorial.domain.dto.forum.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;


@Getter
@Setter
public class ForumProtectedDTO {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String backgroundImageUrl;
    private SubjectDTO subject;
    private String style;
    private String createdDate;
    private String updatedDate;
    private AuthorDTO author;
    private AuthorDTO createdBy;
    private AuthorDTO updatedBy;
    private int numOfPosts;
}
