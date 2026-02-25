package com.mitchmele.musicbrain_api.dto;

import lombok.Data;

@Data
public class TagDto {
    private String name;
    private int count;
    private String url;
}
