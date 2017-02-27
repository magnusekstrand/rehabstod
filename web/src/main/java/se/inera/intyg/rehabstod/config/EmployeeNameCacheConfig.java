package se.inera.intyg.rehabstod.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignition;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import se.inera.intyg.infra.cache.core.ConfigurableCache;

/**
 * Created by eriklupander on 2017-02-23.
 */
@Configuration
public class EmployeeNameCacheConfig implements ConfigurableCache {
    public static final String EMPLOYEE_NAME_CACHE_NAME = "employeeName";
    private static final String EMPLOYEE_NAME_CACHE_EXPIRY = "employee.name.cache.expiry";

    @Value("${" + EMPLOYEE_NAME_CACHE_EXPIRY + "}")
    private String employeeNameCacheExpirySeconds;

    @Autowired
    private SpringCacheManager cacheManager;

    @PostConstruct
    public void init() {
        Duration employeeNameDuration = buildDuration(employeeNameCacheExpirySeconds, EMPLOYEE_NAME_CACHE_EXPIRY);

        initCache(EMPLOYEE_NAME_CACHE_NAME, employeeNameDuration);

    }

    @PreDestroy
    public void tearDown() {
        Ignition.stopAll(false);
    }

    private void initCache(String cacheName, Duration duration) {
        cacheManager.getDynamicCacheConfiguration().setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
        cacheManager.getCache(cacheName);
    }
}
