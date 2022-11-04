package be.rm.secu.tp1.util;

import java.util.Map;

public class Payload<T> {
    private Map<String, Object> options;
    private T object;

    private Payload(T object) {
        this.object = object;
    }

    public static <T> Payload<T> of(T object) {
        return new Payload<>(object);
    }

    public T getObject() {
        return object;
    }

    public Payload<T> withOption(String key, Object value) {
        options.put(key, value);
        return this;
    }
}
