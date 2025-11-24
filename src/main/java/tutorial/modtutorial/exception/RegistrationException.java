package tutorial.modtutorial.exception;

import lombok.Getter;


@Getter
public class RegistrationException extends RuntimeException {
    private final String reason;

    public RegistrationException(String reason, String message) {
        super(message);
        this.reason = reason;
    }
}
