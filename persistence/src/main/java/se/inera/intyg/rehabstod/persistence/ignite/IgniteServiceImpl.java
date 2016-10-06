/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.persistence.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.cache.configuration.Factory;

/**
 * Created by eriklupander on 2016-10-06.
 */
@Service
public class IgniteServiceImpl implements IgniteService {

    private IgniteCache<String, String> userPreferences;

    @Value("${user.preferences.file}")
    private String userPreferenceFile;

    @PostConstruct
    public void init() {

        Ignite ignite = Ignition.start("example-ignite.xml");
        CacheConfiguration<String, String> cfg = new CacheConfiguration<>();
        cfg.setCacheMode(CacheMode.REPLICATED);
        cfg.setName("userpreferences");

        Factory<CacheStore<String, String>> factory = () -> new DiskStore(userPreferenceFile);

        cfg.setCacheStoreFactory(factory);
        cfg.setReadThrough(true);
        cfg.setWriteThrough(true);

        userPreferences = ignite.getOrCreateCache(cfg);
        IgniteBiPredicate<String, String> pred = (IgniteBiPredicate<String, String>) (s, s2) -> {
            userPreferences.put(s, s2);
            return true;
        };
        userPreferences.loadCache(pred);
    }

    @Override
    public String getValue(String hsaId, String key) {
        String compositeKey = buildKey(hsaId, key);
        if (userPreferences.containsKey(compositeKey)) {
            return userPreferences.get(hsaId + "-" + key);
        }
        return null;
    }

    @Override
    public void putValue(String hsaId, String key, String value) {
        userPreferences.put(hsaId + "-" + key, value);
    }

    @Override
    public void removeValue(String hsaId, String key) {
        userPreferences.remove(hsaId + "-" + key);
    }

    private String buildKey(String hsaId, String key) {
        return hsaId + "-" + key;
    }
}
