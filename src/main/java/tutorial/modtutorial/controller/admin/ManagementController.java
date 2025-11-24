package tutorial.modtutorial.controller.admin;

import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.constant.PhoneRegEx;
import tutorial.modtutorial.domain.dto.user.response.ProfilePublicDTO;
import tutorial.modtutorial.domain.enums.Role;
import tutorial.modtutorial.service.ManagementService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/private/management")
public class ManagementController {
    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }


    @GetMapping("/sms")
    public ResponseEntity<List<Object[]>> getSMSCountByTemplate() {
        return ResponseEntity.ok(managementService.smsCountByTemplate());
    }

    @GetMapping("/users")
    public ResponseEntity<List<Object[]>> getUserCountByActivated() {
        return ResponseEntity.ok(managementService.userCountByActivated());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ProfilePublicDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(managementService.getUserById(userId));
    }

    @PutMapping("/users")
    public ResponseEntity<Boolean> updateUserRole(@RequestParam @Pattern(regexp = PhoneRegEx.PHONE_REGEX, message = "phone number format invalid") String username, @RequestParam Role role) {
        return ResponseEntity.ok(managementService.updateUserRole(username, role));
    }

    @GetMapping("/users/roles")
    public ResponseEntity<List<String>> getUserPhonesByRole(@RequestParam(defaultValue = "MODERATOR") Role role) {
        return ResponseEntity.ok(managementService.getUserPhonesByRole(role));
    }
}
