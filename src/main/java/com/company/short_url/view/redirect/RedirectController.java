package com.company.short_url.view.redirect;

import com.company.short_url.app.OptionCache;
import com.company.short_url.entity.ShortUrl;
import io.jmix.core.DataManager;
import io.jmix.core.NoResultException;
import io.jmix.core.UnconstrainedDataManager;
import io.jmix.data.PersistenceHints;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.eclipse.persistence.config.QueryType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {

    @PersistenceContext
    private EntityManager entityManager;

  private final UnconstrainedDataManager dataManager;

  public RedirectController(UnconstrainedDataManager dataManager, OptionCache optionCache) {
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
      Query query = entityManager.createNativeQuery(
              "select * from SH_SHORT_URL where SHORT_URL = ?1", ShortUrl.class);
      query.setParameter(1, shortPath);
      ShortUrl result = (ShortUrl) query.getSingleResult();
      return result;
  }


    @Transactional
    public String selectFullUrl(ShortUrl shortUrl, String shortPath) {
        if (shortUrl.getRedirectLimit() != null) {
            int redirectLimit = shortUrl.getRedirectLimit();
            entityManager.setProperty(PersistenceHints.SOFT_DELETION, false); // to include soft-deleted rows if needed

            ShortUrl resultShortUrl = (ShortUrl) entityManager
                    .createNativeQuery(
                            "select * from SH_SHORT_URL where SHORT_URL = ?1 and ACCESS_COUNT <= ?2", ShortUrl.class)
                    .setParameter(1, shortPath)
                    .setParameter(2, redirectLimit)
                    .getSingleResult();

            return resultShortUrl.getOriginalUrl();
        } else {
            return shortUrl.getOriginalUrl();
        }
    }
}
