package com.company.short_url.app;

import com.company.short_url.entity.ShortUrl;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("sh_ShortUrlEntityListener")
public class ShortUrlEntityListener {
    private final UrlShortenerService urlShortenerService;

    @Autowired
    public ShortUrlEntityListener(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @EventListener
    public void onEntitySaving(EntitySavingEvent<ShortUrl> event) {
        ShortUrl entity = event.getEntity();

        // Check if the entity is new and shortUrl hasn't been set yet
        if (event.isNewEntity() && (entity.getShortUrl() == null || entity.getShortUrl().isEmpty())) {
            String generatedShortUrl = urlShortenerService.createShortUrl();
            entity.setShortUrl(generatedShortUrl);
        }
    }
}
