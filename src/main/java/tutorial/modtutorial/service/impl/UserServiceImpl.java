package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import tutorial.modtutorial.constant.SpecialUsers;
import tutorial.modtutorial.domain.dto.TokenDTO;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.dto.general.response.AuthorProfileDTO;
import tutorial.modtutorial.domain.dto.sms.SMSDTO;
import tutorial.modtutorial.domain.dto.user.ContactDTO;
import tutorial.modtutorial.domain.dto.user.request.ProfileDTO;
import tutorial.modtutorial.domain.dto.user.request.UserDTO;
import tutorial.modtutorial.domain.dto.user.response.ProfilePublicDTO;
import tutorial.modtutorial.domain.entity.Authority;
import tutorial.modtutorial.domain.entity.User;
import tutorial.modtutorial.domain.enums.AccessType;
import tutorial.modtutorial.domain.enums.Role;
import tutorial.modtutorial.domain.enums.Template;
import tutorial.modtutorial.exception.RegistrationException;
import tutorial.modtutorial.repository.UserRepository;
import tutorial.modtutorial.security.UserPrincipal;
import tutorial.modtutorial.security.JwtService;
import tutorial.modtutorial.service.FileService;
import tutorial.modtutorial.service.SMSService;
import tutorial.modtutorial.service.UserService;
import tutorial.modtutorial.service.VerificationService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static tutorial.modtutorial.service.impl.VerificationServiceImpl.EXPIRATION_INTERVAL;
import static tutorial.modtutorial.utils.SecurityUtils.getCurrentUserId;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final SMSService smsService;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final String GUEST_USERNAME = "guest";

    public UserServiceImpl(
            UserRepository userRepository, VerificationService verificationService,
            SMSService smsService, FileService fileService,
            PasswordEncoder passwordEncoder, JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.verificationService = verificationService;
        this.smsService = smsService;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    @Override
    @Transactional(readOnly = true)
    public AuthorDTO toAuthor(String userId) {
        return userRepository.findAuthorById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorProfileDTO toAuthorProfile(String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    AuthorProfileDTO result = new AuthorProfileDTO();

                    result.setId(user.getId());
                    result.setFullName(user.getFullName());
                    result.setImageUrl(user.getImageUrl());
                    result.setSpeciality(user.getSpeciality());
                    result.setArticlesCount(user.getArticlesCount());
                    result.setPostsCount(user.getPostsCount());
                    result.setBlocksCount(user.getBlocksCount());

                    return result;
                })
                .orElse(null);
    }

    @Override
    public void validate(String userId) {
        Optional<User> optional = userRepository.findById(userId);

        if (optional.isEmpty() || optional.get().isDeleted()) {
            throw new EntityNotFoundException("User with id = '" + userId + "' not found");
        }
    }

    @Override
    @Transactional
    public void updateResourceCount(String userId, String resource, int count) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = '" + userId + "' not found");
        }

        if ("articles".equals(resource)) {
            userRepository.updateArticlesCount(userId, count);
        } else if ("posts".equals(resource)) {
            userRepository.updatePostsCount(userId, count);
        } else if ("blocks".equals(resource)) {
            userRepository.updateBlocksCount(userId, count);
        } else {
            throw new RuntimeException("resource not found");
        }
    }


    // ======================== USER ZONE ========================
    @Override
    @Transactional
    public TokenDTO createGuest() {
        String userId = getCurrentUserId();

        if (SpecialUsers.GUEST_CREATOR_ID.equals(userId)) {
            // create guest user per install
            User guest = new User();

            guest.setUsername(GUEST_USERNAME);
            guest.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            guest.setFirstName(null);
            guest.setLastName(null);
            guest.setVerified(false); // will be true after registration process finishes
            guest.setActivated(true);
            guest.setAccessType(AccessType.BASIC);
            guest.setAuthorities(Authority.of(Role.GUEST));
            guest.setBalance(0d);

            userRepository.save(guest);

            // create required tokens for guest user
            UserPrincipal principal = new UserPrincipal(guest);
            String access = jwtService.createAccessToken(principal);
            String refresh = jwtService.createRefreshToken(principal);

            return new TokenDTO(principal.getUserId(), access, refresh, Set.of());
        }

        throw new RuntimeException("User with id = " + userId + "is not allowed to create a guest");
    }

    @Override
    @Transactional
    public Boolean registration(UserDTO dto) {
        validatePassword(dto.getPassword());

        // guest user is not verified
        String guestId = getCurrentUserId();

        User guest = getByIdElseThrow(guestId);
        throwIfVerified(guest);

        // 'username' is not verified:
        String username = dto.getUsername();

        Optional<User> optional = userRepository.findByUsername(username);

        if (optional.isEmpty()) {
            // if 'username' is not used before, then set 'username' to guest
            guest.setUsername(username);
        } else {
            User existing = optional.get();

            if (existing.isVerified()) {
                // if 'username' is verified before then throw
                throw new RegistrationException("username", "Foydalanuvchi tizimda mavjud");
            } else {
                // if 'username' is not verified, but used by other existing user,
                // then remove it from existing user, and set 'username' to guest.
                // But do not allow 'username' change within 'EXPIRATION_INTERVAL'
                if (!existing.getId().equals(guestId)) {
                    Duration duration = Duration.between(existing.getUpdatedDate(), LocalDateTime.now());
                    if (duration.toMinutes() < EXPIRATION_INTERVAL) {
                        throw new RegistrationException("username", "Foydalanuvchi tizimda mavjud");
                    }

                    existing.setUsername(GUEST_USERNAME);
                }

                guest.setUsername(username);
            }
        }

        // set/update password
        guest.setPassword(passwordEncoder.encode(dto.getPassword()));

        return true;
    }

    @Override
    @Transactional
    public Boolean registrationSendCode(String username) {
        String guestId = getCurrentUserId();

        User guest = getByIdElseThrow(guestId);
        throwIfVerified(guest);
        // verify guest has the same 'username'
        if (!guest.getUsername().equals(username)) {
            throw new RegistrationException("username", "Telefon raqam noto'g'ri");
        }

        return createVerificationThenSendSms(guest, Template.REGISTRATION);
    }

    @Override
    @Transactional
    public Boolean registrationVerify(String username, String code) {
        String guestId = getCurrentUserId();

        User guest = getByIdElseThrow(guestId);
        throwIfVerified(guest);
        // verify guest has the same 'username'
        if (!guest.getUsername().equals(username)) {
            throw new RegistrationException("username", "Telefon raqam noto'g'ri");
        }

        boolean isVerified = verificationService.checkVerification(guest, code, Template.REGISTRATION);

        if (isVerified) {
            // update 'user' details:
            guest.setActivated(true);
            guest.setVerified(true);
            guest.setAuthorities(Authority.of(Role.USER));
        }

        return isVerified;
    }

    @Override
    @Transactional
    public Boolean forgotPasswordSendCode(String username) {
        User user = getByUsernameElseThrow(username);

        // if account is unverified then password cannot be changed
        throwIfNotVerified(user);

        return createVerificationThenSendSms(user, Template.FORGOT_PASSWORD);
    }

    @Override
    @Transactional
    public Boolean forgotPasswordVerify(UserDTO dto, String code) {
        validatePassword(dto.getPassword());

        User user = getByUsernameElseThrow(dto.getUsername());

        // if account is unverified then password cannot be changed
        throwIfNotVerified(user);

        boolean isVerified = verificationService.checkVerification(user, code, Template.FORGOT_PASSWORD);

        if (isVerified) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return isVerified;
    }

    @Override
    @Transactional
    public Boolean deleteSendCode(String username) {
        User user = getByUsernameElseThrow(username);

        // if account is unverified then deletion cannot be done
        throwIfNotVerified(user);

        return createVerificationThenSendSms(user, Template.DELETE_ACCOUNT);
    }

    @Override
    @Transactional
    public Boolean deleteVerify(String username, String code) {
        User user = getByUsernameElseThrow(username);

        if (hasRoles(user.getId(), Role.ADMIN, Role.MODERATOR, Role.ARTICLE, Role.FORUM, Role.TEST)) {
            // TODO: check if this user has resources, if no just delete
            throw new RuntimeException("Please contact with admin to get instruction");
        }

        // if account is unverified then deletion cannot be done
        throwIfNotVerified(user);
        boolean isVerified = verificationService.checkVerification(user, code, Template.DELETE_ACCOUNT);

        if (isVerified) {
            user.setDeleted(true);
            user.setUsername(user.getId());
            user.setFirstName("Noma'lum");
            user.setLastName("Akkaunt");
            user.setImageUrl(null);
        }

        return isVerified;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfilePublicDTO getProfile(String userId) {
        String currentUserId = getCurrentUserId();

        User user = getByIdElseThrow(userId);

        ProfilePublicDTO result = new ProfilePublicDTO();

        // fill mandatory fields:
        result.setId(user.getId());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setImageUrl(user.getImageUrl());
        result.setFullName(user.getFullName());

        // fill optional fields
        result.setSpeciality(user.getSpeciality());
        result.setBio(user.getBio());
        result.setContacts(user.getContacts());
        result.setArticlesCount(user.getArticlesCount());
        result.setPostsCount(user.getPostsCount());
        result.setBlocksCount(user.getBlocksCount());
        result.setEnrolDate(user.getCreatedDate().format(DateTimeFormatter.ofPattern("HH:mm/dd.MM.yyyy")));
        result.setAccessType(user.getAccessType());

        // [phone, balance] are only visible to the owner
        if (currentUserId.equals(userId)) {
            String username =  user.getUsername().equals(GUEST_USERNAME) ? "************": user.getUsername();

            String phone = "+" + username.substring(0, 3) + " " + username.substring(3, 5) + " " +  username.substring(5, 8) + " " +   username.substring(8, 10) + " " +  username.substring(10, 12);
            result.setPhone(phone);

            result.setBalance(user.getBalance());
        }

        return result;
    }

    @Override
    @Transactional
    public void updateProfile(String userId, ProfileDTO dto) {
        userId = getCurrentUserId();

        User user = getByIdElseThrow(userId);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setSpeciality(dto.getSpeciality());
        user.setBio(dto.getBio());

        if (dto.getContacts() != null) {
            List<ContactDTO> contactsFilled = dto.getContacts().stream()
                .filter(contact -> contact != null && contact.getLink() != null && !contact.getLink().isBlank())
                .map(ContactDTO::fill)
                .toList();

            user.setContacts(contactsFilled);
        } else {
            user.setContacts(null);
        }
    }

    @Override
    @Transactional
    public void uploadImage(String userId, MultipartFile file) {
        userId = getCurrentUserId();

        User user = getByIdElseThrow(userId);

        if (user.getArticlesCount() + user.getPostsCount() + user.getBlocksCount() > 0) {
            String oldImageUrl = user.getImageUrl();

            String newImageUrl = fileService.save(file);
            user.setImageUrl(newImageUrl);

            if (oldImageUrl != null) {
                fileService.deleteByUrl(oldImageUrl);
            }
        } else {
            throw new RuntimeException("User with 0 resource is not allowed to upload profile image");
        }
    }

    @Override
    @Transactional
    public TokenDTO refreshToken(String authorization, String version) {
        String token = authorization.substring(7);

        String userId = jwtService.extractUserId(token);

        User user = getByIdElseThrow(userId);
        if (user.isDeleted()) {
            throw new EntityNotFoundException("User with id = '" + userId + "' not found");
        }

        // update users mobile app version
        user.setVersion(version);

        // create access token
        UserPrincipal principal = new UserPrincipal(user);
        String access = jwtService.createAccessToken(principal);

        return new TokenDTO(user.getId(), access);
    }


    private Boolean createVerificationThenSendSms(User user, Template template) {
        String code = verificationService.createVerification(user.getId(), template);

        SMSDTO sms = new SMSDTO();
        sms.setUserId(user.getId());
        sms.setPhone(user.getUsername());
        sms.setCode(code);
        sms.setTemplate(template);

        smsService.send(sms);

        return true;
    }

    private User getByUsernameElseThrow(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
            throw new RegistrationException("username", "Foydalanuvchi mavjud emas");
        }

        return optionalUser.get();
    }

    private void validatePassword(String password) {
        if (ObjectUtils.isEmpty(password) || password.length() < 6) {
            throw new RegistrationException("password", "Yaroqsiz parol kiritilgan");
        }
    }

    private void throwIfVerified(User user) {
        if (user.isVerified()) {
            throw new RegistrationException("username", "Foydalanuvchi tizimda mavjud");
        }
    }

    private void throwIfNotVerified(User user) {
        if (!user.isVerified()) {
            throw new RegistrationException("username", "Foydalanuvchi mavjud emas");
        }
    }

    private User getByIdElseThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id = '" + userId + "' not found"));
    }

    private boolean hasRoles(String userId, Role... roles) {
        // TODO: check all usages
        return getByIdElseThrow(userId).getAuthorities().stream().
                anyMatch(authority -> Arrays.stream(roles).anyMatch(role -> authority.equals(role.asAuthority())));
    }
}
