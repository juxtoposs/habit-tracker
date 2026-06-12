package lt.viko.eif.habittracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/**
 * Configures HTTP caching support for REST API responses.
 */
@Configuration
public class CacheConfig {

    /**
     * Adds ETag headers to eligible HTTP responses.
     * Clients can use these ETags to check whether cached data is still current.
     *
     * @return shallow ETag filter
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }
}