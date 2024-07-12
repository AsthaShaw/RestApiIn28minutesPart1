# SpringBoot with RestAPI Udemy

Reference -In 28 minutes repo-https://github.com/in28minutes/spring-microservices-v2/tree/main/02.restful-web-services

### Tools used

- IntelliJ IDEA Community Version
- Spring Initializer

                        - Dependencies - Spring Web- For building web applications including Restful applications using Spring MVC and Apache Tomcat as the default embedded container 

                                                   - Spring Data JPA- For persisting data in SQL database with Java Persistence API using Spring Data and Hibernate 

                                                   -H2 Database-Provides a fast in-memory database that supports JDBC API

### Creating a Hello World Rest API

HelloWorldController.java

```jsx
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    //@RequestMapping(method= RequestMethod.GET, path="/hello-world")
    @GetMapping("/hello-world")
    public String helloWorld(){
        return "Hello World";
    }
}

```

Previous example is returning a string but mainly json is returned. Enhancing the Hello World REST API to return a Bean

We returned a bean back below but what we are getting is a json response. So internally it is getting converted to json.

To understand this use logs. Go to [application.properties](http://application.properties) and write

logging.level.org.springframework=debug(by default info)

```jsx
 @GetMapping("/hello-world-bean")
    public HellowWorldBean helloWorld(){
        return new HellowWorldBean("Hello World");
    }
```

Questions-How are requests handled?

First request goes to/handled by because dispatcher servlet is mapped to “/” url →

- DispatcherServlet-Front Controller Pattern →From logs - Mapping servlets: dispatcherServlet urls=[/]. DispatcherServlet would map the request to the right controller
- Who is configuring the dispatcher servlet?- DispatcherServletAutoConfiguration
- How does HelloWorldBean get converted to json?- @ResponseBody(This message should be received as is and the default config for message configuration is JacksonHttpMessageConvertersConfiguration) + JacksonHttpMessageConvertersConfiguration(auto-configured)

Who is auto configuring this error mapping when an unknown URL is typed?

- Auto Configuration (ErrorMVCAutoConfiguration)

How are all the jars available (Spring, SpringMVC, Jackson, Tomcat)?

- Starter Projects- Spring Web dependency(Spring boot-starter-web) has springBoot-starter Web(spring-webmvc, spring-web, spring-boot-starter-tomcat, spring boot starter-json)

What is a bean in SpringBoot?
       A bean is an object that is instantiated, assembled and managed by a Spring IOC(Inversion of Control) container.

@PathVariable - simplifies extracting data from incoming requests making it easier to process dynamic values from the URLs in your Rest API endpoints

```jsx
 @GetMapping("/hello-world/{name}")
    public HellowWorldBean hellowWorldPathVariable(@PathVariable String name){
        return new HellowWorldBean(String.format("Hello World, %s",name));
    }
```

# Creating a Social Media Application using RestAPI

This social media application will have users and posts associated with it.

User bean

```jsx
package com.astha.project.restful_webservices_udemy_part1.user;

import java.time.LocalDate;

public class User {

    private Integer id;
    private String name;
    private LocalDate birthdate;

    public User(Integer id, String name, LocalDate birthdate) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }
}

```

UserDaoService

```jsx
package com.astha.project.restful_webservices_udemy_part1.user;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDaoService {

    private static List<User> users=new ArrayList<>();
    private static int userCount=0;

    static {
        users.add(new User(++userCount, "Adam", LocalDate.now().minusYears(30)));
        users.add(new User(++userCount, "Eve", LocalDate.now().minusYears(25)));
        users.add(new User(++userCount, "Jim", LocalDate.now().minusYears(20)));
    }

    public List<User> findAll(){
        return users;
    }

    //My way
//    public User findOneById(Integer id) {
//        return users.get(id-1);
//    }

    public User findOneById(Integer id) {
        return users.stream().filter(user->user.getId().equals(id)).findFirst().get();
    }

    public User save(User user){
        user.setId(++userCount);
        users.add(user);
        return user;
    }
}

```

UserResourse which is the controller

```jsx
package com.astha.project.restful_webservices_udemy_part1.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserResource {

    private UserDaoService service;

//constructor injection
    public UserResource(UserDaoService service){
        this.service=service;
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return service.findAll();
    }

    @GetMapping("/users/{id}")
    public User retrieveUser(@PathVariable int id){
        return service.findOneById(id);
    }

    @PostMapping("/users")
    public void createUser(@RequestBody User user){
        service.save(user);

    }

}

```

**So what is happening-when we request in the browser, the request comes to the controller which calls the service, retrieves the data and converts to json**

### @RequestBody

- You apply **@RequestBody** to handler methods in Spring controllers.
- It indicates that Spring should deserialize the request body into an object, which is then passed as a parameter to the handler method.

The post mapping at the moment is sending 200 as a success response instead of 201. Let’s make it return 201;

```jsx
@PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){
        service.save(user);
        return ResponseEntity.created(null).build();

    }
```

### @ResponseEntity

• With **ResponseEntity**, you can also set headers and status codes.

**Now we would like to return the URI in the post request as a response. So to the URI of the current request we would like to add a path “/{id}” and replace it with the id of the present user generated and converted to the URI and send it back;**

```jsx
@PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser=service.save(user);
        URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();

    }
```

Now go and check in the header for the location and you will see the uri.