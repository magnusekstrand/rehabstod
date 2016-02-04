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
package se.inera.intyg.rehabstod.web.controller.api;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.certificate.SjukfallService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabUserMock;

    @Mock
    UserService userService;

    @Mock
    private SjukfallService sjukfallService;

    @InjectMocks
    private SjukfallController sjukfallController = new SjukfallController();

    @Before
    public void before() {
        when(userService.getUser()).thenReturn(rehabUserMock);
        when(rehabUserMock.getValdVardenhet()).thenReturn(new Vardenhet("123", "enhet"));
    }

    @Test
    public void testGetSjukfall() {
        // Given
        GetSjukfallRequest request = new GetSjukfallRequest();

        // When
        when(sjukfallService.getSjukfall(anyString(), any(GetSjukfallRequest.class))).thenReturn(new ArrayList<Sjukfall>());

        // Then
        List<Sjukfall> response = sjukfallController.getSjukfallForCareUnit(request);

        verify(sjukfallService).getSjukfall(anyString(), any(GetSjukfallRequest.class));
    }

}
