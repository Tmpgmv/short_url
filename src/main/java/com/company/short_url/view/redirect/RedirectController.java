package com.company.short_url.view.redirect;

import com.company.short_url.app.OptionCache;
import com.company.short_url.entity.ShortUrl;
import io.jmix.core.DataManager;
import io.jmix.core.NoResultException;
import io.jmix.data.PersistenceHints;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {

  private final DataManager dataManager;

  public RedirectController(DataManager dataManager, OptionCache optionCache) {
    this.dataManager = dataManager;
  }

  @GetMapping("/s/{shortPath}")
  public ResponseEntity<?> redirectToFullUrl(@PathVariable String shortPath) {
    try {
      ShortUrl shortUrl = getShortUrl(shortPath);

      Integer redirectLimit = shortUrl.getRedirectLimit();
      int accessCount = shortUrl.getAccessCount();
      if (redirectLimit != null && accessCount >= redirectLimit) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Limit of transfers exceeded");
      }

      if (shortUrl.getDeletedDate() != null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Expired");
      }

      String whereTo = selectFullUrl(shortUrl, shortPath);

      incrementShortUrl(shortUrl);

      HttpHeaders headers = new HttpHeaders();
      headers.add("Location", whereTo);
      return new ResponseEntity<>(headers, HttpStatus.FOUND);

    } catch (NoResultException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short link not found");
    }
  }

  private void incrementShortUrl(ShortUrl shortUrl) {
    shortUrl.setAccessCount(shortUrl.getAccessCount() + 1);
    dataManager.save(shortUrl);
  }

  private ShortUrl getShortUrl(String shortPath) {
    return dataManager
        .load(ShortUrl.class)
        .query("e.shortUrl = ?1", shortPath)
        .hint(PersistenceHints.SOFT_DELETION, false)
        .one();
  }

  private String selectFullUrl(ShortUrl shortUrl, String shortPath) {
    if (shortUrl.getRedirectLimit() != null) {
      int redirectLimit = shortUrl.getRedirectLimit();
      ShortUrl resultShortUrl =
          dataManager
              .load(ShortUrl.class)
              .query("e.shortUrl = ?1 and e.accessCount <= ?2", shortPath, redirectLimit)
              .one();
      return resultShortUrl.getOriginalUrl();
    } else {
      return shortUrl.getOriginalUrl();
    }
  }
}
