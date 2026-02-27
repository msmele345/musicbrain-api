package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimilarArtistsResponse {

    @JsonProperty("similarartists")
    private SimilarArtists similarArtists;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SimilarArtists {
        @JsonProperty("artist")
        private List<SimilarArtistDto> artists;
    }
}
