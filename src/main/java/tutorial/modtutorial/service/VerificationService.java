package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.entity.User;
import tutorial.modtutorial.domain.enums.Template;


public interface VerificationService {
    // ======================== USER ZONE ========================
    String createVerification(String userId, Template template);
    boolean checkVerification(User user, String code, Template template);
}
