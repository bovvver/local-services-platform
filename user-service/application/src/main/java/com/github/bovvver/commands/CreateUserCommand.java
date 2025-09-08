package com.github.bovvver.commands;

public record CreateUserCommand(String userId,
                         String email,
                         String firstName,
                         String lastName) {
}
