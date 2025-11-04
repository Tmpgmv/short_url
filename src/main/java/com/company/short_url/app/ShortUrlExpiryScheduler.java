package com.company.short_url.app;

import com.company.short_url.entity.ShortUrl;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("sh_ShortUrlExpiryScheduler")
public class ShortUrlExpiryScheduler {
  private final DataManager dataManager;
  private final SystemAuthenticator systemAuthenticator;

  public ShortUrlExpiryScheduler(DataManager dataManager, SystemAuthenticator systemAuthenticator) {
    this.dataManager = dataManager;
    this.systemAuthenticator = systemAuthenticator;
  }

  // Запускается каждую минуту
  @Scheduled(fixedRate = 60_000)
  @Transactional
  public void deleteExpiredShortUrls() {
    systemAuthenticator.withSystem(
        () -> {
          Date now = new Date();
          List<ShortUrl> expiredUrls =
              dataManager
                  .load(ShortUrl.class)
                  .query("e.deadline <= ?1 and e.deletedDate is null", now)
                  .list();

          for (ShortUrl expired : expiredUrls) {
            dataManager.remove(expired); // будет soft-delete если включено
          }
          return null;
        });
  }
}
