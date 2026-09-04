package com.myshop.auth.seeder;

import com.myshop.auth.entity.Role;
import com.myshop.auth.entity.User;
import com.myshop.auth.entity.UserRole;
import com.myshop.auth.repository.RoleRepository;
import com.myshop.auth.repository.UserRepository;
import com.myshop.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ADMIN")
                        .description("Administrator")
                        .build()));

        User admin = userRepository.findByEmail(ADMIN_EMAIL).orElse(null);
        if (admin == null) {
            admin = userRepository.save(User.builder()
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .enabled(true)
                    .build());
            log.info("Seeded admin user {}", ADMIN_EMAIL);
        }

        if (!userRoleRepository.existsByUserIdAndRoleId(admin.getId(), adminRole.getId())) {
            userRoleRepository.save(UserRole.builder()
                    .userId(admin.getId())
                    .roleId(adminRole.getId())
                    .build());
            log.info("Assigned ADMIN role to {}", ADMIN_EMAIL);
        }
    }
}
