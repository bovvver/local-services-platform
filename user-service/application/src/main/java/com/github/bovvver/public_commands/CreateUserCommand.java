package com.github.bovvver.public_commands;

public record CreateUserCommand(String userId,
                         String email,
                         String firstName,
                         String lastName) {
}
