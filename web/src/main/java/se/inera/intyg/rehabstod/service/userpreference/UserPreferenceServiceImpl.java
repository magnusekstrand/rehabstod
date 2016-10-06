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
package se.inera.intyg.rehabstod.service.userpreference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.persistence.store.UserPreferenceStore;
import se.inera.intyg.rehabstod.service.user.UserService;

/**
 * Created by eriklupander on 2016-10-06.
 */
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Autowired
    private UserPreferenceStore userPreferenceStore;

    @Autowired
    private UserService userService;

    @Override
    public String getValue(String key) {
        RehabstodUser user = getUser();
        return userPreferenceStore.getValue(user.getHsaId(), key);
    }

    @Override
    public void putValue(String key, String value) {
        RehabstodUser user = getUser();
        userPreferenceStore.putValue(user.getHsaId(), key, value);
    }

    @Override
    public void removeValue(String key) {
        RehabstodUser user = getUser();
        userPreferenceStore.removeValue(user.getHsaId(), key);
    }

    private RehabstodUser getUser() {
        RehabstodUser user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("Cannot remove user preference, no user session in context");
        }
        return user;
    }
}
