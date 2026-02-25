package com.mitchmele.musicbrain_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LastFmWebClientConfig {

    @Bean
    public WebClient lastFmWebClient() {
        return WebClient.builder()
                .baseUrl("https://ws.audioscrobbler.com/2.0")
                .defaultHeader("User-Agent", "MusicBrain/1.0")
                .build();
    }
}
