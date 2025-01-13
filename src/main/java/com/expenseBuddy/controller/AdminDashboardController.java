package com.expenseBuddy.controller;

import com.expenseBuddy.model.UserEntity;
import com.expenseBuddy.service.UserService;
import org.apache.catalina.User;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final UserService userService;

    //Constructor-based dependency injection
    private AdminDashboardController(UserService userService){
        this.userService = userService;
    }


    //For viewing users
    @GetMapping("/users")
    public String viewAllUsers(Model model){
        List<UserEntity> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-user-management";
    }

    //Get the user details for editing
    @GetMapping("/users/edit/{id}")
    public String showEditUserPage(@PathVariable("id") Long id, Model model){
        UserEntity user = userService.getUserById(id);
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "admin-user-edit";
    }

    //Update user details after edit
    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable("id") Long id,
                           @ModelAttribute UserEntity updatedUser){

        //Retrieve the existing user from the database
        UserEntity existingUser = userService.getUserById(id);

        //Update only modifiable fields
        existingUser.setUsername(updatedUser.getUsername());

        existingUser.setEmail(updatedUser.getEmail());

        existingUser.setRole(updatedUser.getRole());

        //Save the updated user
        userService.updateUser(existingUser);
        return "redirect:/admin/users";  //Redirect back to the dashboard
    }

    //For deleting users
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    //for adding a new user
    @GetMapping("/users/add")
    public String addUserForm(Model model){
        //Provide a blank UserEntity for the form
        model.addAttribute("user", new UserEntity());
        return "admin-user-add";
    }

    //Save new user
    @PostMapping("/users/add")
    public String saveNewUser(@ModelAttribute("user") UserEntity user){
        userService.addUser(user);
        return "redirect:/admin/users";
    }


    @GetMapping("/dashboard-view")
    public String showAdminDashboard(Model model, Authentication authentication) {

        //Get the logged-in admin's username
        String username = authentication.getName();

        //Add the username to the model
        model.addAttribute("username", username);
        return "admin-dashboard";
    }


}
