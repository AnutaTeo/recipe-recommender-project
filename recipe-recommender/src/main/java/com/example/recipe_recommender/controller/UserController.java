package com.example.recipe_recommender.controller;

import com.example.recipe_recommender.model.User;
import com.example.recipe_recommender.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user, Model model) {
        String validationError = userService.validateUser(user);

        if (validationError != null) {
            model.addAttribute("error", validationError);
            model.addAttribute("user", user);
            return "add-user";
        }

        userService.addUser(user);
        return "redirect:/";
    }
}