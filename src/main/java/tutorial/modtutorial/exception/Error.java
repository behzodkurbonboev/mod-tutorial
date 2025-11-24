package tutorial.modtutorial.exception;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Error {
    private String cause;
    private String message;

    public Error(String cause, String message) {
        this.cause = cause;
        this.message = message;
    }
}
