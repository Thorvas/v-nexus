package com.example.demo.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for operations related to JWT token
 *
 * @author Thorvas
 */
@Service
public class JwtService {

    private final String SECRET_KEY = "ijtOwic7j1eGYDZVrt0lP4LrYdSAQ/fkfoxMJxgaOW+cMM+cZKAeLX5enrurl8U9PRRpy1lLwgbLebyXGziVgJcEAkzRn5MaFEyRNdtgulA=";

    /**
     * Extracts username from claims stored within token
     *
     * @param jwtToken JWT token provided in parameter
     * @return String containing extracted claim
     */
    public String extractUsername(String jwtToken) {

        Claims allClaims = this.extractAllClaims(jwtToken);

        return allClaims.getSubject();
    }

    /**
     * Generates JWT token
     *
     * @param userDetails userDetails passed within filter
     * @return String containing generated token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }

    public String generateToken(
            String username
    ) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }

    /**
     * Checks whether provided token is valid
     *
     * @param token   Provided JWT token
     * @param details userDetails provided in filter
     * @return Boolean value containing result whether token is valid
     */
    public boolean isTokenValid(String token, UserDetails details) {

        final String username = extractUsername(token);

        return (username.equals(details.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks whether JWT token is expired
     *
     * @param token checked JWT token
     * @return Boolean value containing result whether token is expired
     */
    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration date from JWT token
     *
     * @param token checked JWT token
     * @return Date value containing expiration date of token
     */
    private Date extractExpiration(String token) {

        Claims allClaims = extractAllClaims(token);

        System.out.println(allClaims.getExpiration());
        return allClaims.getExpiration();
    }

    /**
     * Extracts all claims from JWT token
     *
     * @param token checked JWT token
     * @return Claims value containing extracted claims (data stored in token) of token
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Encrypts secret key for JWT token
     *
     * @return Prepared key for decoding
     */
    private Key getSignKey() {

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}
