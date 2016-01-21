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

package se.inera.intyg.rehabstod.auth.authorities.bootstrap;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConfiguration;
import se.inera.intyg.rehabstod.auth.authorities.Privilege;
import se.inera.intyg.rehabstod.auth.authorities.RequestOrigin;
import se.inera.intyg.rehabstod.auth.authorities.Role;
import se.inera.intyg.rehabstod.auth.authorities.Title;
import se.inera.intyg.rehabstod.auth.authorities.TitleCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AuthoritiesConfigurationLoaderTest {

    private static final String authoritiesConfigurationFile = "AuthoritiesConfigurationLoaderTest/authorities-test.yaml";
    private static final String authoritiesConfigurationOutputFile = "AuthoritiesConfigurationLoaderTest/authorities-output.txt";

    @InjectMocks
    AuthoritiesConfigurationLoader loader = new AuthoritiesConfigurationLoader(authoritiesConfigurationFile);

    @Before
    public void setupAuthoritiesConfiguration() {
        // When
        try {
            loader.afterPropertiesSet();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void loadConfigurationAndAssertTypeOfObjects() {
        AuthoritiesConfiguration configuration = loader.getConfiguration();

        assertTrue(configuration.getRequestOrigins().size() == 1);
        assertTrue(configuration.getPrivileges().size() == 1);
        assertTrue(configuration.getRoles().size() == 2);
        assertTrue(configuration.getTitles().size() == 2);
        assertTrue(configuration.getTitleCodes().size() == 4);

        // Assert that lists are of specific types
        try {
            List<RequestOrigin> requestOrigins = (List<RequestOrigin>) configuration.getRequestOrigins();
            List<Privilege> privileges = (List<Privilege>) configuration.getPrivileges();
            List<Role> roles = (List<Role>) configuration.getRoles();
            List<Title> titles = (List<Title>) configuration.getTitles();
            List<TitleCode> titleCodes = (List<TitleCode>) configuration.getTitleCodes();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void loadConfigurationAndAssertString() {
        AuthoritiesConfiguration configuration = loader.getConfiguration();

        String actual = configuration.toString().replaceAll("\\s","").trim();
        String expected = "";

        try {
            Resource resource = getResource(authoritiesConfigurationOutputFile);
            expected = new String(Files.readAllBytes(Paths.get(resource.getURI()))).replaceAll("\\s","").trim();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadConfigurationWithBadLocation() {
        AuthoritiesConfigurationLoader loader = new AuthoritiesConfigurationLoader(null);
    }


    // ~ Private scope
    // ======================================================================================================

    private Resource getResource(String location) {
        PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
        return r.getResource(location);
    }

}