package com.rigel.ExpenseTracker.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.ForbiddenException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.service.UserService;
import com.rigel.ExpenseTracker.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class SecurityController {

    private final UserService userService;

    @GetMapping("/refresh/token}")
    private void getRefreshToken(HttpServletRequest request, HttpServletResponse response){
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
            }catch(Exception e){
                log.error("Error logging in: {}", e.getMessage());
                throw new ForbiddenException("Error while logging in: " + e.getMessage());
            }
        }
        else{
            throw new NotFoundException("Refresh token is missing!");
        }
    }

    @PostMapping("/register")
    private ResponseEntity<String> registerUser(String username, String password, String firstName, String lastName, String email, Double currentBudget) {
        if (username !=  null && password != null && firstName != null && lastName != null && email != null && currentBudget != null) {
            boolean validUsername = userService.getAllDBUsers().stream().anyMatch(user -> user.getUsername().equals(username));
            boolean validEmail = userService.getAllDBUsers().stream().anyMatch(user -> user.getEmail().equals(email));
            if(validUsername && validEmail) {
                userService.saveUser(new User(username, password, firstName, lastName, email, currentBudget));
                return ResponseEntity.ok().body(firstName + " " + lastName + " has been added successfully!");
            } else{
                if(!validUsername){
                    throw new BadRequestException("User with this username already exists.");
                }
                else  if (!validEmail){
                    throw new BadRequestException("User with this username already exists.");
                }
            }
        }
        throw new BadRequestException("Make sure you provide all data, including: username, password, first and last name, current budget!");
    }




}
