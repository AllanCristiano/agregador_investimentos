package allancristiano.agregador_investimentos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import allancristiano.agregador_investimentos.entities.User;
import allancristiano.agregador_investimentos.entities.dto.CreateUserDto;
import allancristiano.agregador_investimentos.repositories.UserRepositore;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepositore userRepositore;
    @InjectMocks
    private UserService userService; 
    @Captor
    private ArgumentCaptor<User> usArgumentCaptor;

    @Nested
    class createUser{
        @Test
        @DisplayName("should  create a user with success")
        void shouldCreateAUserWithSuccess(){
            // Arrange
            var user = new User(
                UUID.randomUUID(), 
                "username", 
                "email@email.com", 
                "password", 
                Instant.now(), 
                null
                );

            doReturn(user).when(userRepositore).save(usArgumentCaptor.capture());

            var input = new CreateUserDto(
                "username", 
                "email@email.com", 
                "password"
                );
            // Act
            var output = userService.createUser(input);
            // Assert
            assertNotNull(output);
            var userCaptor = usArgumentCaptor.getValue();
            assertEquals(input.username(), userCaptor.getUsername());
            assertEquals(input.email(), userCaptor.getEmail());
            assertEquals(input.password(), userCaptor.getPassword());

        }

        @Test
        @DisplayName("should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs(){
            doThrow(new RuntimeException()).when(userRepositore).save(any());
            var input = new CreateUserDto(
                "username", 
                "email@email.com", 
                "password"
                );

            assertThrows(RuntimeException.class, () -> userService.createUser(input));
        }
    }
}
