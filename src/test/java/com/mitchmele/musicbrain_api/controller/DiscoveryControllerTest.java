package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.ArtistDto;
import com.mitchmele.musicbrain_api.dto.SimilarArtistDto;
import com.mitchmele.musicbrain_api.dto.SimilarArtistsResponse;
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

@WebMvcTest(DiscoveryController.class)
class DiscoveryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LastFmService lastFmService;

    @Test
    void getSuggestedArtists_returns200WithSimilarArtistList() {
        TopArtistsResponse topArtistsResponse = buildTopArtistsResponse("Portishead");
        when(lastFmService.getTopArtists(1, "overall")).thenReturn(Mono.just(topArtistsResponse));

        SimilarArtistDto massiveAttack = SimilarArtistDto.of("Massive Attack", 0.87, "https://last.fm/music/Massive+Attack", "https://img.jpg");
        SimilarArtistDto tricky = SimilarArtistDto.of("Tricky", 0.72, "https://last.fm/music/Tricky", null);
        SimilarArtistsResponse similarResponse = buildSimilarResponse(List.of(massiveAttack, tricky));
        when(lastFmService.getSimilarArtists("Portishead", 10)).thenReturn(Mono.just(similarResponse));

        webTestClient
                .get()
                .uri("/api/discovery/suggested")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SimilarArtistDto.class)
                .hasSize(2);
    }

    @Test
    void getSuggestedArtists_chainsTopArtistIntoSimilarArtistsCall() {
        TopArtistsResponse topArtistsResponse = buildTopArtistsResponse("Radiohead");
        when(lastFmService.getTopArtists(1, "overall")).thenReturn(Mono.just(topArtistsResponse));

        SimilarArtistDto similar = SimilarArtistDto.of("Thom Yorke", 0.95, "https://last.fm/music/Thom+Yorke", null);
        SimilarArtistsResponse similarResponse = buildSimilarResponse(List.of(similar));
        when(lastFmService.getSimilarArtists("Radiohead", 10)).thenReturn(Mono.just(similarResponse));

        webTestClient
                .get()
                .uri("/api/discovery/suggested")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Thom Yorke");
    }

    @Test
    void getSuggestedArtists_returnsEmptyListWhenNoTopArtist() {
        TopArtistsResponse emptyResponse = new TopArtistsResponse();
        emptyResponse.setTopArtists(null);
        when(lastFmService.getTopArtists(1, "overall")).thenReturn(Mono.just(emptyResponse));

        webTestClient
                .get()
                .uri("/api/discovery/suggested")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SimilarArtistDto.class)
                .hasSize(0);
    }

    private TopArtistsResponse buildTopArtistsResponse(String artistName) {
        TopArtistsResponse response = new TopArtistsResponse();
        TopArtistsResponse.TopArtists topArtists = new TopArtistsResponse.TopArtists();
        topArtists.setArtists(List.of(ArtistDto.of(artistName, "1000", "https://last.fm/music/" + artistName, null)));
        response.setTopArtists(topArtists);
        return response;
    }

    private SimilarArtistsResponse buildSimilarResponse(List<SimilarArtistDto> artists) {
        SimilarArtistsResponse response = new SimilarArtistsResponse();
        SimilarArtistsResponse.SimilarArtists similar = new SimilarArtistsResponse.SimilarArtists();
        similar.setArtists(artists);
        response.setSimilarArtists(similar);
        return response;
    }
}
