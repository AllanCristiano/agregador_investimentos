package allancristiano.agregador_investimentos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
import allancristiano.agregador_investimentos.entities.dto.UpdateUserDto;
import allancristiano.agregador_investimentos.repositories.UserRepositore;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepositore userRepositore;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Nested
    class createUser {
        @Test
        @DisplayName("should  create a user with success")
        void shouldCreateAUserWithSuccess() {
            // Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);

            doReturn(user).when(userRepositore).save(userArgumentCaptor.capture());

            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "password");
            // Act
            var output = userService.createUser(input);
            // Assert
            assertNotNull(output);
            var userCaptor = userArgumentCaptor.getValue();
            assertEquals(input.username(), userCaptor.getUsername());
            assertEquals(input.email(), userCaptor.getEmail());
            assertEquals(input.password(), userCaptor.getPassword());

        }

        @Test
        @DisplayName("should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {
            doThrow(new RuntimeException()).when(userRepositore).save(any());
            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "password");

            assertThrows(RuntimeException.class, () -> userService.createUser(input));
        }
    }

    @Nested
    class getUserById {
        @Test
        @DisplayName("should get user by id with success optional is present")
        void shouldGetUserByIdWithSuccessWhenOptionalIsPresent() {
            // Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);
            doReturn(Optional.of(user)).when(userRepositore).findById(uuidArgumentCaptor.capture());
            // act
            var output = userService.getUserById(user.getUserId().toString());
            // assert
            assertTrue(output.isPresent());
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("should get user by id with success optional is empty")
        void shouldGetUserByIdWithSuccessWhenOptionalIsEmpty() {
            // Arrange
            var userId = UUID.randomUUID();
            doReturn(Optional.empty()).when(userRepositore).findById(uuidArgumentCaptor.capture());
            // act
            var output = userService.getUserById(userId.toString());
            // assert
            assertTrue(output.isEmpty());
            assertEquals(userId, uuidArgumentCaptor.getValue());
        }
    }

    @Nested
    class listUsers {
        @Test
        @DisplayName("should return all users with success")
        void shouldReturnAllUsersWithSuccess() {
            // Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);
            doReturn(List.of(user)).when(userRepositore).findAll();
            // Act
            var output = userService.listUsers();
            // Assert
            assertNotNull(output);
            assertEquals(1, output.size());
        }
    }

    @Nested
    class deleteById {
        @Test
        @DisplayName("should delete user with success when user exist")
        void shouldDeleteUserWithSuccessWhenUserExist() {
            UUID userId = UUID.randomUUID();

            // Configure the mocks
            doReturn(true).when(userRepositore).existsById(userId);
            doNothing().when(userRepositore).deleteById(userId);

            // Act
            userService.deleteById(userId.toString());

            // Assert
            verify(userRepositore, times(1)).existsById(uuidArgumentCaptor.capture());
            verify(userRepositore, times(1)).deleteById(uuidArgumentCaptor.capture());

            var idList = uuidArgumentCaptor.getAllValues();
            assertEquals(2, idList.size());
            assertEquals(userId, idList.get(0));
            assertEquals(userId, idList.get(1));

            verify(userRepositore, times(1)).existsById(idList.get(0));
            verify(userRepositore, times(1)).deleteById(idList.get(1));
        }

        @Test
        @DisplayName("should not delete user when user does not exist")
        void shouldNotDeleteUserWhenUserDoesNotExist() {
            UUID userId = UUID.randomUUID();

            // Configure the mocks
            doReturn(false).when(userRepositore).existsById(userId);

            // Act
            userService.deleteById(userId.toString());

            // Assert
            verify(userRepositore, times(1)).existsById(userId);
            verify(userRepositore, times(0)).deleteById(any(UUID.class));
        }
    }

    @Nested
    class updateUserById {
        @Test
        @DisplayName("should update user by id when user exist and username and password is filled")
        void shouldUpdateUserByIdWhenUserExistAndUserNameAndPasswordIsFilled() {
            // Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);

            var updateUserDto = new UpdateUserDto("newusername", "newpassword");
            doReturn(Optional.of(user)).when(userRepositore).findById(uuidArgumentCaptor.capture());
            doReturn(user).when(userRepositore).save(userArgumentCaptor.capture());
            // act
            userService.updateUserById(user.getUserId().toString(), updateUserDto);
            // assert
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
            var userCaptured = userArgumentCaptor.getValue();
            assertEquals(userCaptured.getUsername(), updateUserDto.username());
            assertEquals(userCaptured.getPassword(), updateUserDto.password());

            verify(userRepositore, times(1)).findById(uuidArgumentCaptor.capture());
            verify(userRepositore, times(1)).save(user);
        }

        @Test
        @DisplayName("should update user by id when user does not exist")
        void shouldUpdateUserByIdWhenUserNotExist() {
            // Arrange
            var userId = UUID.randomUUID();
            var updateUserDto = new UpdateUserDto("newusername", "newpassword");

            // Configurar os mocks
            doReturn(Optional.empty()).when(userRepositore).findById(userId);

            // Act
            userService.updateUserById(userId.toString(), updateUserDto);

            // Assert
            verify(userRepositore, times(1)).findById(uuidArgumentCaptor.capture());
            assertEquals(userId, uuidArgumentCaptor.getValue());

            // Verificar que o save não foi chamado, pois o usuário não existe
            verify(userRepositore, times(0)).save(any());
        }

    }
}
