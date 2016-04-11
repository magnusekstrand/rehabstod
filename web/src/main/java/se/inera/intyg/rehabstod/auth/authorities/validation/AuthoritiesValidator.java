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
package se.inera.intyg.rehabstod.auth.authorities.validation;

import se.inera.intyg.rehabstod.auth.RehabstodUser;

/**
 * Utility class that makes it easy to express and enforce authority constraint rules in backend code.
 * Example usage could be:
 *
 * <pre>
 * authoritiesValidator.given(user, &quot;fk7263&quot;).
 *         features(&quot;HANTERA_UTKAST&quot;).
 *         roles(AuthoritiesConstants.ROLE_LAKARE, AuthoritiesConstants.ROLE_PRIVATLAKARE).
 *         notOrigins(RehabstodUserOriginType.UTHOPP).
 *         privilege(&quot;SkrivIntyg&quot;);
 * </pre>
 * <p/>
 * Created by marced on 14/12/15.
 */
public final class AuthoritiesValidator {

    /**
     * Create a expectation context with just a user and no intygstyp, i.e intygstyp doesnt' matter in validations.
     *
     * @param user
     * @return
     */
    public AuthExpectationSpecification given(RehabstodUser user) {
        return new AuthExpectationSpecImpl(user);
    }

}
