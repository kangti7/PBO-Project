package com.keuangan.app.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JwtResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String role;
}
