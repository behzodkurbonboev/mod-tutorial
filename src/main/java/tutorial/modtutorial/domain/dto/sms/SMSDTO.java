package tutorial.modtutorial.domain.dto.sms;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.Template;


@Getter
@Setter
public class SMSDTO {
    @NonNull
    private String userId;
    @NonNull
    private String phone;
    @NonNull
    private String code;
    private Template template;
}
