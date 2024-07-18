# SpringBoot with RestAPI Udemy

Reference -In 28 minutes repo-https://github.com/in28minutes/spring-microservices-v2/tree/main/02.restful-web-services

### Tools used

- IntelliJ IDEA Community Version
- Spring Initializer
 - Dependencies -> Spring Web- For building web applications including Restful applications using Spring MVC and Apache Tomcat as the default embedded container
                -> Spring Data JPA- For persisting data in SQL database with Java Persistence API using Spring Data and Hibernate 
                -> H2 Database-Provides a fast in-memory database that supports JDBC API

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

-**`ResponseEntity`** in Spring is a powerful class that allows you to **customize** your HTTP responses. It represents the entire HTTP response, including the **status code**, **headers**, and **body**. 

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

**Implementing Exception Handling for 404 Request Not Found**

When you try users/101 you will get a WhiteLabelErrorPage with an exception trace. We wouldn’t like to have that so what we can do is try exception handling

- Changing the findOneById method code-Changing the get() method to an optional. But again that was just returning null so added exception handling

```jsx
  public User findOneById(Integer id) {
//        return users.stream().filter(user->user.getId().equals(id)).findFirst().get();
        //get() throws NoElementException so better to use the below
        return users.stream().filter(user->user.getId().equals(id)).findFirst().orElse(null);
        //But the above returns 200 success message but gives nothing when an error, so this isn't optimum, so better  to throw an exception
    }
```

- After adding Exception Handling

```jsx
@GetMapping("/users/{id}")
    public User retrieveUser(@PathVariable int id){

        User user= service.findOneById(id);

        if(user==null){
            throw new UserNotFoundException("id:"+id);
        }

        return user;
    }
```

Adding a custom Exception. The error was 500 Internal server error but we want to return 404 user not found so we need a ResponseStatus with our exception handling

```jsx
package com.astha.project.restful_webservices_udemy_part1.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//When user not found it is a 404 error and hence to return a 404 we need a ResponseStatus
@ResponseStatus(code= HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
```

### Implementing Generic Exception Handling for all resources

When we go to postman of a 404 error we see the following as structure of error handing in the body of the response

```jsx
{
    "timestamp": "2024-07-15T17:16:42.793+00:00",
    "status": 404,
    "error": "Not Found",
    "path": "/users/404"
}
```

This is a common structure that is defined by Spring and how is it implemented?

By ResponseEntityExceptionHandler-a standard class which raises all Spring MVC exception

And within that ResponseEntityException there is a handleException that defines the structure above and what we are going to do is just override that to provide our own structure. For that we will create two classes-one ErrorDetails class and the other CustomizedResponseEntityExceptionHandler and ErrorDetails(this has our structure)

CustomizedResponseEntityExceptionHandler

```jsx
package com.astha.project.restful_webservices_udemy_part1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

//To make it available to all controllers in the project use @ControllerAdvice
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) throws Exception {
        ErrorDetails errorDetails=new ErrorDetails(LocalDate.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

```

ErrorDetails

```jsx
package com.astha.project.restful_webservices_udemy_part1.exception;

import java.time.LocalDate;

public class ErrorDetails {
    //Structure of the ErrorDetails
    //timestamp
    //message
    //details

    private LocalDate timestamp;
    private String message;
    private String details;

    public ErrorDetails(LocalDate timestamp, String message, String details) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

}

```

We get the body response in postman like this

```jsx
{
    "timestamp": "2024-07-15",
    "message": "id:101",
    "details": "uri=/users/101"
}
```

We can change the LocalDate to LocalDateTime and get time as well. Also for user not found we do not keep Internal server error for “user not found” issue and can customise the

`CustomizedResponseEntityExceptionHandler` further. **Having a right response structure and status to the consumer of your RestAPI is really important**

```jsx
package com.astha.project.restful_webservices_udemy_part1.exception;

import com.astha.project.restful_webservices_udemy_part1.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;

//To make it available to all controllers in the project use @ControllerAdvice
//You can also use @InitBinder, @ModelAttribute methods to be shared across multiple @Controller classes
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllException(Exception ex, WebRequest request) throws Exception {
        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) throws Exception {
        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
    }
}

```

### Implementing Delete Request

UserDaoService

```jsx
 public void deleteById(int id) {
        Predicate<User> predicate = user -> user.getId().equals(id);
        users.removeIf(predicate);
    }
```

UserResource controller

```jsx
   @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){

        service.deleteById(id);

    }
```

### Implementing Validations for RestAPI

At the moment we can post users without a name and future dob which is bad. To prevent this we need validations. We need to have a dependency for that.

```jsx
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
```

Add the @Valid tag to the controller of postmapping

