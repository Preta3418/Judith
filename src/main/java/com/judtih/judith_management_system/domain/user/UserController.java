package com.judtih.judith_management_system.domain.user;

import com.judtih.judith_management_system.domain.graduate.Graduate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//Get = read/retrieve data >> Get a data
//Post = Create a data >> Post a new data
//Put = update/modify a data >> Put a change to data
//Delete = delete a data

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userUpdates) {
        return userService.updateUser(id, userUpdates);
    }

    @PostMapping("/{id}/graduate")
    public Graduate graduateUser(@PathVariable Long id) {
        return userService.graduateUser(id);
    }





}
