package farm.giggle.yt2rss.youtube;

import java.io.File;
import java.io.IOException;

public class Y2Serv {
    static public void downloadVideo() {
        try {
            // вот так работает
            // ./yt-dlp -o "test video.%(ext)s" --extract-audio --audio-format=mp3 exexWtYNo9o

            ProcessBuilder pb = new ProcessBuilder("E:\\user\\petository\\yt2rss\\ytdlp\\yt-dlp.exe", "-o \"test.%(ext)s\"", "--extract-audio", "--audio-format=opus", "https://www.youtube.com/v/exexWtYNo9o?version=3");
            pb.redirectOutput(new File("E:\\user\\petository\\yt2rss\\ytdlp\\test003.txt"));
            pb.directory(new File("E:\\user\\petository\\yt2rss\\ytdlp\\"));
            Process p = pb.start();

            //"Process process = Runtime.getRuntime().exec("E:\\user\\petository\\yt2rss\\ytdlp.exe", null, new File("E:\\user\\petository\\yt2rss\\"));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
