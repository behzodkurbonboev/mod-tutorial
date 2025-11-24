package tutorial.modtutorial.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class SecurityUtils {
    private static final Set<String> admin = Set.of("account", "files", "subjects", "forums", "articles", "special-tests", "special-blocks");
    private static final Set<String> moderator = Set.of("account", "files", "subjects", "forums", "articles", "special-tests", "special-blocks");
    private static final Set<String> test = Set.of("account", "files", "subjects", "special-tests");
    private static final Set<String> article = Set.of("account", "files", "subjects", "articles");
    private static final Set<String> forum = Set.of("account", "files", "subjects", "forums");
    private static final Set<String> user = Set.of();

    public static String getCurrentUserId() {
        SecurityContext context = SecurityContextHolder.getContext();

        return Optional.ofNullable(context.getAuthentication())
                .map(Principal::getName)
                .orElse(null);
    }

    public static Set<String> toRoutes(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(SecurityUtils::toRoutes).flatMap(Set::stream).collect(Collectors.toSet());
    }


    private static Set<String> toRoutes(GrantedAuthority authority) {
        if (authority == null) {
            return Set.of();
        }

        return switch(authority.getAuthority()) {
            case "ROLE_USER" -> user;
            case "ROLE_FORUM" -> forum;
            case "ROLE_ARTICLE" -> article;
            case "ROLE_TEST" -> test;
            case "ROLE_MODERATOR" -> moderator;
            case "ROLE_ADMIN" -> admin;
            default -> throw new RuntimeException("Unexpected authority value: " + authority.getAuthority());
        };
    }
}
