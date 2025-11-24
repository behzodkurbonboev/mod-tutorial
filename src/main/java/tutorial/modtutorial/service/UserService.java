package tutorial.modtutorial.service;

import org.springframework.web.multipart.MultipartFile;
import tutorial.modtutorial.domain.dto.TokenDTO;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.dto.general.response.AuthorProfileDTO;
import tutorial.modtutorial.domain.dto.user.request.ProfileDTO;
import tutorial.modtutorial.domain.dto.user.request.UserDTO;
import tutorial.modtutorial.domain.dto.user.response.ProfilePublicDTO;


public interface UserService {
    AuthorDTO toAuthor(String userId);
    AuthorProfileDTO toAuthorProfile(String userId);
    void validate(String userId);
    void updateResourceCount(String authorId, String resource, int count);


    // ======================== USER ZONE ========================
    TokenDTO createGuest();
    Boolean registration(UserDTO dto);
    Boolean registrationSendCode(String username);
    Boolean registrationVerify(String username, String code);
    Boolean forgotPasswordSendCode(String username);
    Boolean forgotPasswordVerify(UserDTO dto, String code);
    Boolean deleteSendCode(String username);
    Boolean deleteVerify(String username, String code);
    ProfilePublicDTO getProfile(String userId);
    void updateProfile(String userId, ProfileDTO dto);
    void uploadImage(String userId, MultipartFile file);
    TokenDTO refreshToken(String authorization, String version);
}
