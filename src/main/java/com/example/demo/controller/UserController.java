package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------- READ ----------
    @GetMapping("/")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    // ---------- CREATE ----------
    @GetMapping("/adduser")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "add-user";
        }
        userRepository.save(user);
        return "redirect:/";
    }

    // ---------- UPDATE ----------
    @GetMapping("/edituser/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("user", user.get());
        return "edit-user";
    }

    @PostMapping("/edituser/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult result) {
        if (result.hasErrors()) {
            user.setId(id); // nécessaire pour reconstruire l'URL du formulaire
            return "edit-user";
        }

        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) {
            return "redirect:/";
        }

        User toUpdate = existing.get();
        toUpdate.setName(user.getName());
        toUpdate.setEmail(user.getEmail());
        userRepository.save(toUpdate);
        return "redirect:/";
    }

    // ---------- DELETE ----------
    @PostMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
        return "redirect:/";
    }
}