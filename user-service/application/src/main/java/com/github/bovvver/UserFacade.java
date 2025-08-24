package com.github.bovvver;

import com.github.bovvver.public_commands.CreateUserCommand;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class that acts as a facade for user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;

    /**
     * Creates a new User entity based on the provided CreateUserCommand.
     *
     * @param createUserCommand the command object containing user details
     * @return the saved User entity
     */
    @Transactional
    User createUserFromKeycloak(CreateUserCommand createUserCommand) {
        User user = User.create(
                UserId.from(createUserCommand.userId()),
                new Email(createUserCommand.email()),
                createUserCommand.firstName(),
                createUserCommand.lastName()
        );
        return userRepository.save(user);
    }
}
