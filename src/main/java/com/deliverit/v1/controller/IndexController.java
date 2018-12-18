package com.deliverit.v1.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value ="/",  produces = "application/json")
public class IndexController {
    @RequestMapping(method = RequestMethod.GET)
    public String sayHello(ModelMap model) {
        model.addAttribute("greeting", "Hello World from Spring 4 MVC");
        return "index";
    }

    @RequestMapping(value = "/helloagain", method = RequestMethod.GET)
    public String sayHelloAgain(ModelMap model) {
        model.addAttribute("greeting", "Hello World Again, from Spring 4 MVC");
        return "index";
    }

    @RequestMapping(value = "/v1/pets", method = RequestMethod.GET)
    public String getPets(ModelMap model) {
        model.addAttribute("greeting", "Hello World Again, You want pets ?");
        return "index";
    }

    @RequestMapping(value = "/v1/pets", method = RequestMethod.POST)
    public String postPets(ModelMap model) {
        model.addAttribute("greeting", "Hello World Again, Thank you for your pet !!");
        return "index";
    }

    @RequestMapping(value = "/v1/pets/{petId}", method = RequestMethod.GET)
    public String getPets(ModelMap model, @PathVariable("petId") Integer petId) {
        model.addAttribute("greeting", "Hello World Again, are you asking for " + petId);
        return "index";
    }
}
