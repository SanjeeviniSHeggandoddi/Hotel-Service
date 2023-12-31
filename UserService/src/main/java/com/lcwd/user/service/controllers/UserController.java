package com.lcwd.user.service.controllers;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    private Logger logger=LoggerFactory.getLogger(UserController.class);

    //create
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User user1= userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    //single user get
    int retryCount=1;
    @GetMapping("/{userId}")
    //@CircuitBreaker(name="ratingHotelBreaker",fallbackMethod="ratingHotelFallback")
   // @Retry(name="ratingHotelService",fallbackMethod = "ratingHotelFallback")
    @RateLimiter(name="userRateLimiter",fallbackMethod="ratingHotelFallback")
    public ResponseEntity<User> getSingeUSer(@PathVariable String userId){
        logger.info("Get single user handler:usercontroller");
        logger.info("Retry count:{}",retryCount);
        retryCount++;
        User user= userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    //creating fall back method

    public ResponseEntity<User> ratingHotelFallback(String userId,Exception ex){
        //logger.info("Fallback is execute because service is down:",ex.getMessage());

        User user=User.builder().email("sanjeevinish00@gmail.com")
                .name("dummy")
                .about("This user is created dummy bcs some service is down")
                .userId("12345")
                .build();
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    //all user
    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        List<User> allUser = userService.getAllUser();
        return ResponseEntity.ok(allUser);
    }
}
