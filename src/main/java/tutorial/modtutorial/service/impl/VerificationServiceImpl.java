package tutorial.modtutorial.service.impl;

import org.springframework.stereotype.Service;
import tutorial.modtutorial.domain.entity.User;
import tutorial.modtutorial.domain.entity.Verification;
import tutorial.modtutorial.domain.enums.Template;
import tutorial.modtutorial.exception.RegistrationException;
import tutorial.modtutorial.repository.VerificationRepository;
import tutorial.modtutorial.service.VerificationService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRepository repository;

    private static final int DAILY_LIMIT = 2;
    private static final long LIMIT_NOT_REACHED_REQUEST_INTERVAL = 2; // 2 minutes
    private static final long LIMIT_REACHED_REQUEST_INTERVAL = 24; // 24 hours
    private static final List<String> options = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    public static final long EXPIRATION_INTERVAL = 2; // 2 minutes

    public VerificationServiceImpl(VerificationRepository repository) {
        this.repository = repository;
    }


    // ======================== USER ZONE ========================
    @Override
    public String createVerification(String userId, Template template) {
        String code = generateCode(template.getCodeLength());
        Optional<Verification> optionalVerification = repository.findByUserId(userId);

        Verification verification;
        if (optionalVerification.isEmpty()) {
            // if first attempt then create new 'verification' with 'dailyUsage' = 1
            verification = new Verification(userId, code, 1);
        } else {
            // get existing 'verification' and update 'code'(code change may be reverted in case exception)
            verification = optionalVerification.get();
            verification.setCode(code);

            Duration duration = Duration.between(verification.getUpdatedDate(), LocalDateTime.now());

            if (duration.toHours() >= LIMIT_REACHED_REQUEST_INTERVAL) {
                // if last attempt was at least 'LIMIT_REACHED_REQUEST_INTERVAL' hours ago, then set dailyUsage to 1
                verification.setDailyUsage(1);
            } else if (duration.toMinutes() >= LIMIT_NOT_REACHED_REQUEST_INTERVAL && verification.getDailyUsage() < DAILY_LIMIT) {
                // if last request is within a 'LIMIT_REACHED_REQUEST_INTERVAL' hours, then
                // verify last request was more than 'LIMIT_NOT_REACHED_REQUEST_INTERVAL' minutes age and dailyUsage is less than 'DAILY_LIMIT'
                verification.setDailyUsage(verification.getDailyUsage() + 1);
            } else {
                throw new RegistrationException("interval", "Iltimos keyinroq urinib ko'ring");
            }
        }

        return repository.save(verification).getCode();
    }

    @Override
    public boolean checkVerification(User user, String code, Template template) {
        Optional<Verification> optionalVerification = repository.findByUserId(user.getId());

        if (optionalVerification.isEmpty()) {
            throw new RegistrationException("code", "Tasdiqlash kodi mavjud emas");
        }

        Verification verification = optionalVerification.get();

        // evaluate request interval
        Duration duration = Duration.between(verification.getUpdatedDate(), LocalDateTime.now());

        if (duration.toMinutes() > EXPIRATION_INTERVAL) {
            throw new RegistrationException("interval", "Tasdiqlash muddati tugagan");
        }

        if (Objects.equals(verification.getCode(), code) && verification.getCode().length() == template.getCodeLength()) {
            // set random code not to use this twice
            verification.setCode(generateCode(3));
            return true;
        }

        throw new RegistrationException("code", "Tasdiqlash kodi noto'g'ri");
    }


    private String generateCode(int length) {
        if (length == 5) {
            int rn = 10000000 + (int) (Math.random() * 10000000);
            return String.valueOf(rn).substring(0, 5);
        }

        int len = options.size();
        int index;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            index = (int) (Math.random() * len);
            result.append(options.get(index));
        }

        return result.toString();
    }
}
