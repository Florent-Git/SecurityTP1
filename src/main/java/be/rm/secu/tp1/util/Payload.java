package be.rm.secu.tp1.util;

public record Payload<T>(
    T object,
    Object source
) {
    public static <T> Payload<T> of(T object, Object source) {
        return new Payload<>(object, source);
    }
}
