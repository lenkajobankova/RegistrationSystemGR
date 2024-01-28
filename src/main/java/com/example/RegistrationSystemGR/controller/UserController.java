package com.example.RegistrationSystemGR.controller;

import com.example.RegistrationSystemGR.model.User;
import com.example.RegistrationSystemGR.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user)throws FileNotFoundException{
        try {
            return userService.createNewUser(user);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Error during reading file ValidatedPersonId.txt"+e.getLocalizedMessage());
        }
    }
    @GetMapping("/{id}")
    public User getUserById(
            @PathVariable("id") long id,
            @RequestParam(value = "detail", required = false) String detail){
        return userService.getUserById(id, detail);
    }
    @GetMapping
    public List<User> getUsers(
            @RequestParam(value = "detail", required = false) String detail){
        return userService.getAllUsers(detail);
    }
    @PutMapping("/{id}")
    public String updateUser(
            @PathVariable("id") long id,
            @RequestBody User user){
        userService.updateUser(user, id);
        return "User: id = "+id+" was updated - new name: "+user.getName()
                +", new surname: "+user.getSurname()+".";
    }
    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable int id){
        userService.deleteUser(id);
        return "User: id = "+id+" was deleted.";
    }
}
