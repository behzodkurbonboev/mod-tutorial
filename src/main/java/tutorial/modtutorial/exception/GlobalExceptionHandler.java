package tutorial.modtutorial.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RegistrationException.class)
    public Error handleRegistrationException(RegistrationException e) {
        return new Error(e.getReason(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public Error handleRegistrationException(Exception e) {
        return new Error("validation", "So'rovda talabga javob bermaydigan o'zgaruvchi mavjud.");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(Exception e) {
        return "Talab etilgan ma'lumot mavjud emas.";
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public String handleEntityExistsException(Exception e) {
        return "Mavjud ma'lumot bilan kelishmovchilik vujudga keldi.";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public String handleUnclassifiedExceptions(Exception e) {
        return "So'rovni amalga oshirishda muammo vujudga keldi.";
    }
}
