package tutorial.modtutorial.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tutorial.modtutorial.domain.dto.TokenDTO;
import tutorial.modtutorial.domain.dto.user.request.ProfileDTO;
import tutorial.modtutorial.domain.dto.user.request.UserDTO;
import tutorial.modtutorial.domain.dto.user.response.ProfilePublicDTO;
import tutorial.modtutorial.service.UserService;
import tutorial.modtutorial.constant.PhoneRegEx;


@Validated
@RestController
@RequestMapping("/api/v1/public/users")
public class UserPublicController {
    private final UserService userService;

    public UserPublicController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping("/forgot-password/send-code")
    public ResponseEntity<Boolean> sendForgotPasswordCode(@RequestParam @Pattern(regexp = PhoneRegEx.PHONE_REGEX, message = "phone number format invalid") String username) {
        return ResponseEntity.ok(userService.forgotPasswordSendCode(username));
    }

    @PutMapping("/forgot-password/verify")
    public ResponseEntity<Boolean> verifyForgotPassword(@RequestBody UserDTO dto, @RequestParam String code) {
        return ResponseEntity.ok(userService.forgotPasswordVerify(dto, code));
    }

    @PutMapping("/delete/send-code")
    public ResponseEntity<Boolean> deleteSendCode(@RequestParam @Pattern(regexp = PhoneRegEx.PHONE_REGEX, message = "phone number format invalid") String username) {
        return ResponseEntity.ok(userService.deleteSendCode(username));
    }

    @DeleteMapping("/delete/verify")
    public ResponseEntity<Boolean> deleteVerify(@RequestParam @Pattern(regexp = PhoneRegEx.PHONE_REGEX, message = "phone number format invalid") String username, @RequestParam String code) {
        return ResponseEntity.ok(userService.deleteVerify(username, code));
    }


    // following APIs requires auth-token
    @GetMapping("/guest")
    public ResponseEntity<TokenDTO> createGuest() {
        return ResponseEntity.ok(userService.createGuest());
    }

    @PostMapping("/registration")
    public ResponseEntity<Boolean> createUser(@Valid @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.registration(dto));
    }

    @PutMapping("/registration/send-code")
    public ResponseEntity<Boolean> sendVerificationCode(@RequestParam @Pattern(regexp = PhoneRegEx.PHONE_REGEX, message = "phone number format invalid") String username) {
        return ResponseEntity.ok(userService.registrationSendCode(username));
    }

    @PutMapping("/registration/verify")
    public ResponseEntity<Boolean> verifyUser(@RequestParam @Pattern(regexp = PhoneRegEx.PHONE_REGEX, message = "phone number format invalid") String username, @RequestParam String code) {
        return ResponseEntity.ok(userService.registrationVerify(username, code));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ProfilePublicDTO> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<Void> updateProfile(@PathVariable String userId, @RequestBody ProfileDTO dto) {
        userService.updateProfile(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/upload-image")
    public ResponseEntity<Void> uploadImage(@PathVariable String userId, @RequestParam MultipartFile file) {
        userService.uploadImage(userId, file);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDTO> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam(required = false, defaultValue = "22.03.2025-v2.7.3") String version
    ) {
        return ResponseEntity.ok(userService.refreshToken(authorization, version));
    }

    @GetMapping("/mobile/version")
    public ResponseEntity<String> getLatestVersion(@RequestParam(required = false, defaultValue = "android") String type) {
        String version;
        if ("ios".equals(type)) {
            // App Store release version
            version = "17.06.2025-piv2.8.1";
        } else {
            // Google Play release version
            version = "17.06.2025-pav2.8.1";
        }

        return ResponseEntity.ok(version);
    }
}
