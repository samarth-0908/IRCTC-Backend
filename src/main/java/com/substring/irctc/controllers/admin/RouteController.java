package com.substring.irctc.controllers.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config/value")
public class RouteController {

    @Value("${config.test}")
    String value;

    @GetMapping
    public String getValue() {

        return value;
    }
}
