package com.mitchmele.musicbrain_api.service;

import com.mitchmele.musicbrain_api.dto.RecentTracksResponse;
import com.mitchmele.musicbrain_api.dto.SimilarArtistsResponse;
import com.mitchmele.musicbrain_api.dto.TopArtistsResponse;
import com.mitchmele.musicbrain_api.dto.TopTagsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class LastFmService {

    private final WebClient webClient;
    private final String apiKey;
    private final String username;

    public LastFmService(
            WebClient lastFmWebClient,
            @Value("${lastfm.api.key}") String apiKey,
            @Value("${lastfm.username}") String username) {
        this.webClient = lastFmWebClient;
        this.apiKey = apiKey;
        this.username = username;
    }

    @Cacheable("topTags")
    public Mono<TopTagsResponse> getTopTags(int limit) {
        return webClient.get()
                .uri(buildUserUri("user.getTopTags", limit))
                .retrieve()
                .bodyToMono(TopTagsResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    @Cacheable("topArtists")
    public Mono<TopArtistsResponse> getTopArtists(int limit, String period) {
        String uri = UriComponentsBuilder.newInstance()
                .queryParam("method", "user.getTopArtists")
                .queryParam("user", username)
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", limit)
                .queryParam("period", period)
                .build()
                .toUriString();
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(TopArtistsResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    @Cacheable("recentTracks")
    public Mono<RecentTracksResponse> getRecentTracks(int limit) {
        return webClient.get()
                .uri(buildUserUri("user.getRecentTracks", limit))
                .retrieve()
                .bodyToMono(RecentTracksResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    @Cacheable("similarArtists")
    public Mono<SimilarArtistsResponse> getSimilarArtists(String artist, int limit) {
        String uri = UriComponentsBuilder.newInstance()
                .queryParam("method", "artist.getSimilar")
                .queryParam("artist", artist)
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", limit)
                .build()
                .toUriString();
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(SimilarArtistsResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
    }

    private String buildUserUri(String method, int limit) {
        return UriComponentsBuilder.newInstance()
                .queryParam("method", method)
                .queryParam("user", username)
                .queryParam("api_key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", limit)
                .build()
                .toUriString();
    }
}
