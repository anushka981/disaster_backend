package com.anushka.disaster_backend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 1, max = 100) String password
) {}
