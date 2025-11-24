package tutorial.modtutorial.domain.dto.general.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NameDTO {
    @NotNull
    @NotEmpty
    @Size(min = 3, max = 100, message = "name length should be between 3 and 100")
    private String name;
}
