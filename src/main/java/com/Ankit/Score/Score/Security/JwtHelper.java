package com.Ankit.Score.Score.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtHelper {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 hours
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // EXISTING METHODS (NO CHANGES)
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // EXISTING METHOD (NO CHANGES)
    public String generateToken(UserDetails userDetails, Long adminId, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("name", name);

        // Extract roles as comma-separated string
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);

        return doGenerateToken(claims, userDetails.getUsername());
    }

    // EXISTING METHOD (NO CHANGES)
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(secretKey)
                .compact();
    }

    // EXISTING METHOD (NO CHANGES)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // EXISTING METHODS (NO CHANGES)
    public Long getAdminIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("adminId", Long.class));
    }

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("email", String.class));
    }

    public String getRolesFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("roles", String.class));
    }

    // NEW METHODS FOR OAUTH 2.0
    /**
     * Generate token for OAuth2 users
     */
    public String generateTokenFromOAuthUser(CustomOAuth2User oauthUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", oauthUser.getUser().getUserId());
        claims.put("name", oauthUser.getUser().getName());
        claims.put("email", oauthUser.getUser().getEmail());

        // Extract roles as comma-separated string
        String roles = oauthUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);

        claims.put("provider", oauthUser.getUser().getProvider());

        return doGenerateToken(claims, oauthUser.getUser().getEmail());
    }

    /**
     * Generate token for regular users (without adminId)
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Extract roles as comma-separated string
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);

        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Helper method to extract userId from token
     */
    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Helper method to extract name from token
     */
    public String getNameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("name", String.class));
    }

    /**
     * Helper method to extract provider from token
     */
    public String getProviderFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("provider", String.class));
    }
}