package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.SimilarArtistDto;
import com.mitchmele.musicbrain_api.service.LastFmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    private final LastFmService lastFmService;

    public DiscoveryController(LastFmService lastFmService) {
        this.lastFmService = lastFmService;
    }

    @GetMapping("/suggested")
    public Mono<List<SimilarArtistDto>> getSuggestedArtists() {
        return lastFmService.getTopArtists(1, "overall")
                .flatMap(response -> {
                    if (response.getTopArtists() == null
                            || response.getTopArtists().getArtists() == null
                            || response.getTopArtists().getArtists().isEmpty()) {
                        return Mono.just(Collections.emptyList());
                    }
                    String topArtist = response.getTopArtists().getArtists().get(0).getName();
                    return lastFmService.getSimilarArtists(topArtist, 10)
                            .map(simResponse -> {
                                if (simResponse.getSimilarArtists() == null
                                        || simResponse.getSimilarArtists().getArtists() == null) {
                                    return Collections.<SimilarArtistDto>emptyList();
                                }
                                return simResponse.getSimilarArtists().getArtists();
                            });
                });
    }
}
