package uk.co.setech.easybook.commons.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.model.User;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Autowired
    private HttpServletRequest servletRequest;

    private static final String SECRET_KEY = "4D6251655468576D5A7134743777397A24432646294A404E635266556A586E32";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(User user) {
        return Jwts
                .builder()
                .setClaims(Collections.singletonMap("user_id", user.getId()))
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);

        return (extractedUsername.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsresolver) {
        final Claims claims = extractAllClaims(token);

        return claimsresolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        String errorMessage;
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException JWT Token Error ", e);
            errorMessage = "Token Has Expired";
            servletRequest.setAttribute("actual-error", errorMessage);
            throw e;
        } catch (SignatureException e) {
            log.error("ExpiredJwtException JWT Token Error ", e);
            errorMessage = "Invalid Token Supplied";
            servletRequest.setAttribute("actual-error", errorMessage);
            throw e;
        }


    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
