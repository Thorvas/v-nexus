package com.example.demo.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private final String SECRET_KEY = "ijtOwic7j1eGYDZVrt0lP4LrYdSAQ/fkfoxMJxgaOW+cMM+cZKAeLX5enrurl8U9PRRpy1lLwgbLebyXGziVgJcEAkzRn5MaFEyRNdtgulA=";

    public String extractUsername(String jwtToken) {

        Claims allClaims = this.extractAllClaims(jwtToken);

        return allClaims.getSubject();
    }

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

    public boolean isTokenValid(String token, UserDetails details) {

        final String username = extractUsername(token);

        return (username.equals(details.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {

        Claims allClaims = extractAllClaims(token);

        System.out.println(allClaims.getExpiration());
        return allClaims.getExpiration();
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}
