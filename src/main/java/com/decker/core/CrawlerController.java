package com.decker.core;

public class CrawlerController {
    private static CrawlerController _instance;

    private CrawlerController() {

    }

    public static CrawlerController get_instance() {
        if (_instance == null) {
            _instance = new CrawlerController();
        }
        return _instance;
    }
}
