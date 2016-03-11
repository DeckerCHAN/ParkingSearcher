/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Derek.CHAN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.decker.parkingSearch.receiver;

import com.decker.parkingSearch.entires.CityInfo;
import com.decker.parkingSearch.entires.Park;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParkingContentReceiver implements Receiver {
    private String baseUrl;
    private String name;
    private CityInfo info;

    public ParkingContentReceiver(String name, String baseUrl) {
        this.baseUrl = baseUrl;
        this.name = name;
        info = new CityInfo();
        info.parks = new ArrayList<Park>();
        info.name = name;
        info.url = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void fetch() throws IOException {

        Document doc = Jsoup.connect(this.baseUrl).get();
        Elements detailBox = doc.select("td[style=\"vertical-align:top;\"]");

        for (Element es : detailBox) {
            try {
                Park detail = new Park();
                detail.name = es.childNode(0) instanceof Element ? ((Element) es.childNode(0)).text() : "";
                detail.address = "";
                for (int i = 1; i < es.childNodes().size() - 1; i++) {
                    String content = (es.childNodes().get(i)).toString();
                    if (content.equals((es.childNodes().get(i + 1)).toString())) {
                        break;
                    } else {

                        if (!content.equals("<br>")) {
                            detail.address += (StringEscapeUtils.unescapeHtml(content) + " ");
                        }
                    }
                }
                if (es.select("span > a").size() == 0) {
                    continue;
                }
                String secretContent = StringEscapeUtils.unescapeHtml(es.select("span > a").get(0).attr("href").replaceAll("\"", ""));
                Matcher matcher = Pattern.compile("(?<=javascript\\:count\\().*(?=\\))").matcher(secretContent);
                String[] secretInfoList;
                if (matcher.find()) {
                    secretInfoList = matcher.group().split(",");
                } else {
                    continue;
                }
                String mobContent = Jsoup.connect(String.format("http://www.goseeaustralia.com.au/statslookup.asp?keyID=%s&StatID=0", secretInfoList[1])).get().text();
                detail.mobileNumber = StringUtils.isNotBlank(mobContent) ? mobContent : "";
                String phoneContent = Jsoup.connect(String.format("http://www.goseeaustralia.com.au/statslookup.asp?keyID=%s&StatID=1", secretInfoList[1])).get().text();
                detail.phoneNumber = StringUtils.isNotBlank(phoneContent) ? phoneContent : "";
                String faxContent = Jsoup.connect(String.format("http://www.goseeaustralia.com.au/statslookup.asp?keyID=%s&StatID=2", secretInfoList[1])).get().text();
                detail.faxNumber = StringUtils.isNotBlank(faxContent) ? faxContent : "";
                detail.email = StringUtils.isNotBlank(secretInfoList[2]) ? secretInfoList[2] : "";
                this.info.parks.add(detail);
            } catch (Exception ex) {
                System.out.printf("Error during fetch %s park with url %s %n", es.childNode(0).toString(), this.baseUrl);
                ex.printStackTrace();
            }
        }


    }

    public CityInfo getInfo() {
        return this.info;
    }
}
