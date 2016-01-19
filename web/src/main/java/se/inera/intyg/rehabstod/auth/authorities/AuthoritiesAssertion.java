/*
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.auth.authorities;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Magnus Ekstrand on 16/09/15.
 */
public final class AuthoritiesAssertion {

    private AuthoritiesAssertion() {
    }

    /**
     * Assert that user has a role that matches a granted role.
     * If no role match then an IllegalAccessError will be thrown.
     *
     * @param grantedRoles the granted roles
     * @param userRoles user's current roles
     * @throws AuthoritiesException
     */
    public static void assertUserRoles(String[] grantedRoles, String[] userRoles) throws AuthoritiesException {
        List<String> gr = Arrays.asList(grantedRoles);

        for (String role : userRoles) {
            if (gr.contains(role)) {
                return;
            }
        }

        throw new AuthoritiesException(
            String.format("User does not have a valid role for current request. User's role must be one of [%s] but was [%s]",
                    StringUtils.join(grantedRoles, ","), StringUtils.join(userRoles, ",")));
    }

    /**
     * Assert that user has a privilege that matches a granted privilege.
     * If no privilege match then an IllegalAccessError will be thrown.
     *
     * @param grantedPrivileges the granted privileges
     * @param userPrivileges user's current privileges
     * @throws AuthoritiesException
     */
    public static void assertUserPrivileges(String[] grantedPrivileges, String[] userPrivileges) throws AuthoritiesException {
        List<String> gp = Arrays.asList(grantedPrivileges);

        for (String privilege : userPrivileges) {
            if (gp.contains(privilege)) {
                return;
            }
        }

        throw new AuthoritiesException(
                String.format("User does not have a valid role for current request. User's role must be one of [%s] but was [%s]",
                        StringUtils.join(grantedPrivileges, ","), StringUtils.join(grantedPrivileges, ",")));
    }

    /**
     * Assert that user has a request origin that matches the granted request origin.
     * If no request origin match then an IllegalAccessError will be thrown.
     *
     * @param grantedRequestOrigin the granted request origin
     * @param userRequestOrigin user's current request origin
     * @throws AuthoritiesException
     */
    public static void assertRequestOrigin(String grantedRequestOrigin, String userRequestOrigin) throws AuthoritiesException {
        if (grantedRequestOrigin.equals(userRequestOrigin)) {
            return;
        }

        throw new AuthoritiesException(
                String.format("User does not have a valid request origin for current task. User's request origin must be [%s] but was [%s]",
                        grantedRequestOrigin, userRequestOrigin));
    }

}
