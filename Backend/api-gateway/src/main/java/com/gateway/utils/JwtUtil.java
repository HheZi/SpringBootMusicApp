package com.gateway.utils;

import com.gateway.model.UserJwtPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.springframework.util.StringUtils.hasText;

@Component
public class JwtUtil {

	@Value("${token.secret}")
	private String SECRET_KEY;

	public final static String USER_ROLE_KEY = "userRole";

	public final static String USER_ID_KEY = "id";

	private final static String BEARER_PREFIX = "Bearer ";
	
	private Claims getClaims(String token) {
		return  Jwts.parser().verifyWith(getSingingKey()).build().parseSignedClaims(token).getPayload();
	}

	public boolean isExpired(String token) {
		try {
			return getClaims(token).getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	public UserJwtPayload getUserJwtPayload(String token) {
		Claims claims = getClaims(token);

		Integer userId = (Integer) claims.get(USER_ID_KEY);
		String userRole = (String) claims.get(USER_ROLE_KEY);

		return new UserJwtPayload(userId, userRole);
	}

	public String getValueOfPayload(String key, String token){
		Claims claims = getClaims(token);

		return (String) claims.get(key);
	}

	private SecretKey getSingingKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

}
