package com.theurich.stabilizer;

import com.theurich.stabilizer.service.FileSystemStorageService;
import com.theurich.stabilizer.service.StabilizerService;
import com.theurich.stabilizer.service.StorageService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAutoConfiguration
@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan("com.theurich.stabilizer")
public class StabilizerConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public StabilizerService stabilizerService() {
        return new StabilizerService();
    }

    @Bean
    public StorageService storageService() {
        return new FileSystemStorageService();
    }

    @Bean
    public Environment environment() {
        return new StandardEnvironment();
    }
}


