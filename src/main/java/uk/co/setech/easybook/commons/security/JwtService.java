package uk.co.setech.easybook.commons.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    private static final String SECRET_KEY = "4D6251655468576D5A7134743777397A24432646294A404E635266556A586E32";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts
                .builder()
//                .setClaims(new HashMap<>()) TODO: just remove this if you don't want to set anything
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
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

        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException JWT Token Error ", e);
//            errorMessage = "User Token Has Expired";
//            servletRequest.setAttribute("actual-error", errorMessage);
            throw e;
        }


    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
