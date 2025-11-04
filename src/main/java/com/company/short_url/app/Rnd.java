package com.company.short_url.app;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("sh_Rnd")
public class Rnd {
    @Bean
    public Random getRandom() {
        return new Random();
    }
}
