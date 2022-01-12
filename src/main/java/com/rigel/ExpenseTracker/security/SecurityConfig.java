package com.rigel.ExpenseTracker.security;

import com.rigel.ExpenseTracker.exception.CustomAccessDeniedHandler;
import com.rigel.ExpenseTracker.filter.CustomAuthenticationEntryPoint;
import com.rigel.ExpenseTracker.filter.CustomAuthenticationFilter;
import com.rigel.ExpenseTracker.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        CustomAuthenticationFilter customAuthFilter = new CustomAuthenticationFilter(
                customAuthenticationEntryPoint(), authenticationManagerBean());
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
        http.authorizeRequests().antMatchers(GET, "/api/expense/transaction/**").hasAnyRole(ADMIN, USER);
        http.authorizeRequests().antMatchers(GET, "/api/expense*").hasAnyRole(USER);
        http.authorizeRequests().antMatchers(GET, "/api/expense/transactions/admin").hasAnyRole(ADMIN);
        http.authorizeRequests().antMatchers(GET, "/api/expense/transactions/user", "/api/expense/transactions/category",
                "/api/expense/categories/user", "/api/expense/transactions/date").hasAnyRole(USER);
        http.authorizeRequests().antMatchers(POST,"/api/add/expense/category/**", "/api/add/expense/transaction/**" ).hasAnyRole(USER);
        http.authorizeRequests().antMatchers(PUT,"/api/modify/expense/transaction/**").hasAnyRole(USER);
        http.authorizeRequests().antMatchers(DELETE,"/api/delete/expense/category/user/**",
                "/api/delete/expense/transactions/**", "/api/delete/expense/transaction/**").hasAnyRole(USER);

//        For Income operations:
        http.authorizeRequests().antMatchers(GET, "/api/income/transaction/**").hasAnyRole(ADMIN, USER);
        http.authorizeRequests().antMatchers(GET, "/api/income/transactions/admin").hasAnyRole(ADMIN);
        http.authorizeRequests().antMatchers(GET, "/api/income/transactions/user", "/api/income/transactions/category",
                "/api/income/categories/user", "/api/income/transactions/date").hasAnyRole(USER);
        http.authorizeRequests().antMatchers(POST,"/api/add/income/category/**", "/api/add/income/transaction/**" ).hasAnyRole(USER);
        http.authorizeRequests().antMatchers(PUT,"/api/modify/income/transaction/**").hasAnyRole(USER);
        http.authorizeRequests().antMatchers(DELETE,"/api/delete/income/category/user/**",
                "/api/delete/income/transactions/**", "/api/delete/income/transaction/**").hasAnyRole(USER);

//        Filtering:
        http.authorizeRequests().anyRequest().authenticated()
                .and().exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());
        http.addFilter(customAuthFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(customAccessDeniedHandler()), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}
