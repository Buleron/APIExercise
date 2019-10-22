package modules;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.sf.ehcache.CacheManager;
import play.api.inject.ApplicationLifecycle;

import java.util.concurrent.CompletableFuture;

@Singleton
public class StartupProvider {

    @Inject
    public StartupProvider(ApplicationLifecycle lifecycle) {
        lifecycle.addStopHook(() -> {
            CacheManager.getInstance().shutdown();
            return CompletableFuture.completedFuture(null);
        });
    }
}