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
package se.inera.intyg.rehabstod.auth;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.rehabstod.service.monitoring.MonitoringLogService;

/**
 * Created by marced on 13/04/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoggingSessionRegistryImplTest {

    private static final String SESSION_ID = "sessionId";
    @Mock
    private MonitoringLogService monitoringService;

    @InjectMocks
    private LoggingSessionRegistryImpl testee;

    RehabstodUser user;

    @Before
    public void before() {
        user = new RehabstodUser("hsaId", "En Användare");
        user.setAuthenticationScheme("my:auth");
    }

    @Test
    public void testRegisterNewSession() throws Exception {
        testee.registerNewSession(SESSION_ID, user);
        verify(monitoringService).logUserLogin(user.getHsaId(), user.getAuthenticationScheme());
    }

    @Test
    public void testRemoveSessionInformation() throws Exception {
        testee.registerNewSession(SESSION_ID, user);
        testee.removeSessionInformation(SESSION_ID);
        verify(monitoringService).logUserLogout(user.getHsaId(), user.getAuthenticationScheme());
    }
}
