package com.substring.irctc.config.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(FileProperties.class)
@Profile("dev")
public class DevConfig {

    @Autowired
    FileProperties fileProperties;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx){
        return args -> {
            System.out.println("Let's Inspect the beans provided by Spring Boot: ");
            System.out.println("Bean names: "+ ctx.getBeanDefinitionCount());
            System.out.println("File path: "+ fileProperties.getPath());
            System.out.println("File name: "+ fileProperties.getFileName());
        };
    }
}
