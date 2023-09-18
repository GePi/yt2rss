package farm.giggle.yt2rss.youtube;

import farm.giggle.yt2rss.model.File;

public class RssToDBConverter {
    static public File RssFile2DBFile(RssFile rssFile) {

        File dbFile = new File();
        dbFile.setGuid(rssFile.getVideoId());
        dbFile.setOriginalUrl(rssFile.getVideoUrl());
        dbFile.setPublishedAt(rssFile.getPublishedAt());
        dbFile.setDowloadedAt(rssFile.getUpdatedAt());
        dbFile.setTitle(rssFile.getTitle());

        return dbFile;
    }
}
