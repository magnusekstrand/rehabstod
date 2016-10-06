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

import net.openhft.chronicle.map.ChronicleMap;
import org.apache.ignite.lang.IgniteBiInClosure;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Ignite CacheStore implementation using ChronicleMap as backend, enabling safe disk-based storage of entries.
 *
 * Created by eriklupander on 2016-10-06.
 */
public class DiskStore implements org.apache.ignite.cache.store.CacheStore<String, String> {

    private static final int ENTRIES = 200;
    private static final double AVERAGE_KEY_SIZE = 40d;
    private static final double AVERAGE_VALUE_SIZE = 20d;

    private ChronicleMap<String, String> userPreferences;

    public DiskStore(String userPreferenceFile) {
        try {
            userPreferences = ChronicleMap
                    .of(String.class, String.class)
                    .entries(ENTRIES)
                    .averageValueSize(AVERAGE_VALUE_SIZE)
                    .averageKeySize(AVERAGE_KEY_SIZE)
                    .createPersistedTo(new File(userPreferenceFile));
        } catch (IOException e) {
            throw new RuntimeException("Unable to bootstrap disk persistent user preference store: " + e.getMessage());
        }
    }

    @Override
    public void loadCache(IgniteBiInClosure<String, String> clo, Object... args) throws CacheLoaderException {
        for (String key: userPreferences.keySet()) {
            clo.apply(key, userPreferences.get(key));
        }
    }

    @Override
    public Map<String, String> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
        Map<String, String> m = new HashMap<>();
        for (String key : keys) {
            m.put(key, userPreferences.get(key));
        }
        return m;
    }

    @Override
    public void write(Cache.Entry<? extends String, ? extends String> entry) throws CacheWriterException {
        userPreferences.put(entry.getKey(), entry.getValue());
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        if (userPreferences.containsKey(key)) {
            userPreferences.remove(key);
        }
    }

    @Override
    public void deleteAll(Collection keys) throws CacheWriterException {
        for (Object key : keys) {
            userPreferences.remove(key);
        }
    }

    @Override
    public void writeAll(Collection<Cache.Entry<? extends String, ? extends String>> collection) throws CacheWriterException {
        for (Cache.Entry<? extends String, ? extends String> e : collection) {
           userPreferences.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public String load(String key) throws CacheLoaderException {
        return userPreferences.get(key);
    }



    @Override
    public void sessionEnd(boolean commit) throws CacheWriterException {

    }
}
