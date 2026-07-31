package com.anushka.disaster_backend;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final VolunteerRepository volunteerRepository;

    public CustomUserDetailsService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Volunteer volunteer = volunteerRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
                volunteer.getUsername(),
                volunteer.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_" + volunteer.getRole().toUpperCase(java.util.Locale.ROOT)
                        )
                )
        );
    }
}
