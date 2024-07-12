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
