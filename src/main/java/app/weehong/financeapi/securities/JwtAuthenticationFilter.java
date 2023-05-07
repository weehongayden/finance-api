package app.weehong.financeapi.securities;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

//        System.out.println(token);
//
//        String username = Jwts.parser()
//                .setSigningKey("mySecretKey")
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();

//        Jwt jwt = jwtDecoder.decode(jwtToken);
//        Map<String, Object> claims = jwt.getClaims();

//        if (!claims.containsKey("sub")) {
//            return null;
//            return claims.get("sub").toString().split("\\|")[1];
//        }


        filterChain.doFilter(request, response);
    }
}
