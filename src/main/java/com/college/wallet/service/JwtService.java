package com.college.wallet.service;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.college.wallet.model.User;
import com.college.wallet.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService{
    private final UserRepository userRepository;
    @Value("${app.jwt.secret}")
    private String SecretKey;
    @Value("${app.jwt.access-token-expiry}")
    private long  atexpiry;
    @Value("${app.jwt.refresh-token-expiry}")
    private long rtexpiry;
    JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Map<String,String> tokens(UUID userId){
        String at=Jwts.builder()
                  .setSubject(userId.toString()).setIssuedAt(new Date())
                  .setExpiration(new Date(System.currentTimeMillis()+atexpiry))
                 . signWith(Keys.hmacShaKeyFor(SecretKey.getBytes()),SignatureAlgorithm.HS256)
                 .compact();
        String rt=Jwts.builder().setSubject(userId.toString()).setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+rtexpiry)).signWith(Keys.hmacShaKeyFor(SecretKey.getBytes()),SignatureAlgorithm.HS256).compact();
       return  Map.of("accessToken",at,"refreshToken",rt);
    }
 public String findUserId(String Token){
 return extractClaim(Token,Claims::getSubject);
 }

 public  boolean checkToken(String Token, User user){
    final String userIdfromToken=findUserId(Token);
    boolean isEqual= userIdfromToken.equals(user.getId().toString());
    boolean isNotExpired= !isExpired(Token);
    return (isEqual && isNotExpired);
  
 }
 public boolean isExpired(String token){
    return extractClaim(token,Claims::getExpiration).before(new Date());
 }
public<T> T extractClaim(String token,Function<Claims,T>claimsResolver){
   final Claims claims=Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SecretKey.getBytes())).build().parseClaimsJws(token).getBody();
   return claimsResolver.apply(claims);
}
}

