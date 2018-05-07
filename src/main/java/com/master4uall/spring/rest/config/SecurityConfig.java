package com.master4uall.spring.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
            auth
                    .inMemoryAuthentication()
                    .passwordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance())
                    .withUser("user1")
                    .password("secret1")
                    .roles("USER")
                    .and()
                    .withUser("admin1")
                    .password("secret1")
                    .roles("USER", "ADMIN");
    }

    protected void configure(HttpSecurity http) throws Exception {
            http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/**")
                .hasRole("USER").and()
                .csrf()
                .disable()
                .headers()
                .frameOptions()
                .disable();
    }
}
