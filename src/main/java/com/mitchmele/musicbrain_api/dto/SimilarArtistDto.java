package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimilarArtistDto {

    private String name;
    private double match;
    private String url;
    private String imageUrl;

    public SimilarArtistDto() {}

    public static SimilarArtistDto of(String name, double match, String url, String imageUrl) {
        SimilarArtistDto dto = new SimilarArtistDto();
        dto.name = name;
        dto.match = match;
        dto.url = url;
        dto.imageUrl = imageUrl;
        return dto;
    }

    public String getName() { return name; }
    public double getMatch() { return match; }
    public String getUrl() { return url; }
    public String getImageUrl() { return imageUrl; }

    public void setName(String name) { this.name = name; }
    public void setUrl(String url) { this.url = url; }

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
        if (images != null) {
            images.stream()
                    .filter(img -> "large".equals(img.get("size")))
                    .map(img -> img.get("#text"))
                    .findFirst()
                    .ifPresent(u -> this.imageUrl = u);
        }
    }
}
