package com.substring.irctc.config;

import com.substring.irctc.interceptors.MyCustomInterceptors;
import com.substring.irctc.interceptors.TimeLoggerInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.management.modelmbean.ModelMBean;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Autowired
    private MyCustomInterceptors myCustomInterceptors;

    @Autowired
    private TimeLoggerInterceptor timeLoggerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(timeLoggerInterceptor)
                .addPathPatterns("/trains/**", "/stations/**")
                .excludePathPatterns("/trains/list");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
