package farm.giggle.yt2rss.youtube.services;


import farm.giggle.yt2rss.exceptions.ParserTimeoutException;
import farm.giggle.yt2rss.exceptions.URLSyntaxException;
import farm.giggle.yt2rss.youtube.dto.YoutubeEpisodeDTO;
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
    public static final String YT_PREFIX = "https://www.youtube.com/";
    private static final String CHANNEL_URL_PREFIX = "https://www.youtube.com/feeds/videos.xml?channel_id=";
    private static final Set<String> CHANNEL_PATTERNS = Set.of("/channel/([A-Za-z0-9_-]+)$", "channel_id=([A-Za-z0-9_-]+)$");
    private static final String CHANNEL_PATTERN_IN_HTML = "key\":\"browse_id\",\"value\":\"([^\"]+)\""; //"key":"browse_id","value":"UCz9iqx91UzlKg-WbBnlOR2g"
    private static final Set<String> PLAYLIST_PATTERNS = Set.of("playlist_id=([A-Za-z0-9_-]+)$");
    private static final Set<Pattern> YT_PREFIX_PATTERNS = Set.of(
            Pattern.compile("^https://www.youtube.com"),
            Pattern.compile("^https://youtube.com"),
            Pattern.compile("^www.youtube.com"),
            Pattern.compile("^youtube.com"));
    private static final int TIME_OUT_MC = 5000;
    private final DateTimeFormatter youtubeDataTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final String channelId;
    private final Document ytChannelPage;

    public static YoutubeParser createFromUrl(String url) {
        final String channelId = identifyYTChannelId(url);
        if (channelId == null) {
            throw new URLSyntaxException("Не удалось определить адрес канала Youtube");
        }
        Document ytPage;
        try {
            ytPage = Jsoup.connect(CHANNEL_URL_PREFIX + channelId).timeout(TIME_OUT_MC).get();
        } catch (IOException e) {
            throw new ParserTimeoutException(String.format("Не удалось загрузить страницу канала Youtube в течении %d сек.", TIME_OUT_MC / 1000) );
        }
        return new YoutubeParser(channelId, ytPage);
    }

    private YoutubeParser(String channelId, Document ytChannelPage) {
        this.channelId = channelId;
        this.ytChannelPage = ytChannelPage;
    }

    public String getUrl() {
        return CHANNEL_URL_PREFIX + channelId;
    }

    public String getTitle() {
        for (Element e : ytChannelPage.select("title")) {
            return e.text();
        }
        return "";
    }

    public List<YoutubeEpisodeDTO> getEpisodes() {
        List<YoutubeEpisodeDTO> youtubeEpisodeDTOS = new ArrayList<>();
        for (Element e : ytChannelPage.select("entry")) {
            YoutubeEpisodeDTO youtubeEpisodeDTO = new YoutubeEpisodeDTO();

            youtubeEpisodeDTO.setVideoId(e.select("yt|videoId").text());
            youtubeEpisodeDTO.setTitle(e.select("title").text());
            youtubeEpisodeDTO.setVideoUrl(e.select("link").attr("href"));

            String dateString = e.select("published").text();
            youtubeEpisodeDTO.setPublishedAt(OffsetDateTime.parse(dateString, youtubeDataTimeFormatter));
            dateString = e.select("updated").text();
            youtubeEpisodeDTO.setUpdatedAt(OffsetDateTime.parse(dateString, youtubeDataTimeFormatter));

            youtubeEpisodeDTOS.add(youtubeEpisodeDTO);
        }
        return youtubeEpisodeDTOS;
    }

    private static String identifyYTChannelId(String url) {
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
        return parseUrlContent(url, 0);
    }

    private static String parseUrlContent(String url, int level) {

        if (!isYouTubeLink(url)) return null;

        Document ytPage;
        try {
            ytPage = Jsoup.connect(url).timeout(TIME_OUT_MC).get();
        } catch (IOException e) {
            return null;
        }

        Pattern p = Pattern.compile(CHANNEL_PATTERN_IN_HTML);
        Matcher m = p.matcher(ytPage.outerHtml());
        if (m.find()) {
            return m.group(1);
        }

        // ссылка на "быстро подписаться"
        for (Element e : ytPage.select("yt-simple-endpoint")) {
            if (e.tagName().equals("a") &&
                    e.attr("href").startsWith("@") &&
                    level < 2) {
                return parseUrlContent(YT_PREFIX + e.attr("href"),level + 1 );
            }
        }
        return null;
    }

    private static boolean isYouTubeLink(String url) {
        for (Pattern p : YT_PREFIX_PATTERNS) {
            Matcher matcher = p.matcher(url);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }
}
