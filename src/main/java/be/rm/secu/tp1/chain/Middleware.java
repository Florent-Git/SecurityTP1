package be.rm.secu.tp1.chain;

public abstract class Middleware<T> {
    private Middleware<T> _next;

    public static <T> Middleware<T> basic() {
        return new Middleware<>() {
            @Override
            public T operate(T object) {
                return object;
            }
        };
    }

    @SafeVarargs
    public static <T> Middleware<T> link(Middleware<T> first, Middleware<T> ...chain) {
        var head = first;

        for (var middleware : chain) {
            head._next = middleware;
            head = middleware;
        }

        return first;
    }

    public abstract T operate(T object);

    protected final T next(T object) {
        if (_next == null) return object;
        return _next.operate(object);
    }

    protected final T error(Throwable throwable) {
        // TODO: Si erreur
        return null;
    }
}
