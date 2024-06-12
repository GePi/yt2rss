package farm.giggle.yt2rss.controllers;

import lombok.Getter;
import lombok.NonNull;

public class UrlParam {
    @NonNull
    final private String name;

    @Getter
    final private Object value;

    static UrlParam of(@NonNull String name, Object value) {
        return new UrlParam(name, value);
    }

    public UrlParam(@NonNull String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @NonNull
    public String getName() {
        return name;
    }
}
