package allancristiano.agregador_investimentos.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import allancristiano.agregador_investimentos.entities.User;
import allancristiano.agregador_investimentos.entities.dto.CreateUserDto;
import allancristiano.agregador_investimentos.repositories.UserRepositore;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepositore userRepositore;


    public UUID createUser(CreateUserDto userDto){
        // Dto -> Entity
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        user.setCreationTimesTamp(Instant.now());
        user.setUpdateTimesTamp(null);
        
        var usersaved = userRepositore.save(user);
        return usersaved.getUserId();
    }

    public Optional<User> getUserById(String userId){
        var user = userRepositore.findById(UUID.fromString(userId));
        return user;
    }


    public List<User> listUsers(){
        return userRepositore.findAll();
    }

    public void deleteById(String userId){
        var userExists = userRepositore.existsById(UUID.fromString(userId));
        if (userExists) {
            userRepositore.deleteById(UUID.fromString(userId));
        }
    }
}
