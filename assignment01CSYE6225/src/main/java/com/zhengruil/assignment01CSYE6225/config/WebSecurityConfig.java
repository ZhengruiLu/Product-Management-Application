package com.zhengruil.assignment01CSYE6225.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(new BCryptPasswordEncoder())
                .withUser("ramp@gmail.com")
                .password("abc123")
                .roles("USER")
//                .and()
//                .withUser("admin")
//                .password("...")
//                .roles("ADMIN")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.POST,"/v1/user").permitAll()
                .antMatchers(HttpMethod.GET,  "/v1/user/*").hasRole("USER")
                .antMatchers(HttpMethod.PUT,  "/v1/user/*").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .exceptionHandling().accessDeniedPage("/403")
        ;


//        http.authorizeRequests()
//                .antMatchers("/v1/user/*").hasRole("USER")
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic()
//                .and()
//                .exceptionHandling().accessDeniedPage("/403");

//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic();
    }
}