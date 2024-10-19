package com.auth.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth.model.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
public class JwtUtil {

	@Value("${token.secret}")
	private String SECRET_KEY;
	
	
	public String createJwtToken(UserDetails userDetails) {
		return Jwts.builder()
				.claim("username", userDetails.getUsername())
				.claim("id", userDetails.getId())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5))
				.signWith(getSingingKey())
				.compact();
	}
	
	public Claims getClaims(String token) {
		return (Claims) Jwts.parser().decryptWith(getSingingKey()).build().parse(token).getPayload();
	}
	
	
	public boolean isExpired(String token) {
		return getClaims(token).getExpiration().before(new Date());
	}
	
	private SecretKey getSingingKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}
}
