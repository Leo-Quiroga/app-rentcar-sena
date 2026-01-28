package com.autoreserve.backend.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientTestController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public String clientProfile() {
        return "Welcome CLIENT, RBAC is working!";
    }
}
