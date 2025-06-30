package com.tiliregister.app.security;

import com.tiliregister.app.dao.UserDao;
import com.tiliregister.app.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // ✅ CSRF disabled
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // ✅ updated httpBasic

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserDao userDao) {
        return username -> {
            User user = userDao.findByUsername(username);
            if (user == null) throw new UsernameNotFoundException("User not found");
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getUserAuthentication().getPassword(),
                    List.of(new SimpleGrantedAuthority("USER"))
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}