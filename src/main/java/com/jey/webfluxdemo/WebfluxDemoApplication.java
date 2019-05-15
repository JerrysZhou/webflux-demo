package com.jey.webfluxdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author zhoujx
 * @date 2019-05-14
 */
@SpringBootApplication
public class WebfluxDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxDemoApplication.class, args);
    }

}

@Document(value = "User")
@Data
@AllArgsConstructor
@NoArgsConstructor
class User {

    @Id
    private String id;
    private String name;
    private Integer age;
}

@RestController
class UserReactiveController {

    private final UserReactiveService service;

    @Autowired
    UserReactiveController(UserReactiveService service) {
        this.service = service;
    }

    @GetMapping(value = "/greet")
    public Mono<String> greet(String name) {
        return Mono.just("hello " + name + "!");
    }

    @GetMapping(value = "/user/{id}")
    public Mono<User> findById(@PathVariable("id") String id) {
        return service.findById(id);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> findAll() {
        return service.findAll().delayElements(Duration.ofSeconds(1));
    }

    @PostMapping(value = "/user")
    public Mono<User> save(@RequestBody User user) {
        return service.save(user);
    }
}

@Service
class UserReactiveService {

    private final UserReactiveRepository userReactiveRepository;

    @Autowired
    UserReactiveService(UserReactiveRepository userReactiveRepository) {
        this.userReactiveRepository = userReactiveRepository;
    }

    public Mono<User> findById(String id) {
        return userReactiveRepository.findById(id);
    }

    public Flux<User> findAll() {
        return userReactiveRepository.findAll();
    }

    public Mono<User> save(User user) {
        return userReactiveRepository.save(user);
    }

}

interface UserReactiveRepository extends ReactiveMongoRepository<User, String> {

}

