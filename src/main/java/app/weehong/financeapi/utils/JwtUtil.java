package app.weehong.financeapi.utils;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Map;

public class JwtUtil {

    private static JwtDecoder jwtDecoder;

    public JwtUtil(JwtDecoder jwtDecoder) {
        JwtUtil.jwtDecoder = jwtDecoder;
    }

    public static String extractUserId(String token) {
        String jwtToken = token.substring(7);
        Jwt jwt = jwtDecoder.decode(jwtToken);
        Map<String, Object> claims = jwt.getClaims();

        if (claims.containsKey("sub")) {
            return claims.get("sub").toString().split("\\|")[1];
        }

        return null;
    }
}
