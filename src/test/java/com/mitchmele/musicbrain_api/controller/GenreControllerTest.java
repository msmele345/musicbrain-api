package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.TagDto;
import com.mitchmele.musicbrain_api.dto.TopTagsResponse;
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

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LastFmService lastFmService;

    @Test
    void getTopGenres_returns200WithTagList() throws Exception {
        TagDto rock = new TagDto();
        rock.setName("rock");
        rock.setCount(1234);
        rock.setUrl("https://last.fm/tag/rock");

        TagDto indie = new TagDto();
        indie.setName("indie");
        indie.setCount(800);
        indie.setUrl("https://last.fm/tag/indie");

        TopTagsResponse response = new TopTagsResponse();
        TopTagsResponse.TopTags topTags = new TopTagsResponse.TopTags();
        topTags.setTags(List.of(rock, indie));
        response.setTopTags(topTags);

        when(lastFmService.getTopTags(25)).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/genres/top")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(TagDto.class)
                .hasSize(2)
                .contains(rock, indie);
    }

    @Test
    void getTopGenres_returnsEmptyListWhenNoTags() throws Exception {
        TopTagsResponse response = new TopTagsResponse();
        response.setTopTags(null);

        when(lastFmService.getTopTags(25)).thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri("/api/genres/top")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(TagDto.class)
                .hasSize(0);
    }
}