@Valid-The **`@Valid`** annotation in Spring is used for **validation purposes**. When you apply **`@Valid`** to a method parameter, Spring automatically triggers validation for that parameter before invoking the method. It ensures that the incoming data adheres to specified validation rules. [For example, if you have an object annotated with **`@Valid`**, the validation engine will validate its properties based on constraints defined in the object’s class1](https://stackoverflow.com/questions/3595160/what-does-the-valid-annotation-indicate-in-spring)[2](https://www.sitepoint.com/java-bean-validation-object-graphs-valid-annotation/)[3](https://medium.com/@himani.prasad016/validations-in-spring-boot-e9948aa6286b). If any validation errors occur, Spring responds with an HTTP 400 Bad Request status code. So, it’s a handy way to ensure data integrity and validity in your Spring applications!

```jsx

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        User savedUser=service.save(user);
        URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();

    }
```

Now we need to define the validations in our User Bean having name size to be a min of 2 characters and dob to be of past date

```jsx
  private Integer id;
    @Size(min=2)
    private String name;
    @Past
    private LocalDate birthdate;
```

Now when we put blank name and future birthdate then this will give us Bad Request 400. But still does not say what exactly went wrong. Consumer will have no idea what went wrong with the request.

In ResponseEntityExceptionHandler there is a method called MethodArgumentNotValid which throws MethodArgumentNotValidException. So we can override this.

```jsx
 @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
```

Now if you try posting then we get a long message with a bad request which tells the consumer the issue but still its too long and can be customised further

```jsx
{
    "timestamp": "2024-07-16T17:44:20.646576",
    "message": "Validation failed for argument [0] in public org.springframework.http.ResponseEntity<com.astha.project.restful_webservices_udemy_part1.user.User> com.astha.project.restful_webservices_udemy_part1.user.UserResource.createUser(com.astha.project.restful_webservices_udemy_part1.user.User) with 2 errors: [Field error in object 'user' on field 'birthdate': rejected value [2098-04-17]; codes [Past.user.birthdate,Past.birthdate,Past.java.time.LocalDate,Past]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.birthdate,birthdate]; arguments []; default message [birthdate]]; default message [must be a past date]] [Field error in object 'user' on field 'name': rejected value []; codes [Size.user.name,Size.name,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [user.name,name]; arguments []; default message [name],2147483647,2]; default message [size must be between 2 and 2147483647]] ",
    "details": "uri=/users"
}
```

So for that we add message to the fields in User Bean like below

```jsx
private Integer id;
    @Size(min=2, message="Name should have atleast 2 characters")
    private String name;
    @Past(message="Birth Date should be in the past")
    private LocalDate birthdate;
```

You can go further with the customisation-By customising the exception handler to just send the first error message and then once user fixes the first issue and then the second error issue would be shown like the below-changing ex.getMessage() to ex.getFieldError().getDefaultMessage() and now try in postman

```jsx
  @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(), ex.getFieldError().getDefaultMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
```

The other way you can do is below

```jsx
 @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),"Total Errors are::"+ex.getErrorCount()+" First Error:"+ex.getFieldError().getDefaultMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
```

And you will get the message as below :

```jsx
{
    "timestamp": "2024-07-16T18:05:39.77191",
    "message": "Total Errors are::2 First Error:Birth Date should be in the past",
    "details": "uri=/users"
}
```
### Advanced Rest API features

- Documentation
- Content Negotiation
- Internationalization-i18n
- Versioning
- HATEOAS
- Static Filtering
- Dynamic Filtering
- Monitoring

### Rest API Documentation

- The consumers need to understand our Rest API and hence we need documentation
- Consumers need to understand the - **Resources we use, Actions performed, Request/Response Structure(Constraints/Validations)**

For consistency(documentation consistent with other RestAPIs used in the enterprise) and accuracy(ensuring documentation is upto date and correct) purpose we would generate documentation through code. Manually can be done but more efficient would be generating documentation through code

### Rest API Documentation using Swagger and OpenAPI

2011-Swagger Specification and Swagger Tools were introduced

2016- OpenAPI specification created based on Swagger specification but Swagger Tools like Swagger UI which brings a visual aspect to the RestAPI is separate and is still very much in use to visualise and interact with our Rest API

For this we would need a dependency. Versions keep changing have a look out

```jsx
<dependency>
	<groupId>org.springdoc</groupId>
	<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
	<version>2.3.0</version>
</dependency>
```

Two important links

`springdoc-openapi` java library helps to automate the generation of API documentation using spring boot projects. `springdoc-openapi` works by examining an application at runtime to infer API semantics based on spring configurations, class structure and various annotations.

https://springdoc.org/

https://github.com/springdoc/springdoc-openapi

Make sure to see which version of SpringBoot is supported by the openapi. And the dependency is there in your pom.xml. If the dependency is not there then go to the github page and add the dependency from below.

https://github.com/springdoc/springdoc-openapi/blob/main/springdoc-openapi-starter-webmvc-ui/pom.xml

And now run the server, go to the below link and see the magic!

http://localhost:8080/swagger-ui/index.html

The v3 api-docs at the top of the swagger page will help us to see the OpenAPI specification defining our RestAPI whereas the swagger page is more for visualising the API and playing with it.