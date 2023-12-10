package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public User saveUser(User user) {
        //generate unique id
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        //get user from database with the help of user repository
        User user= userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with given id is not found on server"+userId));
        //fetch rating of the above user from Rating Service
        Rating[] ratingsOfUser =restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(),Rating[].class);
        logger.info("{}",ratingsOfUser);

        List<Rating> ratings=Arrays.stream(ratingsOfUser).collect(Collectors.toList());

        List<Rating> ratingList=ratings.stream().map(rating -> {
         //api call to hotel service to get the hotel
            //ResponseEntity<Hotel> forEntity =restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
           // Hotel hotel=forEntity.getBody();
            //using feign client remove 61 line and in place of 62 add
            Hotel hotel=hotelService.getHotel(rating.getHotelId());
           // logger.info("response status code: {}",forEntity.getStatusCode());
         //set the hotel to rating
            rating.setHotel(hotel);
         //return the rating
         return rating;
        }).collect(Collectors.toList());
        user.setRatings(ratingList);
        return user;
    }
}
