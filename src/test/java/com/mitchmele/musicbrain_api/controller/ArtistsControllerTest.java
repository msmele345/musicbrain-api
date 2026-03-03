package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.ArtistDto;
import com.mitchmele.musicbrain_api.dto.TopArtistsResponse;
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

@WebMvcTest(ArtistsController.class)
class ArtistsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LastFmService lastFmService;

    @Test
    void getTopArtists_returns200WithArtistList() {
        ArtistDto radiohead = artistDto("Radiohead", "1500", "https://last.fm/music/Radiohead", "https://img.jpg");
        ArtistDto portishead = artistDto("Portishead", "800", "https://last.fm/music/Portishead", null);

        TopArtistsResponse response = buildResponse(List.of(radiohead, portishead));
        when(lastFmService.getTopArtists(10, "14day")).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/artists/top")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ArtistDto.class)
                .hasSize(2);
    }

    @Test
    void getTopArtists_usesDefaultParamsWhenNoneProvided() {
        TopArtistsResponse response = buildResponse(List.of());
        when(lastFmService.getTopArtists(10, "14day")).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/artists/top")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ArtistDto.class)
                .hasSize(0);
    }

    @Test
    void getTopArtists_acceptsCustomPeriodAndLimit() {
        TopArtistsResponse response = buildResponse(List.of());
        when(lastFmService.getTopArtists(5, "overall")).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/artists/top?period=overall&limit=5")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getTopArtists_returnsEmptyListWhenNoArtists() {
        TopArtistsResponse response = new TopArtistsResponse();
        response.setTopArtists(null);
        when(lastFmService.getTopArtists(10, "14day")).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/artists/top")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ArtistDto.class)
                .hasSize(0);
    }

    private ArtistDto artistDto(String name, String playcount, String url, String imageUrl) {
        return ArtistDto.of(name, playcount, url, imageUrl);
    }

    private TopArtistsResponse buildResponse(List<ArtistDto> artists) {
        TopArtistsResponse response = new TopArtistsResponse();
        TopArtistsResponse.TopArtists topArtists = new TopArtistsResponse.TopArtists();
        topArtists.setArtists(artists);
        response.setTopArtists(topArtists);
        return response;
    }
}
