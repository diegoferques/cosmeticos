package com.cosmeticos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by matto on 19/07/2017.
 */
@Controller
public class WebTemplatesController {

    //https://spring.io/guides/gs/serving-web-content/
    //@RequestMapping(path = "/teste", method = RequestMethod.GET)
    @RequestMapping("/secure/teste")
    public String secureTeste(
            @RequestParam(value="name", required=false, defaultValue="World") String name,
            Model model) {
        model.addAttribute("name", name);
        return "secure/teste";
    }
}
