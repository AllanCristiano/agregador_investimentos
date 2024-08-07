package allancristiano.agregador_investimentos.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import allancristiano.agregador_investimentos.entities.User;
import allancristiano.agregador_investimentos.entities.dto.CreateUserDto;
import allancristiano.agregador_investimentos.entities.dto.UpdateUserDto;
import allancristiano.agregador_investimentos.services.UserService;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/v1/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserDto userDto) {

        var userId = userService.createUser(userDto);
        
        return ResponseEntity.created(URI.create("/v1/users/" + userId.toString())).build();
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUserById(@PathVariable("user_id") String userId) {
        var user = userService.getUserById(userId);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getMethodName() {
        var users = userService.listUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user_id") String userId){
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<Void> putMethodName(@PathVariable("user_id") String id, @RequestBody UpdateUserDto updateUserDto) {
        userService.updateUserById(id, updateUserDto);        
        return ResponseEntity.noContent().build();
    }    
    
}
