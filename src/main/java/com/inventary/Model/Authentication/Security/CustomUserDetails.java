package com.inventary.Model.Authentication.Security;

import com.inventary.Model.Authentication.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final Users usuarios;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Users usuarios) {
        this.usuarios = usuarios;
        this.authorities = loadAuthorities(usuarios.getRole());
    }

    public Users getUser() {
        return usuarios;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = usuarios.getRole();
        return loadAuthorities(role);
    }

    private Collection<? extends GrantedAuthority> loadAuthorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            System.out.println("Loaded Role: " + role.getName());
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuarios.getPassword();
    }

    @Override
    public String getUsername() {
        return usuarios.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
