package com.subash.SGDisposals.util;

import com.subash.SGDisposals.RoleEnum;
import com.subash.SGDisposals.exception.UnauthorizedRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "SG_DIS_000_111_xxx_YYY_2026_SG_DIS";

    public String generateToken(String email, RoleEnum role){

        return Jwts.builder().setSubject(email).claim("role",role.toString()).setIssuedAt(new Date()).setExpiration(
                new Date(System.currentTimeMillis() + 1000 * 60 * 60)).signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                SignatureAlgorithm.HS256).compact();
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token).getBody();
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

    public RoleEnum extractRole(String token){
        String role =  getClaims(token).get("role",String.class);
        try{
            return RoleEnum.valueOf(role);
        }

        catch (Exception e){
            throw  new UnauthorizedRequestException("No Valid Roles Found in Token");
        }
    }

    public boolean validateToken(String token){
        try{
            getClaims(token);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
