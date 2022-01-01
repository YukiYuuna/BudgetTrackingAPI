package com.rigel.ExpenseTracker.security;

import com.rigel.ExpenseTracker.exception.ApiExceptionHandler;
import com.rigel.ExpenseTracker.exception.CustomAccessDeniedHandler;
import com.rigel.ExpenseTracker.exception.CustomAuthenticationFailureHandler;
import com.rigel.ExpenseTracker.filter.CustomAuthFilter;
import com.rigel.ExpenseTracker.filter.CustomAuthorizationFilter;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.rigel.ExpenseTracker.entities.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthFilter customAuthFilter = new CustomAuthFilter(authenticationManagerBean());
        customAuthFilter.setFilterProcessesUrl("/api/login");

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);

//        For Security operations:
        http.authorizeRequests().antMatchers("/api/register/**", "/api/login*", "/api/refresh/token/**").permitAll();

//        For User operations:
        http.authorizeRequests().antMatchers(GET, "/api/users/**").hasAnyRole(ADMIN);
        http.authorizeRequests().antMatchers(GET, "/api/user").hasAnyRole(ADMIN, USER);
        http.authorizeRequests().antMatchers(PUT, "/api/user/modify").hasAnyRole(ADMIN, USER);
        http.authorizeRequests().antMatchers(POST, "/api/user/save/role", "/api/user/add/role/**").hasAnyRole(ADMIN);
        http.authorizeRequests() .antMatchers(DELETE, "/api/user/delete").hasAnyRole(USER);

//        For Expenses operations:
        http.authorizeRequests().antMatchers(GET, "/api/expense/transactions").hasAnyRole(ADMIN);
        http.authorizeRequests().antMatchers(GET, "/api/expense/transactions/user", "/api/expense/categories", "/api/expense/transaction/**").hasAnyRole(USER);
        http.authorizeRequests().antMatchers(POST,"/api/add/expense/category/**", "/api/add/expense/transaction/**" ).hasAnyRole(USER);
        http.authorizeRequests().antMatchers(PUT,"/api/modify/expense/transaction/**").hasAnyRole(USER);
        http.authorizeRequests().antMatchers(DELETE,"/api/delete/expense/category/**", "/api/delete/expense/transactions/**", "/api/delete/expense/transaction/**").hasAnyRole(USER);

//        Filtering:
        http.authorizeRequests().anyRequest().authenticated()
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        http.addFilter(customAuthFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
