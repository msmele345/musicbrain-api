package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimilarArtistDto {

    private String name;
    private double match;
    private String url;
    private String imageUrl;

    private SimilarArtistDto() {}

    public static SimilarArtistDto of(String name, double match, String url, String imageUrl) {
        SimilarArtistDto dto = new SimilarArtistDto();
        dto.name = name;
        dto.match = match;
        dto.url = url;
        dto.imageUrl = imageUrl;
        return dto;
    }


    @JsonProperty("match")
    public void setMatch(String match) {
        try {
            this.match = Double.parseDouble(match);
        } catch (NumberFormatException e) {
            this.match = 0.0;
        }
    }

    @JsonProperty("image")
    public void setImage(List<Map<String, String>> images) {
        LastFmImageUtils.extractLargeImageUrl(images).ifPresent(u -> this.imageUrl = u);
    }
}
