package com.keuangan.app.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;  // ← jalan 1x per request
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain       // ← rantai filter, harus di-call di akhir
    ) throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);  // ① Ambil token dari header

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {  // ② Validasi
                String username = jwtUtils.getUsernameFromJwtToken(jwt);  // ③ Baca username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // ④ Load dari DB

                // ⑤ Buat objek Authentication dan simpan ke SecurityContext
                // Setelah ini, controller bisa tahu siapa yang request
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Tidak bisa set authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);  // ← WAJIB: lanjut ke filter berikutnya
    }

    // Ambil token dari header, buang prefix "Bearer "
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);  // "Bearer " = 7 karakter
        }
        return null;
    }
}
