package tutorial.modtutorial.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@Getter
@ConfigurationProperties(prefix = "ttt", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final Minio minio = new Minio();
    private final Sms sms = new Sms();
    private final Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Minio {
        private String application;
        private String host;
        private String username;
        private String password;
    }

    @Setter
    @Getter
    public static class Sms {
        private String authUrl;
        private String host;
        private String username;
        private String password;
        private String nickname;
        private String from;
    }

    @Setter
    @Getter
    public static class Jwt {
        private RSAPublicKey publicKey;
        private RSAPrivateKey privateKey;
        private String userClaim;
        private String authClaim;
        private String keyId;
        private long accessTokenExpiration;
        private long refreshTokenExpiration;
    }
}
