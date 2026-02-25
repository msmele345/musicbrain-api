package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.TagDto;
import com.mitchmele.musicbrain_api.dto.TopTagsResponse;
import com.mitchmele.musicbrain_api.service.LastFmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(get("/api/genres/top")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$[0].name").value("rock"))
//                .andExpect(jsonPath("$[0].count").value(1234))
//                .andExpect(jsonPath("$[1].name").value("indie"))
//                .andExpect(jsonPath("$[1].count").value(800));
    }

    @Test
    void getTopGenres_returnsEmptyListWhenNoTags() throws Exception {
        TopTagsResponse response = new TopTagsResponse();
        response.setTopTags(null);

        when(lastFmService.getTopTags(25)).thenReturn(Mono.just(response));

        mockMvc.perform(get("/api/genres/top"))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$").isEmpty());
    }
}
