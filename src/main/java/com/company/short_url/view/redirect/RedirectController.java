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

            boolean limitExceeded = shortUrl.getRedirectLimit() == shortUrl.getAccessCount();

            incrementShortUrl(shortUrl);

            if (limitExceeded) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Limit of transfers exceeded");
            }

            boolean expired = shortUrl.getDeletedDate() != null;

            if (expired) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Expired");
            }

            String whereTo = selectFullUrl(shortUrl, shortPath);

            // Return 302 Redirect
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", whereTo);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (NoResultException ex) {
            // Custom status and message for not found/deleted
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short link not found");
        }
    }

    private void incrementShortUrl(ShortUrl shortUrl) {
        int accessCount = shortUrl.getAccessCount();
        shortUrl.setAccessCount(++accessCount);
        dataManager.save(shortUrl);
    }

    private ShortUrl getShortUrl(String shortPath) {
        ShortUrl shortUrl =
                dataManager
                        .load(ShortUrl.class)
                        .query("e.shortUrl=?1", shortPath)
                        .hint(PersistenceHints.SOFT_DELETION, false)
                        .one();
        return shortUrl;
    }

    private String selectFullUrl(ShortUrl shortUrl, String shortPath) {
        ShortUrl resultShortUrl = null;

        if (shortUrl.getRedirectLimit() != null) {
            int redirectLimit = shortUrl.getRedirectLimit();
            resultShortUrl =
                    dataManager
                            .load(ShortUrl.class)
                            .query(
                                    "e.shortUrl=?1 " + "and e.deletedDate is null and e.accessCount < ?2",
                                    shortPath,
                                    redirectLimit)
                            .one();
        } else {
            resultShortUrl = shortUrl;
        }
        return resultShortUrl.getOriginalUrl();
    }
}
