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