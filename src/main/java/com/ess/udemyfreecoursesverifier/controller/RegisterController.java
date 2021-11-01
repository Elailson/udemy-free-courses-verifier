package com.ess.udemyfreecoursesverifier.controller;

import com.ess.udemyfreecoursesverifier.model.Register;
import com.ess.udemyfreecoursesverifier.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public void register(@RequestBody Register register) {
        registerService.validate(register);
    }
}
