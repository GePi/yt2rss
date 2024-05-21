package farm.giggle.yt2rss.config.security;

public enum Auth2ProviderEnum {
    GOOGLE, GITHUB, YANDEX;
    public static final int MAX_LEN_PROVIDER_NAME = 10;
    public static final int MAX_LEN_PROVIDER_ID = 32;
}
