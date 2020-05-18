package modules;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.discovery.Discovery;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.management.javadsl.AkkaManagement;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import play.Logger;
import play.api.inject.ApplicationLifecycle;

import java.util.concurrent.CompletableFuture;

@Singleton
public class ApplicationStartProvider {

    @Inject
    public ApplicationStartProvider(ApplicationLifecycle lifecycle, Config config, ActorSystem system) {
        lifecycle.addStopHook(() -> {
            final Cluster cluster = Cluster.get(system);
            cluster.leave(cluster.selfAddress());
            return CompletableFuture.completedFuture(null);
        });

        String mode = config.getString("mode");
        System.out.println("------- Running in mode -------");
        System.out.println(mode);

        // Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();
        System.out.println("------- Heap utilization statistics [MB] -------");
        int mb = 1024 * 1024;
        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);
        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);

        if (mode.equals("production")) {
            Logger.debug("loading kubernetes api service discovery");
            Discovery.get(system).loadServiceDiscovery("kubernetes-api");
        }

        Logger.debug("Play SECRET");
        String secret = config.getString("play.http.secret.key");
        Logger.debug(secret);

        Logger.debug("SYSTEM NAME: {}", system.name());
        // Akka Management hosts the HTTP routes used by bootstrap
        AkkaManagement.get(system).start();

        // Starting the bootstrap process needs to be done explicitly
        ClusterBootstrap.get(system).start();

        final Cluster cluster = Cluster.get(system);
        cluster.registerOnMemberUp(() -> {
            Logger.debug("MEMBER IS UP");
        });
    }
}