package be.rm.secu.tp1.net;

import java.util.concurrent.Callable;

public interface CallableFactory<T, R> {
    Callable<R> createCallable(T parameter);
}
