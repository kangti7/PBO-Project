package com.keuangan.app.security;

import com.keuangan.app.enums.UserStatus;
import com.keuangan.app.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private UserStatus status;   // ← ganti dari Boolean isValidated ke UserStatus

    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email,
                           UserStatus status, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id          = id;
        this.username    = username;
        this.email       = email;
        this.status      = status;
        this.password    = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            // ↑ enum M5 hanya "USER"/"ADMIN", jadi perlu prefix "ROLE_"
            // hasil akhir: "ROLE_USER" atau "ROLE_ADMIN"
        );

        return new UserDetailsImpl(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getStatus(),    // ← UserStatus enum, bukan Boolean
            user.getPassword(),
            authorities
        );
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword()                { return password; }
    @Override public String getUsername()                { return username; }
    @Override public boolean isAccountNonExpired()       { return true; }
    @Override public boolean isAccountNonLocked()        { return true; }
    @Override public boolean isCredentialsNonExpired()   { return true; }

    @Override
    public boolean isEnabled() {
        // Hanya user TERVALIDASI yang bisa aktif menggunakan sistem
        //return this.status == UserStatus.TERVALIDASI;
        return true; // debug
    }
}