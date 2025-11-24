package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.user.response.ProfilePublicDTO;
import tutorial.modtutorial.domain.enums.Role;

import java.util.List;


public interface ManagementService {
    List<Object[]> smsCountByTemplate();
    List<Object[]> userCountByActivated();
    ProfilePublicDTO getUserById(String userId);
    boolean updateUserRole(String username, Role role);
    List<String> getUserPhonesByRole(Role role);
}
