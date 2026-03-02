package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.TrackDto;
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
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TracksController {

    private final LastFmService lastFmService;

    @GetMapping("/recent")
    public Mono<List<TrackDto>> getRecentTracks(
            @RequestParam(defaultValue = "20") int limit) {
        return lastFmService.getRecentTracks(limit)
                .map(response -> {
                    if (response.getRecentTracks() == null || response.getRecentTracks().getTracks() == null) {
                        return Collections.emptyList();
                    }
                    return response.getRecentTracks().getTracks();
                });
    }
}
