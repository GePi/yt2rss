package farm.giggle.yt2rss.youtube.services;


import farm.giggle.yt2rss.youtube.structure.YoutubeChannelView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeParser {
    private static final String CHANNEL_URL_PREFIX = "https://www.youtube.com/feeds/videos.xml?channel_id=";
    private static final Set<String> CHANNEL_PATTERNS = Set.of("/channel/([A-Za-z0-9_-]+)$", "channel_id=([A-Za-z0-9_-]+)$");
    private static final String CHANNEL_PATTERN_IN_HTML = "key\":\"browse_id\",\"value\":\"([^\"]+)\""; //"key":"browse_id","value":"UCz9iqx91UzlKg-WbBnlOR2g"
    private static final Set<String> PLAYLIST_PATTERNS = Set.of("playlist_id=([A-Za-z0-9_-]+)$");
    private static final int TIME_OUT = 5000;
    private final DateTimeFormatter youtubeDataTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final String channelId;
    private final Document rssDocument;

    public static YoutubeParser createFromUrl(String url) {
        final String channelId = extractChannelId(url);
        if (channelId == null) {
            return null;
        }
        Document rssDocument;
        try {
            rssDocument = Jsoup.connect(CHANNEL_URL_PREFIX + channelId).timeout(TIME_OUT).get();
        } catch (IOException e) {
            return null;
        }
        return new YoutubeParser(channelId, rssDocument);
    }

    private YoutubeParser(String channelId, Document rssDocument) {
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

    public List<YoutubeChannelView> getFileList() {
        List<YoutubeChannelView> youtubeChannelViews = new ArrayList<>();
        for (Element e : rssDocument.select("entry")) {
            YoutubeChannelView youtubeChannelView = new YoutubeChannelView();

            youtubeChannelView.setVideoId(e.select("yt|videoId").text());
            youtubeChannelView.setTitle(e.select("title").text());
            youtubeChannelView.setVideoUrl(e.select("link").attr("href"));

            String dateString = e.select("published").text();
            youtubeChannelView.setPublishedAt(OffsetDateTime.parse(dateString, youtubeDataTimeFormatter));
            dateString = e.select("updated").text();
            youtubeChannelView.setUpdatedAt(OffsetDateTime.parse(dateString, youtubeDataTimeFormatter));

            youtubeChannelViews.add(youtubeChannelView);
        }
        return youtubeChannelViews;
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
        //TODO проверить, что это адрес с ютуба

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
