package com.egomaa.booknetwork;

import com.egomaa.booknetwork.entity.Role;
import com.egomaa.booknetwork.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookNetworkApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            List<Role> roles = new ArrayList<>();
            roles.add(Role.builder().name("ADMIN").build());
            roles.add(Role.builder().name("USER").build());
            roleRepository.saveAll(roles);
        };
    }



}
