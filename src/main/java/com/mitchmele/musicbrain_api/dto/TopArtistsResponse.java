package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopArtistsResponse {

    @JsonProperty("topartists")
    private TopArtists topArtists;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TopArtists {
        @JsonProperty("artist")
        private List<ArtistDto> artists;
    }
}
