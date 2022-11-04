package be.rm.secu.tp1.util;

import javax.annotation.Nullable;
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

    public Object getOption(String key) {
        return options.get(key);
    }

    public Payload<T> copy() {
        return copy(null);
    }

    public Payload<T> copy(@Nullable T object) {
        Payload<T> payload;

        if (object != null) {
            payload = Payload.of(object);
        } else {
            payload = Payload.of(this.object);
        }

        for (var entry : options.entrySet()) {
            payload.withOption(entry.getKey(), entry.getValue());
        }

        return payload;
    }

    public Payload<T> withOption(String key, Object value) {
        options.put(key, value);
        return this;
    }
}
