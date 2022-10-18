package be.rm.secu.tp1;

import be.rm.secu.tp1.net.Server;
import com.google.inject.AbstractModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DIModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExecutorService.class)
            .toInstance(Executors.newFixedThreadPool(2));

        bind(Server.class).to(Server.class);
    }
}
