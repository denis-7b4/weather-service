package ru.testtask.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shutdown", description = "Shutdown controller can be used to stop service via context closing")
@RestController
public class ShutdownController implements ApplicationContextAware {

    private ApplicationContext context;

    @PostMapping("/shutdownContext")
    public void shutdownContext() {
        ((ConfigurableApplicationContext) context).close();
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;

    }
}