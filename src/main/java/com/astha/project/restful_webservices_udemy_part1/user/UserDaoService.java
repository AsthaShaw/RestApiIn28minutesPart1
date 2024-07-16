package com.astha.project.restful_webservices_udemy_part1.user;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
//        return users.stream().filter(user->user.getId().equals(id)).findFirst().get();
        //get() throws NoElementException so better to use the below
        Predicate<User> predicate = user -> user.getId().equals(id);
        return users.stream().filter(predicate).findFirst().orElse(null);
        //But the above returns 200 success message but gives nothing when an error, so this isn't optimum, so better  to throw an exception
    }

    public User save(User user){
        user.setId(++userCount);
        users.add(user);
        return user;
    }

    public void deleteById(int id) {
        Predicate<User> predicate = user -> user.getId().equals(id);
        users.removeIf(predicate);
    }
}
