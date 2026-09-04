package com.example.MyShop_API.config;

import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.Role;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.repo.AddressRepository;
import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    @Autowired
    @Lazy
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner initApplicationRunner(UserRepository userRepository, RoleRepository roleRepository) {

        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                Role role = roleRepository.findById("ADMIN").orElseGet(
                        () -> roleRepository.save(
                                Role.builder()
                                        .roleName("ADMIN")
                                        .description("Default role")
                                        .build()
                        )
                );

                roleRepository.save(role);

                User user = User.builder()
//                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .enabled(true)
                        .roles((Set.of(role)))
                        .build();


                userRepository.save(user);
            }
        };
    }
}
