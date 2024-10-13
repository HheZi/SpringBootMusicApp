package com.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.user.repository.UserRepository;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class JpaConfig {

}
