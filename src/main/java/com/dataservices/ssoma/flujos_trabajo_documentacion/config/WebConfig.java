package com.dataservices.ssoma.flujos_trabajo_documentacion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/static/frontend/");

        // Mapeo específico para la carpeta frontend
        registry.addResourceHandler("/frontend/**")
                .addResourceLocations("classpath:/static/frontend/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redireccionar la raíz a tu página de login
        registry.addViewController("/").setViewName("forward:/frontend/inicio.html");
    }
}
