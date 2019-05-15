package com.jey.webfluxdemo;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class WebfluxDemoApplicationTests {

    @Autowired
    ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testSaveUser() {
        User user = new User(null, "Mario", 33);
        Mono<User> mono = WebClient.create()
                .post()
                .uri("localhost:8080/user")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user), User.class)
                .retrieve()
                .bodyToMono(User.class);
        mono.block();
    }

    @Test
    public void testFindAll() {
        FluxExchangeResult<User> fluxExchangeResult = webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .returnResult(User.class);
        Assert.assertEquals(fluxExchangeResult.getStatus(), HttpStatus.OK);
        List<User> users = fluxExchangeResult.getResponseBody().collectList().block();

        Assert.assertNotNull(users);
        Assert.assertThat(users.size(), Matchers.greaterThan(0));
    }

}
