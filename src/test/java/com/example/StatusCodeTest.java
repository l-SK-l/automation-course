package com.example;

import com.example.config.EnvironmentConfig;
import com.microsoft.playwright.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeTest {
    private EnvironmentConfig config;
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    public void setup() {
        config = ConfigFactory.create(EnvironmentConfig.class, System.getenv());
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    public void test200StatusCode() {
        int statusCode = page.navigate(config.baseUrl() + "/status_codes/200").status();
        assertEquals(200, statusCode);
    }

    @Test
    public void test404StatusCode() {
        int statusCode = page.navigate(config.baseUrl() + "/status_codes/404").status();
        assertEquals(404, statusCode);
    }

    @Test
    public void test500StatusCode() {
        int statusCode = page.navigate(config.baseUrl() + "/status_codes/500").status();
        assertEquals(500, statusCode);
    }

    @AfterEach
    public void teardown() {
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}