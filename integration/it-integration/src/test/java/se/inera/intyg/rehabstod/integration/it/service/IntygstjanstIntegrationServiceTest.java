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
package se.inera.intyg.rehabstod.integration.it.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ListActiveSickLeavesForCareUnitResponseType;
import se.inera.intyg.clinicalprocess.healthcond.rehabilitation.listactivesickleavesforcareunit.v1.ResultCodeEnum;
import se.inera.intyg.rehabstod.integration.it.client.IntygstjanstClientService;
import se.inera.intyg.rehabstod.integration.it.exception.IntygstjanstIntegrationException;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsLista;

/**
 * Simple test.
 *
 * Created by eriklupander on 2016-02-01.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntygstjanstIntegrationServiceTest {

    private static final String HSA_ID = "123";
    private static final String HSA_ID_UNKNOWN = "456";

    @Mock
    private IntygstjanstClientService intygstjanstClientService;

    @InjectMocks
    private IntygstjanstIntegrationServiceImpl testee;

    @Test
    public void testGetIntygsDataForCareUnit() throws Exception {
        when(intygstjanstClientService.getSjukfall(HSA_ID)).thenReturn(buildResponse());
        List<IntygsData> intygsDataForCareUnit = testee.getIntygsDataForCareUnit(HSA_ID);
        assertEquals(1, intygsDataForCareUnit.size());
    }

    @Test(expected = IntygstjanstIntegrationException.class)
    public void testGetIntygsDataForCareUnitThrowsExceptionOnError() throws Exception {
        when(intygstjanstClientService.getSjukfall(HSA_ID_UNKNOWN)).thenReturn(buildErrorResponse());
        testee.getIntygsDataForCareUnit(HSA_ID_UNKNOWN);
    }

    private ListActiveSickLeavesForCareUnitResponseType buildResponse() {
        ListActiveSickLeavesForCareUnitResponseType response = new ListActiveSickLeavesForCareUnitResponseType();
        response.setResultCode(ResultCodeEnum.OK);

        IntygsLista intygsLista = new IntygsLista();
        intygsLista.getIntygsData().add(new IntygsData());

        response.setIntygsLista(intygsLista);
        return response;
    }

    private ListActiveSickLeavesForCareUnitResponseType buildErrorResponse() {
        ListActiveSickLeavesForCareUnitResponseType response = new ListActiveSickLeavesForCareUnitResponseType();
        response.setResultCode(ResultCodeEnum.ERROR);
        return response;
    }
}