/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import com.itextpdf.text.DocumentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityStore;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.pdf.PdfExportService;
import se.inera.intyg.rehabstod.service.pdl.LogService;
import se.inera.intyg.rehabstod.service.sjukfall.SjukfallService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PDLActivityStore.class)
public class SjukfallControllerTest {

    private static final String VARDENHETS_ID = "123";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RehabstodUser rehabstodUserMock;

    @Mock
    UserService userServiceMock;

    @Mock
    LogService logServiceMock;

    @Mock
    PdfExportService pdfExportServiceMock;

    @Mock
    private SjukfallService sjukfallServiceMock;

    @InjectMocks
    private SjukfallController testee = new SjukfallController();

    @Before
    public void before() {
        when(userServiceMock.getUser()).thenReturn(rehabstodUserMock);
        when(rehabstodUserMock.getValdVardenhet()).thenReturn(new Vardenhet(VARDENHETS_ID, "enhet"));
        when(rehabstodUserMock.getUrval()).thenReturn(Urval.ALL);
    }

    @Test
    public void testGetSjukfall() {
        SjukfallEnhet a = createSjukFallForPatient("19121212-1212");
        SjukfallEnhet b = createSjukFallForPatient("20121212-1212");
        SjukfallEnhet c = createSjukFallForPatient("19840921-9287");

        List<SjukfallEnhet> result = Arrays.asList(a, b);
        List<SjukfallEnhet> toLog = Arrays.asList(c);

        // Given
        GetSjukfallRequest request = new GetSjukfallRequest();

        // When
        mockStatic(PDLActivityStore.class);
        when(PDLActivityStore.getActivitiesNotInStore(anyString(), any(List.class), eq(ActivityType.READ), any(Map.class))).thenReturn(toLog);
        when(sjukfallServiceMock.getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class))).thenReturn(result);

        // Then
        testee.getSjukfallForCareUnit(request);

        // Verify
        verifyStatic();
        PDLActivityStore.getActivitiesNotInStore(anyString(), any(List.class), eq(ActivityType.READ), any(Map.class));

        verify(sjukfallServiceMock).getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class));
        verify(logServiceMock).logSjukfallData(eq(toLog), eq(ActivityType.READ));
    }

    @Test
    public void testGetSjukfallAsPDF() throws DocumentException, IOException {

        SjukfallEnhet a = createSjukFallForPatient("19121212-1212");
        SjukfallEnhet b = createSjukFallForPatient("20121212-1212");
        SjukfallEnhet c = createSjukFallForPatient("19840921-9287");

        List<SjukfallEnhet> allSjukFall = Arrays.asList(a, b, c);
        List<SjukfallEnhet> finalList = Arrays.asList(a, b);
        List<SjukfallEnhet> toLog = Arrays.asList(c);

        // Given
        PrintSjukfallRequest request = new PrintSjukfallRequest();
        request.setPersonnummer(Arrays.asList("19121212-1212", "20121212-1212"));

        // When
        mockStatic(PDLActivityStore.class);
        doNothing().when(PDLActivityStore.class); //This is the preferred way to mock static void methods
        PDLActivityStore.addActivitiesToStore(anyString(), eq(toLog), eq(ActivityType.PRINT), any(Map.class));
        when(PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT), any(Map.class))).thenReturn(toLog);

        when(sjukfallServiceMock.getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class))).thenReturn(allSjukFall);
        when(pdfExportServiceMock.export(eq(finalList), eq(request), eq(rehabstodUserMock), eq(allSjukFall.size()))).thenReturn(new byte[0]);

        // Then
        ResponseEntity response = testee.getSjukfallForCareUnitAsPdf(request);

        // Verify
        verifyStatic();
        PDLActivityStore.addActivitiesToStore(anyString(), eq(toLog), eq(ActivityType.PRINT), any(Map.class));
        verifyStatic();
        PDLActivityStore.getActivitiesNotInStore(anyString(), eq(finalList), eq(ActivityType.PRINT), any(Map.class));

        verify(sjukfallServiceMock).getByUnit(anyString(), isNull(String.class), anyString(), any(Urval.class), any(GetSjukfallRequest.class));
        verify(logServiceMock).logSjukfallData(eq(toLog), eq(ActivityType.PRINT));

        assertTrue(response.getStatusCode().equals(HttpStatus.OK));
    }

    private static SjukfallEnhet createSjukFallForPatient(String personNummer) {
        // CHECKSTYLE:OFF MagicNumber
        SjukfallEnhet isf = new SjukfallEnhet();

        Lakare lakare = new Lakare("123456-0987", "Hr Doktor");
        isf.setLakare(lakare);

        Patient patient = new Patient(personNummer, "patient " + personNummer);
        isf.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        Diagnos diagnos = new Diagnos("M16", "M16", "diagnosnamn");
        diagnos.setKapitel("M00-M99");
        isf.setDiagnos(diagnos);

        isf.setDiagnos(diagnos);
        isf.setStart(LocalDate.now());
        isf.setSlut(LocalDate.now());
        isf.setDagar(1);
        isf.setIntyg(1);
        isf.setGrader(new ArrayList<>());
        isf.setAktivGrad(50);

        return isf;
        // CHECKSTYLE:ON MagicNumber
    }

}
