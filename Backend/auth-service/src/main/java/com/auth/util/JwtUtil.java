package com.auth.util;

import com.auth.enums.UserRole;
import com.auth.payload.response.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

	@Value("${token.secret}")
	private String SECRET_KEY;
	
	@Value("${jwt.expirationInMinutes}")
	private Long EXPIRATION_TIME_IN_MINUTES;

	public final static String USER_ROLE_KEY = "userRole";

	public final static String USER_ID_KEY = "id";
	
	public String createJwtToken(Integer userId, UserRole userRole) {
		return Jwts.builder()
				.claim(USER_ID_KEY, userId)
				.claim(USER_ROLE_KEY, userRole.name())
				.issuedAt(new Date())
				.expiration(Date.from(Instant.now().plus(EXPIRATION_TIME_IN_MINUTES, ChronoUnit.MINUTES)))
				.signWith(getSingingKey())
				.compact();
	
	}
	
	
	public Claims getClaims(String token) {
		return (Claims) Jwts.parser().verifyWith(getSingingKey()).build().parseSignedClaims(token).getPayload();
	}
	
	public boolean isExpired(String token) {
		try {
			return getClaims(token).getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}
	
	private SecretKey getSingingKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}
}
