package tutorial.modtutorial.domain.dto.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.constant.PhoneRegEx;


@Getter
@Setter
public class UserDTO {
    @NotNull
    @NotEmpty
    @Pattern(regexp = PhoneRegEx.PHONE_REGEX, message = "phone number format invalid")
    private String username;
    @NotNull
    @NotEmpty
    @Size(min = 6, max = 100)
    private String password;
}
