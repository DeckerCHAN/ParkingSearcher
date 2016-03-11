package com.decker.parkingSearch.receiver;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class ParkingListReceiver implements Receiver {
    private HashMap<String, String> nameUrlList;
    private String baseUrl;

    public ParkingListReceiver(String baseUrl) {
        this.baseUrl = baseUrl;
        this.nameUrlList = new HashMap<String, String>();
    }

    public void fetch() throws IOException {
        Document doc = Jsoup.connect(this.getBaseUrl())
                .get();
        Elements elements = doc.select("a[href*=/caravan_parks_list/vic/]");
        for (Element element : elements) {
            if (StringUtils.isNotEmpty(element.attr("href")) && StringUtils.isNotEmpty(element.attr("title"))) {
                this.nameUrlList.put(element.attr("title"), element.attr("href"));
            }
        }
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HashMap<String, String> getList() {
        return nameUrlList;
    }

}
