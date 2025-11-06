package com.company.short_url.unit;

import com.company.short_url.app.OptionCache;
import com.company.short_url.entity.Option;
import com.company.short_url.view.redirect.RedirectController;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestingWebApplicationTests {
    @Autowired
    private RedirectController controller;

    @LocalServerPort
    private int port;

    @Autowired
    private OptionCache optionCache;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    /**
     * 1. Пользователь перешел по истекшей ссылке. Должен увидеть сообщение "Expired".
     */
    @Test
    void expiredLinkMessage() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/s/exp", HttpMethod.GET, null, String.class);
        assertThat(response.getBody()).contains("Expired");
    }

    /**
     * 2. Пользователь перешел по истекшей ссылке. Статус ответа сервера должен быть 403.
     */
    @Test
    void expiredLinkStatus() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/s/exp", HttpMethod.GET, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * 3. Пользователь перешел по работающей короткой ссылке. И должен увидеть "Java".
     */
    @Test
    void goodLinkContent() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/s/good", HttpMethod.GET, null, String.class);
        assertThat(response.getBody()).contains("pcask");
    }

    /**
     * 4. Пользователь перешел по работающей короткой ссылке. И должен получить статус 200.
     */
    @Test
    void goodLinkStatus() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/s/good", HttpMethod.GET, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * 5. Пользователь перешел по ссылке с превышением лимита переходов. И должен получить статус 403.
     */
    @Test
    void redirectLimitExceededStatus() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/s/limit", HttpMethod.GET, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /**
     * 6. Пользователь перешел по ссылке с превышением лимита переходов. И должен получить сообщение
     * "Limit of transfers exceeded".
     */
    @Test
    void redirectLimitExceededMessage() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/s/limit", HttpMethod.GET, null, String.class);
        assertThat(response.getBody()).contains("Limit of transfers exceeded");
    }

    /**
     * 7. В закешированных настройках сохранен URL данного сервиса.
     */
    @Test
    void optionCacheShortenerUrl() {
        assertThat(optionCache.getOption().getShortenerUrl()).contains("localhost");
    }

    /**
     * 8. В закешированных настройках сохранен срок действия ссылок.
     */
    @Test
    void optionCacheLinkLifeTime() {
        assertThat(optionCache.getOption().getLinkLifeTime().equals(1));
    }

    /**
     * 9. В закешированных настройках сохранена длина короткой ссылки.
     */
    @Test
    void optionCacheUrlPathLength() {
        assertThat(optionCache.getOption().getUrlPathLength()).isEqualTo(4);
    }

    /**
     * 10. Неавторизованный пользователь попытался перейти на страницу /options и должен увидеть
     * страницу с текстом Log in.
     */
    @Test
    void messageToUnauthorizedUserRedirectedToLoginPageWhenVisitingOptions() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/options", HttpMethod.GET, null, String.class);
        assertThat(response.getBody().contains("login"));
    }

    /**
     * 11. Неавторизованный пользователь попытался перейти на страницу /options и получить статус
     * 200
     * (т.е. редирект на логин произошёл).
     */
    @Test
    void statusToUnauthorizedUserRedirectedToLoginPageWhenVisitingOptions() {
        ResponseEntity<String> response =
                restTemplate.exchange(
                        "http://localhost:" + port + "/options", HttpMethod.GET, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    /**
     * 12. В базе данных и в кеше сохранен URL данного сервиса. Они должны совпадать.
     */
    @Test
    void thisServiceUrlEqualsCachedServiceUrl() {
        String optionUrl = loadOptionShortenerUrl();
        String cachedUrl = optionCache.getOption().getShortenerUrl();
        assertThat(optionUrl).isEqualTo(cachedUrl);
    }

    /**
     * 13. В базе данных и в кеше сохранена длина короткой ссылки. Они должны совпадать.
     */
    @Test
    void urlPathLengthEqualsCachedUrlPathLength() {
        int optionUrlPathLength = loadOptionUrlPathLength();
        int cachedUrlPathLength = optionCache.getOption().getUrlPathLength();
        assertThat(optionUrlPathLength).isEqualTo(cachedUrlPathLength);
    }

    /**
     * 14. В базе данных и в кеше сохранен срок действия ссылок. Они должны совпадать.
     */
    @Test
    void linkLifeTimeEqualsCachedUrlLinkLifeTime() {
        long optionLinkLifeTime = loadOptionLinkLifeTime();
        long cachedLinkLifeTime = optionCache.getOption().getLinkLifeTime();
        assertThat(optionLinkLifeTime).isEqualTo(cachedLinkLifeTime);
    }

    private String loadOptionShortenerUrl() {
        return systemAuthenticator.withSystem(
                () -> dataManager.load(Option.class).all().one().getShortenerUrl());
    }

    private int loadOptionUrlPathLength() {
        return systemAuthenticator.withSystem(
                () -> dataManager.load(Option.class).all().one().getUrlPathLength());
    }

    private long loadOptionLinkLifeTime() {
        return systemAuthenticator.withSystem(
                () -> dataManager.load(Option.class).all().one().getLinkLifeTime());
    }
}
