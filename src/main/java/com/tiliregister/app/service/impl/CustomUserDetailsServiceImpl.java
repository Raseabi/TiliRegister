package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.UserDao;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService, UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userDao.findByUsername(username);

        if (user == null) {
           throw new UsernameNotFoundException("User not found");
        }

        if (user.getVoided() == 1) {
            throw new UsernameNotFoundException("User account is deactivated");
        }

        Set<GrantedAuthority> authorities = user.getUserRoles().stream()
                .flatMap(userRole -> userRole.getRole().getRolePermissions().stream())
                .map(rolePerm -> new SimpleGrantedAuthority(rolePerm.getPermission().getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getUserAuthentication().getPassword(),
                authorities
        );
    }

}
