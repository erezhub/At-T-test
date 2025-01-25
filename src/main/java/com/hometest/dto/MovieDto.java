package com.hometest.dto;

import lombok.Data;

@Data
public class MovieDto {
    private String title;
    private String genre;
    private Integer duration;
    private Integer rating;
    private Integer releaseYear;
}
