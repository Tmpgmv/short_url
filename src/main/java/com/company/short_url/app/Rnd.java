package com.company.short_url.app;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component("sh_Rnd")
public class Rnd {
  @Bean
  public Random getRandom() {
    return new Random();
  }
}
