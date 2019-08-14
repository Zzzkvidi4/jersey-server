package com.zzzkvidi4.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;

public final class Configuration extends ResourceConfig {
    @Inject
    Configuration(@NotNull ServiceLocator serviceLocator) {
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        Injector injector = Guice.createInjector(new ApplicationModule());
        guiceBridge.bridgeGuiceInjector(injector);
    }
}
