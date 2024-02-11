package org.had.hospitalinformationsystem.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtProvider {

    private static final SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public static String generateToken(Authentication auth, String role){
        return Jwts.builder()
                .setIssuer("had").setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+28800000))
                .claim("userName",auth.getName())
                .claim("role",role)
                .signWith(key)
                .compact();
    }
    public static String getUserNameFromJwtToken(String jwt){
        jwt=jwt.substring(7);
        Claims claims=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        return String.valueOf(claims.get("userName"));
    }
    public static String getUserNameFromJwtTokenUnfiltered(String jwt){
        //jwt=jwt.substring(7);
        Claims claims=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        return String.valueOf(claims.get("userName"));
    }

    public static String getRoleFromJwtToken(String jwt){
        jwt=jwt.substring(7);
        Claims claims=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        return String.valueOf(claims.get("role"));
    }
}
