package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import com.app.model.Author;
import com.app.repository.AuthorRepository;

@Configuration
@EnableR2dbcRepositories
public class DataConfig {

	
}
