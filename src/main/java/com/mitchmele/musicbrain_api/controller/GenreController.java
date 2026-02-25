package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.TagDto;
import com.mitchmele.musicbrain_api.service.LastFmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final LastFmService lastFmService;

    public GenreController(LastFmService lastFmService) {
        this.lastFmService = lastFmService;
    }

    @GetMapping("/top")
    public Mono<List<TagDto>> getTopGenres() {
        return lastFmService.getTopTags(25)
                .map(response -> {
                    if (response.getTopTags() == null || response.getTopTags().getTags() == null) {
                        return Collections.emptyList();
                    }
                    return response.getTopTags().getTags();
                });
    }
}
