package com.astha.project.restful_webservices_udemy_part1.controller;

import com.astha.project.restful_webservices_udemy_part1.HellowWorldBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

//
//    //@RequestMapping(method= RequestMethod.GET, path="/hello-world")
//    @GetMapping("/hello-world")
//    public String helloWorld(){
//        return "Hello World";
//    }


    //@RequestMapping(method= RequestMethod.GET, path="/hello-world")
    @GetMapping("/hello-world-bean")
    public HellowWorldBean helloWorld(){
        return new HellowWorldBean("Hello World");
    }
}
