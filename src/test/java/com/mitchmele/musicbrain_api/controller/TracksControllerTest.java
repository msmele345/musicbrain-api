package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.RecentTracksResponse;
import com.mitchmele.musicbrain_api.dto.TrackDto;
import com.mitchmele.musicbrain_api.service.LastFmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(TracksController.class)
class TracksControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LastFmService lastFmService;

    @Test
    void getRecentTracks_returns200WithTrackList() {
        TrackDto nowPlaying = trackDto("Fake Plastic Trees", "Radiohead", "The Bends", null, true);
        TrackDto recent = trackDto("Glory Box", "Portishead", "Dummy", "14 Nov 2023, 20:00", false);

        RecentTracksResponse response = buildResponse(List.of(nowPlaying, recent));
        when(lastFmService.getRecentTracks(20)).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/tracks/recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackDto.class)
                .hasSize(2);
    }

    @Test
    void getRecentTracks_returnsNowPlayingFlag() {
        TrackDto nowPlaying = trackDto("Creep", "Radiohead", "Pablo Honey", null, true);

        RecentTracksResponse response = buildResponse(List.of(nowPlaying));
        when(lastFmService.getRecentTracks(20)).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/tracks/recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].nowPlaying").isEqualTo(true);
    }

    @Test
    void getRecentTracks_returnsEmptyListWhenNoTracks() {
        RecentTracksResponse response = new RecentTracksResponse();
        response.setRecentTracks(null);
        when(lastFmService.getRecentTracks(20)).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/tracks/recent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrackDto.class)
                .hasSize(0);
    }

    @Test
    void getRecentTracks_acceptsCustomLimit() {
        RecentTracksResponse response = buildResponse(List.of());
        when(lastFmService.getRecentTracks(5)).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/tracks/recent?limit=5")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    private TrackDto trackDto(String name, String artist, String album, String timestamp, boolean nowPlaying) {
        return TrackDto.of(name, artist, album, timestamp, nowPlaying);
    }

    private RecentTracksResponse buildResponse(List<TrackDto> tracks) {
        RecentTracksResponse response = new RecentTracksResponse();
        RecentTracksResponse.RecentTracks recentTracks = new RecentTracksResponse.RecentTracks();
        recentTracks.setTracks(tracks);
        response.setRecentTracks(recentTracks);
        return response;
    }
}
