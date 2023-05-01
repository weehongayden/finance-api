package app.weehong.financeapi.utils;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final JwtDecoder jwtDecoder;

  public JwtUtil(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  public String extractUserId(String token) {
    String jwtToken = token.substring(7);
    Jwt jwt = jwtDecoder.decode(jwtToken);
    Map<String, Object> claims = jwt.getClaims();

    if (claims.containsKey("sub")) {
      return claims.get("sub").toString().split("\\|")[1];
    }

    return null;
  }
}
