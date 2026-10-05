package com.samiran.booking_service_System.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-minutes}")
    private long jwtExpirationMinutes;
    //getSignUp-secreat key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
  //generate--Token
    public String generateToken(User user) {
        Date now = new Date();

        // Convert minutes to milliseconds
        long expirationInMs = jwtExpirationMinutes * 60 * 1000;
        Date expiryDate = new Date(now.getTime() + expirationInMs);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
// ====================== EXTRACT CLAIMS =It's the first time you read a token=====================
    private Claims parsingAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey()) //tells the parser which secret to use to check the signature.
                .build()
                .parseSignedClaims(token) //recomputes the HMAC over header.payload and compares it to the
                // token's third part. If they differ, it throws. It also throws if exp is in the past.
                .getPayload(); //now,returns the claims (subject, email, role) only after both checks pass.
    }
    // ====================== EXTRACT USER ID ======================
    public String extractUserId(String token){
        return parsingAllClaims(token).getSubject();
    }
    // ====================== EXTRACT EMAIL ======================
    public String extractEmail(String token){
        return parsingAllClaims(token).get("email",String.class);
    }
    // ====================== CHECK IF TOKEN IS EXPIRED ======================
    private boolean isTokenExpired(String token){
        return parsingAllClaims(token).getExpiration().before(new Date());
    }
    // ====================== VALIDATE TOKEN ======================
    public boolean isTokenValid(String token){
        try {
            parsingAllClaims(token);  // will throw exception if signature is invalid
            return !isTokenExpired(token); // check expiry
        }catch (JwtException | IllegalArgumentException e){
           return false;
        }
    }
}
