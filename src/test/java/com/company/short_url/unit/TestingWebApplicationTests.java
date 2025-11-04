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
     * 1 Пользователь перешел по истекшей ссылке. Должен увидеть сообщение "Expired".
     * @throws Exception
     */
    @Test
    void expiredLinkMessage() throws Exception {

        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/s/dvis5hc",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getBody().contains("Expired"));
    }


    /**
     * 2 Пользователь перешел по истекшей ссылке. Статус ответа сервера должен быть 403.
     * @throws Exception
     */
    @Test
    void expiredLinkStatus() throws Exception {

        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/s/dvis5hc",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    /**
     * 3 Пользователь перешел по работающей короткой ссылке. И должен увидеть
     * @throws Exception
     */
    @Test
    void goodLinkContent() throws Exception {

        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/s/grdlwsa",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getBody()).contains("Java");
    }

    /**
     * 4 Пользователь перешел по работающей короткой ссылке. И должен получить статус 200.
     * @throws Exception
     */
    @Test
    void goodLinkStatus() throws Exception {

        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/s/grdlwsa",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * 5 Пользователь перешел по ссылке с превышением лимита переходов. И должен получить статус 403.
     * @throws Exception
     */
    @Test
    void redirectLimitExceededStatus() throws Exception {

        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/s/gkdkpr0",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    /**
     * 6 Пользователь перешел по ссылке с превышением лимита переходов.
     * И должен получить сообщение "Limit of transfers exceeded".
     * @throws Exception
     */
    @Test
    void redirectLimitExceededMessage() throws Exception {

        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/s/gkdkpr0",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getBody().contains("Limit of transfers exceeded"));
    }


    /**
     * 7 В закешированных настройках сохранен URL данного сервиса.
     * @throws Exception
     */
    @Test
    void optionCacheShortenerUrl() throws Exception {
        assertThat(optionCache.getOption().getShortenerUrl().contains("localhost"));
    }


    /**
     * 8 В закешированных настройках сохранен срок действия ссылок.
     * @throws Exception
     */

    @Test
    void optionCacheLinkLifeTime() throws Exception {
        assertThat(optionCache.getOption().getLinkLifeTime().equals(1));
    }

    /**
     * 9 В закешированных настройках сохранена длина короткой ссылки.
     * @throws Exception
     */
    @Test
    void optionCacheUrlPathLength() throws Exception {
        assertThat(optionCache.getOption().getUrlPathLength().equals(7));
    }

    /**
     * 10 Неавторизованный пользователь попытался перейти на страницу /options
     * и должен увидеть страницу с текстом Log in.
     * @throws Exception
     */
    @Test
    void messageToUnauthorizedUserRedirectedToLoginPageWhenVisitingOptoins() throws Exception {
        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/options",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getBody().contains("Log in"));
    }

    /**
     * 11 Неавторизованный пользователь попытался перейти на страницу /options
     * и получить статус 302.
     * @throws Exception
     */
    @Test
    void statusToUnauthorizedUserRedirectedToLoginPageWhenVisitingOptoins() throws Exception {
        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/options",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode().equals(HttpStatus.OK));
    }


    /**
     * 12 Неавторизованный пользователь попытался перейти на страницу /short-urls
     * и увидеть страницу с текстом Log in.
     * @throws Exception
     */
    @Test
    void unauthorizedUserRedirectedToShortUrlPageWnenVisitingOptoins() throws Exception {
        ResponseEntity<String> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/short-urls",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getBody().contains("Log in"));
    }

    /**
     * 13 В базе данных и в кеше сохранен URL данного сервиса. Они должны совпадать.
     * @throws Exception
     */
    @Test
    void thisServiceUrlEqualsCachedServiceUrl() throws Exception {

        String optionUrl = loadOptionShortenerUrl();
        String cachedUrl = optionCache.getOption().getShortenerUrl();


        assertThat(optionUrl.equals(cachedUrl));
    }


    /**
     * 14 В базе данных и в кеше сохранен URL данного сервиса. Они должны совпадать.
     * @throws Exception
     */
    @Test
    void urlPathLengthEqualsCachedurlPathLength() throws Exception {

        int optionUrlPathLength = loadOptionUrlPathLength();
        int cachedUrlPathLength = optionCache.getOption().getUrlPathLength();


        assertThat(optionUrlPathLength == cachedUrlPathLength);
    }


    /**
     * 15 В базе данных и в кеше сохранен URL данного сервиса. Они должны совпадать.
     * @throws Exception
     */
    @Test
    void linkLifeTimeEqualsCachedurlLinkLifeTime() throws Exception {

        Long optionLinkLifeTime = loadOptionLinkLifeTime();
        Long cachedLinkLifeTime = optionCache.getOption().getLinkLifeTime();
        assertThat(optionLinkLifeTime == cachedLinkLifeTime);
    }




    private String loadOptionShortenerUrl() {
        return systemAuthenticator.withSystem(() ->
                dataManager.load(Option.class).all().one().getShortenerUrl()
        );
    }

    private Integer loadOptionUrlPathLength() {
        return systemAuthenticator.withSystem(() ->
                dataManager.load(Option.class).all().one().getUrlPathLength()
        );
    }

    private Long loadOptionLinkLifeTime() {
        return systemAuthenticator.withSystem(() ->
                dataManager.load(Option.class).all().one().getLinkLifeTime()
        );
    }
}


