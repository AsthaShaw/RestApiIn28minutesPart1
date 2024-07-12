package com.astha.project.restful_webservices_udemy_part1.controller;

import com.astha.project.restful_webservices_udemy_part1.HellowWorldBean;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorldController {


    //@RequestMapping(method= RequestMethod.GET, path="/hello-world")
    @GetMapping("/hello-world")
    public String helloWorld(){
        return "Hello World";
    }


    //@RequestMapping(method= RequestMethod.GET, path="/hello-world-bean")
    @GetMapping("/hello-world-bean")
    public HellowWorldBean helloWorldBean(){
        return new HellowWorldBean("Hello World");
    }

    @GetMapping("/hello-world/{name}")
    public HellowWorldBean hellowWorldPathVariable(@PathVariable String name){
        return new HellowWorldBean(String.format("Hello World, %s",name));
    }


}
