package tutorial.modtutorial.domain.dto.general.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class SubjectDTO {
    private String id;
    private String name;
    private long count;
    private List<TagDTO> tags;

    public static SubjectDTO of(String id, String name, List<TagDTO> tagDTOs) {
        SubjectDTO dto = new SubjectDTO();

        dto.setId(id);
        dto.setName(name);
        dto.setTags(tagDTOs);

        return dto;
    }

    public static SubjectDTO of(String id, String name, long count) {
        SubjectDTO dto = new SubjectDTO();

        dto.setId(id);
        dto.setName(name);
        dto.setCount(count);

        return dto;
    }
}
