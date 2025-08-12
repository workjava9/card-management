package com.example.card_management.security;

import com.example.card_management.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
public class AuthUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;

    public AuthUserDetails(UserEntity u){
        this.id = u.getId();
        this.username = u.getUsername();
        this.password = u.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole().name()));
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities(){ return authorities; }
    @Override public boolean isAccountNonExpired(){ return true; }
    @Override public boolean isAccountNonLocked(){ return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled(){
        return true; }
}
