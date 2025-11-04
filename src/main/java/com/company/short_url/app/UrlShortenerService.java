package com.company.short_url.app;

import com.company.short_url.entity.ShortUrl;
import io.jmix.core.DataManager;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component("sh_UrlShortenerService")
public class UrlShortenerService {
  private final Random rnd;
  private final String CHARS = "abcdefghijklmnopqrstuvwxyz1234567890-";
  private final DataManager dataManager;

  private final OptionCache optionCache;

  public UrlShortenerService(
      Map<String, String> urlMap, Random rnd, DataManager dataManager, OptionCache optionCache) {
    this.rnd = rnd;
    this.dataManager = dataManager;

    this.optionCache = optionCache;
  }

  public String createShortUrl() {

    String cndidatePath = "";

    do {

      char[] symbols = new char[optionCache.getOption().getUrlPathLength()];

      for (int i = 0; i < optionCache.getOption().getUrlPathLength(); i++) {
        int randomIndex = rnd.nextInt(CHARS.length());
        symbols[i] = CHARS.charAt(randomIndex);
        cndidatePath = new String(symbols);
      }

      if (cndidatePathUnique(cndidatePath)) {
        break;
      }

    } while (true);

    return cndidatePath;
  }

  private boolean cndidatePathUnique(String cndidatePath) {
    List<ShortUrl> shortUrl =
        dataManager.load(ShortUrl.class).query("e.shortUrl = ?1", cndidatePath).list();
    return shortUrl.isEmpty();
  }
}
