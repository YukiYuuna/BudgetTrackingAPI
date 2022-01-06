package com.rigel.ExpenseTracker.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.service.UserService;
import com.rigel.ExpenseTracker.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class SecurityController {

    private final UserService userService;

    @Autowired
    public SecurityController(@Lazy UserService userService){
        this.userService = userService;
    }

    @GetMapping("/refresh/token")
    public void getRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            try{
                String refreshToken = authHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secretAlgorithm".getBytes());

                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);

                String username = decodedJWT.getSubject();
                User user = userService.getUserByUsername(username);

                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10min
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }catch(Exception exception){
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("errorMessage", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
        else{
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Refresh token is missing!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(String username, String password, String firstName, String lastName, String email, Double currentBudget) {
        if (username !=  null && password != null && firstName != null && lastName != null && email != null && currentBudget != null) {
            boolean invalidUsername = userService.getAllDBUsers().stream().anyMatch(user -> user.getUsername().equals(username));
            boolean invalidEmail = userService.getAllDBUsers().stream().anyMatch(user -> user.getEmail().equals(email));
            if(!invalidUsername && !invalidEmail) {
                userService.saveUser(new User(username, password, firstName, lastName, email, currentBudget));
                return ResponseEntity.ok().body(firstName + " " + lastName + " has been added successfully!");
            } else{
                if(invalidUsername){
                    throw new ResponseStatusException(BAD_REQUEST, "User with this username already exists.");
                }
                throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists.");
            }
        }
        throw new ResponseStatusException(BAD_REQUEST, "Make sure you provide all data, including: username, password, first and last name, current budget!");
    }




}
