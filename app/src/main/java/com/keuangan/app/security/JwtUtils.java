package com.keuangan.app.security;

import com.keuangan.app.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j      // ← Lombok: buat variabel log otomatis
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")         // ← ambil dari application.properties
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    // Ubah string secret dari properties menjadi SecretKey object untuk signing
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);  // HMAC-SHA256
    }

    // ① Generate token — dipanggil di AuthController setelah login berhasil
    public String generateJwtToken(Authentication authentication) {
        com.keuangan.app.security.UserDetailsImpl userPrincipal = (com.keuangan.app.security.UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
            .subject(userPrincipal.getUsername())   // ← isi payload: username
            .issuedAt(new Date())                   // ← waktu token dibuat
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey())              // ← tanda tangan digital
            .compact();                             // ← hasilkan string token
    }

    // ② Baca username dari token — dipanggil di JwtAuthFilter
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();  // ← ambil "subject" yang kita set tadi (username)
    }

    // ③ Validasi token — dipanggil di JwtAuthFilter sebelum load user
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;  // ← token valid
        } catch (MalformedJwtException e) {
            log.error("Token tidak valid: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token sudah expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token tidak didukung: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token kosong: {}", e.getMessage());
        }
        return false;  // ← token tidak valid
    }
}