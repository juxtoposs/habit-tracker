package lt.viko.eif.habittracker.config;

import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

/**
 * Common cache settings used by API controllers.
 */
public final class CacheSettings {

    private CacheSettings() {
    }

    /**
     * Returns cache settings for normal read-only API responses.
     *
     * @return cache control configuration
     */
    public static CacheControl shortPrivateCache() {
        return CacheControl.maxAge(30, TimeUnit.SECONDS)
                .cachePrivate()
                .mustRevalidate();
    }
}