package com.rigel.ExpenseTracker.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserPayload {

    private Long userId;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Double currentBudget;
}