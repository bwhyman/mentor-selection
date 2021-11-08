package com.example.mentorselection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Slf4j
public class ReactiveTest {

    @Test
    public void test() {
        Mono<String> str1 = Mono.just("BO").map(s -> s.toLowerCase(Locale.ROOT));
        str1.subscribe(log::debug);
    }
}
