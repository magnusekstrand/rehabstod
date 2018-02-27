/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

import redis.embedded.RedisServer;

// import org.apache.ignite.Ignition;

/**
 * Created by eriklupander on 2017-02-23.
 */
@Configuration
public class EmployeeNameCacheConfig {
    public static final String EMPLOYEE_NAME_CACHE_NAME = "employeeName";
    private static final String EMPLOYEE_NAME_CACHE_EXPIRY = "employee.name.cache.expiry";

    @Value("${" + EMPLOYEE_NAME_CACHE_EXPIRY + "}")
    private String employeeNameCacheExpirySeconds;

    @Autowired(required = false)
    private RedisServer redisServer;

    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    public void init() {
        cacheManager.getCache(EMPLOYEE_NAME_CACHE_NAME);
        ((RedisCacheManager) cacheManager).setDefaultExpiration(Integer.parseInt(employeeNameCacheExpirySeconds));
    }

    @PreDestroy
    public void tearDown() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
