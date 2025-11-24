package tutorial.modtutorial.domain.dto.general.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthorProfileDTO {
    private String id;
    private String fullName;
    private String imageUrl;
    private String speciality;
    private int articlesCount;
    private int postsCount;
    private int blocksCount;
}
