package com.csye6225.productmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class BasicAuthConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder auth)
            throws Exception
    {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
//        auth.inMemoryAuthentication()
//                .withUser("admin")
//                .password("{noop}password")//The notation {noop} before a password string in a configuration file indicates that the password should not be encoded
//                .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/healthz").permitAll() // allow unauthenticated access to /healthz
                .antMatchers("/v1/user").permitAll() // allow unauthenticated access to /healthz
                .antMatchers(HttpMethod.GET,"/v1/product/**").permitAll()// allow unauthenticated access to /healthz
                .anyRequest().authenticated()
//                .antMatchers(HttpMethod.GET,"/v1/user/**").access("@userSecurity.hasUserId(authentication,#userId)")
                .and()
                .httpBasic();
    }
    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}