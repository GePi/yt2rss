package farm.giggle.yt2rss.web;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class UrlParams implements Iterable<UrlParam> {

    private final List<UrlParam> params = new ArrayList<>();

    public UrlParams() {
    }

    public static UrlParams of(@NonNull UrlParam... urlParam) {
        UrlParams urlParams = new UrlParams();
        Arrays.stream(urlParam).forEach(urlParams::addParam);
        return urlParams;
    }

    public void addParam(UrlParam urlParam) {
        params.add(urlParam);
    }

    public String getParamString() {
        if (params.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = getParamStringBuilder();
        if (stringBuilder.isEmpty()) {
            return "";
        }
        stringBuilder.insert(0, "&");
        return stringBuilder.toString();
    }

    public String getParamStringSeparated() {
        StringBuilder stringBuilder = getParamStringBuilder();
        if (stringBuilder.isEmpty()) {
            return "";
        }
        stringBuilder.insert(0, "?");
        return stringBuilder.toString();
    }

    @NonNull
    private StringBuilder getParamStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (UrlParam param : params) {
            if (param.getValue() != null) {
                stringBuilder.append(param.getName()).append("=").append(param.getValue().toString());
            }
        }
        return stringBuilder;
    }

    @NotNull
    @Override
    public Iterator<UrlParam> iterator() {
        return params.iterator();
    }

    @Override
    public void forEach(Consumer<? super UrlParam> action) {
        params.forEach(action);
    }

    @Override
    public Spliterator<UrlParam> spliterator() {
        return params.spliterator();
    }
}