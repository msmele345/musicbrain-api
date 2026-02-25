package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TopTagsResponse {

    @JsonProperty("toptags")
    private TopTags topTags;

    @Data
    public static class TopTags {
        @JsonProperty("tag")
        private List<TagDto> tags;
    }
}
