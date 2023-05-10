package farm.giggle.yt2rss.youtube;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Y2Rss {
    private static final String CHANNEL_URL_PREFIX = "https://www.youtube.com/feeds/videos.xml?channel_id=";
    private static final Set<String> CHANNEL_PATTERNS = Set.of("/channel/([A-Za-z0-9_-]+)$", "channel_id=([A-Za-z0-9_-]+)$");
    //"key":"browse_id","value":"UCz9iqx91UzlKg-WbBnlOR2g"
    private static final String CHANNEL_PATTERN_IN_HTML = "key\":\"browse_id\",\"value\":\"([^\"]+)\"";
    private static final Set<String> PLAYLIST_PATTERNS = Set.of("playlist_id=([A-Za-z0-9_-]+)$");

    private final String channelId;
    private final Document rssDocument;

    private Y2Rss(String channelId, Document rssDocument) {
        this.channelId = channelId;
        this.rssDocument = rssDocument;
    }

    public String getUrl() {
        return CHANNEL_URL_PREFIX + channelId;
    }

    public String getTitle() {
        for (Element e : rssDocument.select("title")) {
            return e.text();
        }
        return "";
    }

    public static Y2Rss fromUrl(String url) {
        String channelId = extractChannelId(url);
        if (channelId == null) {
            return null;
        }

        Document rssDocument;
        try {
            rssDocument = Jsoup.connect(CHANNEL_URL_PREFIX + channelId).timeout(5000).get();
            ;
        } catch (IOException e) {
            return null;
        }

        return new Y2Rss(channelId, rssDocument);
    }

    private static String extractChannelId(String url) {
        for (var pattern : CHANNEL_PATTERNS) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return m.group(1);
            }
        }

        for (var pattern : PLAYLIST_PATTERNS) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return m.group(1);
            }
        }

        return parseUrlContent(url);
    }

    private static String parseUrlContent(String url) {
        //TODO проверить что это адрес с ютуба

        Document htmlPageYoutube;
        try {
            htmlPageYoutube = Jsoup.connect(url).timeout(5000).get();
        } catch (IOException e) {
            return null;
        }

        Pattern p = Pattern.compile(CHANNEL_PATTERN_IN_HTML);
        Matcher m = p.matcher(htmlPageYoutube.outerHtml());
        while (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
