package com.mitchmele.musicbrain_api.controller;

import com.mitchmele.musicbrain_api.dto.SimilarArtistDto;
import com.mitchmele.musicbrain_api.dto.TopArtistsResponse;
import com.mitchmele.musicbrain_api.service.LastFmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final LastFmService lastFmService;

    @GetMapping("/suggested")
    public Mono<List<SimilarArtistDto>> getSuggestedArtists() {
        return lastFmService.getTopArtists(1, "overall")
                .flatMap(response -> {
                    if (isEmptyArtistResponse(response)) {
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

    private boolean isEmptyArtistResponse(TopArtistsResponse res) {
        return isNull(res.getTopArtists()) || isEmpty(res.getTopArtists().getArtists());
    }
}
