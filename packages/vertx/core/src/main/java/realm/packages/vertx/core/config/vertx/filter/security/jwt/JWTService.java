package realm.packages.vertx.core.config.vertx.filter.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import realm.packages.vertx.core.config.jackson.DefaultJackson;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxPrincipal;
import realm.packages.vertx.core.config.vertx.security.model.principal.impl.SimpleAuthentication;
import realm.packages.vertx.core.config.vertx.security.model.principal.impl.SimplePrincipal;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ConditionalOnProperty("vertx.security.jwt.enabled")
@Service
public class JWTService {

    @Value("${vertx.security.jwt.secret-key}")
    private String secretKey;

    @Value("${vertx.security.jwt.expired-in}")
    private Long expiresIn;

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public VertxPrincipal extractPrincipal(String token, HttpServerRequest request) {
        try {
            Claims body = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userString = (String) body.get("user");
            SimpleSecurityUser user = DefaultJackson.objectMapper().readValue(userString, SimpleSecurityUser.class);
            SimpleAuthentication authentication = new SimpleAuthentication(user.getRoles());

            return SimplePrincipal.builder()
                    .authentication(authentication)
                    .otherInfo(user)
                    .id(user.getId())
                    .clientInfo(request.headers())
                    .build();
        } catch (Exception e) {
            return SimplePrincipal.builder()
                    .clientInfo(request.headers())
                    .build();
        }
    }

    /**
     * Kiểm tra token đã hết hạn hay chưa
     *
     * @param token
     * @return
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateJwt(SimpleSecurityUser securityUser) {
        System.out.println("SimpleSecurityUser =>" + securityUser.toString());

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("user", DefaultJackson.objectMapper().writeValueAsString(securityUser));

            return Jwts.builder()
                    .subject(securityUser.getId().toString())
                    .claims(claims)
                    .expiration(new Date(System.currentTimeMillis() + expiresIn))
                    .signWith(getSignInKey())
                    .compact();
        } catch (JsonProcessingException e) {
            System.out.println("error jwt =>" + e.getMessage());

            e.printStackTrace();
            return null;
        }
    }

    /**
     * Thiết lập lại hết hạn cho token này để đăng xuất
     *
     * @param token
     */
    public String expireJwt(String token) {
        Date currentDate = new Date();
        System.out.println("token =>" + token);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Tạo token mới với thời gian hết hạn là hiện tại
            return Jwts.builder()
                    .subject(claims.getSubject())
                    .claims(claims)
                    .expiration(currentDate)
                    .signWith(getSignInKey())
                    .compact();
        } catch (Exception ex) {
            System.out.println("Error =>" + ex.getMessage());
            ex.printStackTrace();
            return "";
        }
    }
}