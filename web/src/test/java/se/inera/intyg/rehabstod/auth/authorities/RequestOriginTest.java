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
package se.inera.intyg.rehabstod.auth.authorities;

import org.junit.Test;
import se.inera.intyg.common.security.common.model.RequestOrigin;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by marced on 14/04/16.
 */
public class RequestOriginTest {

    @Test
    public void testGetSetIntygstyper() throws Exception {

        // Arrange
        RequestOrigin requestOrigin = new RequestOrigin();
        requestOrigin.setIntygstyper(null);

        // Act / Assert
        assertNotNull(requestOrigin.getIntygstyper());

        List<String> list = Arrays.asList("typ1", "typ2");
        requestOrigin.setIntygstyper(list);

        // Act / Assert
        assertEquals(list, requestOrigin.getIntygstyper());
    }
}
