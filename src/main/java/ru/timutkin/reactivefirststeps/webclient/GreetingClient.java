package ru.timutkin.reactivefirststeps.webclient;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.timutkin.reactivefirststeps.dto.Greeting;

@Component
public class GreetingClient {

    private final WebClient webClient;

    public GreetingClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:2000")
                .build();
    }

    public Mono<String> getMessage(){
        return this.webClient.get()
                .uri("/hello")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Greeting.class)
                .map(Greeting::getMessage);
    }
}
