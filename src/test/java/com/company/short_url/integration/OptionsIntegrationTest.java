package com.company.short_url.integration;


import com.company.short_url.test_support.AuthenticatedAsAdmin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(AuthenticatedAsAdmin.class)
public class OptionsIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void goodNumberOfSymbols() {
        TestRestTemplate adminTemplate = restTemplate.withBasicAuth("admin", "admin"); // замените пароль
        String response = adminTemplate.getForObject("http://localhost:" + port + "/options", String.class);
        assertThat(response).contains("Количество символов в короткой ссылке");
    }
}