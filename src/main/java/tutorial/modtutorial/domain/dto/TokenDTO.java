package tutorial.modtutorial.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class TokenDTO implements Serializable {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("routes")
    private Set<String> routes;

    public TokenDTO(String userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }

    public TokenDTO(String userId, String accessToken, String refreshToken, Set<String> routes) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.routes = routes;
    }
}
