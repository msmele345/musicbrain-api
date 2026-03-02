package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.ArtistDto;
import com.mitchmele.musicbrain_api.service.LastFmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistsController {

    private final LastFmService lastFmService;

    @GetMapping("/top")
    public Mono<List<ArtistDto>> getTopArtists(
            @RequestParam(defaultValue = "7day") String period,
            @RequestParam(defaultValue = "10") int limit) {
        return lastFmService.getTopArtists(limit, period)
                .map(response -> {
                    if (response.getTopArtists() == null || response.getTopArtists().getArtists() == null) {
                        return Collections.emptyList();
                    }
                    return response.getTopArtists().getArtists();
                });
    }
}
