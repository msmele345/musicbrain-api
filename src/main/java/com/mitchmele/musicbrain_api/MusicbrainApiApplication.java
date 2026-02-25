package com.mitchmele.musicbrain_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MusicbrainApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicbrainApiApplication.class, args);
	}

}
