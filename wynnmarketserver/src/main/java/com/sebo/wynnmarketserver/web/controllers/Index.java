package com.sebo.wynnmarketserver.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Index {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Wynnmarket");
        return "index";
    }
}
