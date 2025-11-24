package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.SpecialUsers;
import tutorial.modtutorial.domain.dto.user.response.ProfilePublicDTO;
import tutorial.modtutorial.domain.entity.Authority;
import tutorial.modtutorial.domain.entity.User;
import tutorial.modtutorial.domain.enums.Role;
import tutorial.modtutorial.repository.SMSRepository;
import tutorial.modtutorial.repository.UserRepository;
import tutorial.modtutorial.service.ManagementService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ManagementServiceImpl implements ManagementService {
    private final SMSRepository smsRepository;
    private final UserRepository userRepository;

    public ManagementServiceImpl(SMSRepository smsRepository, UserRepository userRepository) {
        this.smsRepository = smsRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Object[]> smsCountByTemplate() {
        return smsRepository.countByTemplate();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> userCountByActivated() {
        return userRepository.countByVerified();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfilePublicDTO getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id = '" + userId + "' not found"));

        ProfilePublicDTO result = new ProfilePublicDTO();

        result.setId(user.getId());
        result.setPhone(user.getUsername());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setImageUrl(user.getImageUrl());
        result.setFullName(user.getFullName());
        result.setSpeciality(user.getSpeciality());
        result.setBio(user.getBio());
        result.setContacts(user.getContacts());
        result.setArticlesCount(user.getArticlesCount());
        result.setPostsCount(user.getPostsCount());
        result.setBlocksCount(user.getBlocksCount());
        result.setEnrolDate(user.getCreatedDate().format(DateTimeFormatter.ofPattern("HH:mm/dd.MM.yyyy")));
        result.setAccessType(user.getAccessType());

        return result;
    }

    @Override
    @Transactional
    public boolean updateUserRole(String username, Role role) {
        if (role == Role.ADMIN || role == Role.MODERATOR) {
            throw new RuntimeException("You cannot assign role = '" + role + "' to users.");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("user not found"));

        if (SpecialUsers.contains(user.getId())) {
            throw new RuntimeException("You cannot assign new role to this user, username = '" + username + "'");
        }

        if (!user.isVerified() || user.isDeleted()) {
            throw new RuntimeException("You cannot assign new role to unverified or deleted user, username = '" + username + "'");
        }

        user.setAuthorities(Authority.of(role));
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserPhonesByRole(Role role) {
        if (role == Role.USER) {
            role = Role.MODERATOR;
        }

        return userRepository.findByAuthorities(Set.of(role.asAuthority().getName())).stream().map(User::getUsername).collect(Collectors.toList());
    }
}
