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
package se.inera.intyg.rehabstod.persistence.store;

import net.openhft.chronicle.map.ChronicleMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * Created by eriklupander on 2016-10-06.
 */
@Service
public class UserPreferenceStore {

    private static final int ENTRIES = 200;
    private static final double AVERAGE_KEY_SIZE = 40d;
    private static final double AVERAGE_VALUE_SIZE = 20d;

    @Value("${user.preferences.file}")
    private String userPreferenceFile;

    private ChronicleMap<String, String> userPreferences;

    @PostConstruct
    public void init() {

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

    public String getValue(String hsaId, String key) {
        String compositeKey = buildKey(hsaId, key);
        if (userPreferences.containsKey(compositeKey)) {
            return userPreferences.get(hsaId + "-" + key);
        }
        return null;
    }



    public void putValue(String hsaId, String key, String value) {
        userPreferences.put(hsaId + "-" + key, value);
    }

    public void removeValue(String hsaId, String key) {
         userPreferences.remove(hsaId + "-" + key);
    }

    private String buildKey(String hsaId, String key) {
        return hsaId + "-" + key;
    }
}
