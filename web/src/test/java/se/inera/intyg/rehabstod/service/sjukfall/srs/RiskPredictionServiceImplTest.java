/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.sjukfall.srs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.integration.srs.model.RiskSignal;
import se.inera.intyg.rehabstod.integration.srs.service.SRSIntegrationService;
import se.inera.intyg.rehabstod.service.exceptions.SRSServiceException;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2017-11-01.
 */
@RunWith(MockitoJUnitRunner.class)
public class RiskPredictionServiceImplTest {

    private static final String ENHET_1 = "enhet-1";
    private static final String ENHET_2 = "enhet-2";

    @Mock
    private SRSIntegrationService srsIntegrationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RiskPredictionServiceImpl testee;

    private RehabstodUser user;

    @Before
    public void init() {
        user = buildUser(ENHET_1);
        when(userService.getUser()).thenReturn(user);
    }

    @Test
    public void testNoInteractionWithSRSWhenFeatureNotActive() {
        when(user.getFeatures()).thenReturn(Collections.emptyMap());
        testee.updateWithRiskPredictions(buildSjukfallEnhetList(UUID.randomUUID().toString()));
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testNoInteractionWithSRSWhenFeatureNotActiveForUnit() {
        RehabstodUser user = buildUser(ENHET_2);
        when(user.getFeatures()).thenReturn(Collections.emptyMap());
        when(userService.getUser()).thenReturn(user);
        testee.updateWithRiskPredictions(buildSjukfallEnhetList(UUID.randomUUID().toString()));
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testPatientDataNoInteractionWithSRSWhenFeatureNotActive() {
        when(user.getFeatures()).thenReturn(Collections.emptyMap());
        testee.updateSjukfallPatientListWithRiskPredictions(buildSjukfallPatientList(UUID.randomUUID().toString()));
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testNoInteractionWithSRSWhenFeatureActiveButNoSjukfallSupplied() {
        testee.updateWithRiskPredictions(new ArrayList<>());
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testPatientDataNoInteractionWithSRSWhenFeatureActiveButNoSjukfallSupplied() {
        testee.updateSjukfallPatientListWithRiskPredictions(new ArrayList<>());
        verifyZeroInteractions(srsIntegrationService);
    }

    @Test
    public void testInteractionWithSRSWhenFeatureActive() {
        String intygsId = UUID.randomUUID().toString();

        when(srsIntegrationService.getRiskPreditionerForIntygsId(anyListOf(String.class))).thenReturn(buildRiskSignalList(intygsId));
        List<SjukfallEnhet> sjukfallEnhetList = buildSjukfallEnhetList(intygsId);
        testee.updateWithRiskPredictions(sjukfallEnhetList);

        assertEquals(1, sjukfallEnhetList.size());
        SjukfallEnhet sjukfallEnhet = sjukfallEnhetList.get(0);

        assertNotNull(sjukfallEnhet.getRiskSignal());
        assertEquals(intygsId, sjukfallEnhet.getRiskSignal().getIntygsId());
        assertEquals(2, sjukfallEnhet.getRiskSignal().getRiskKategori());
        assertEquals("beskrivning", sjukfallEnhet.getRiskSignal().getRiskDescription());

        verify(srsIntegrationService, times(1)).getRiskPreditionerForIntygsId(anyListOf(String.class));
    }

    @Test
    public void testPatientDataInteractionWithSRSWhenFeatureActive() {
        String intygsId = UUID.randomUUID().toString();

        when(srsIntegrationService.getRiskPreditionerForIntygsId(anyListOf(String.class))).thenReturn(buildRiskSignalList(intygsId));
        List<SjukfallPatient> sjukfallPatientList = buildSjukfallPatientList(intygsId);
        testee.updateSjukfallPatientListWithRiskPredictions(sjukfallPatientList);

        assertEquals(1, sjukfallPatientList.size());
        PatientData patientData = sjukfallPatientList.get(0).getIntyg().get(0);

        assertEquals(intygsId, patientData.getRiskSignal().getIntygsId());
        assertEquals(2, patientData.getRiskSignal().getRiskKategori());
        assertEquals("beskrivning", patientData.getRiskSignal().getRiskDescription());

        verify(srsIntegrationService, times(1)).getRiskPreditionerForIntygsId(anyListOf(String.class));
    }

    @Test(expected = SRSServiceException.class)
    public void testExpectedExceptionIsThrownWhenCallToSRSFails() {
        when(srsIntegrationService.getRiskPreditionerForIntygsId(anyListOf(String.class))).thenThrow(new RuntimeException("FEL FEL FEL"));
        testee.updateWithRiskPredictions(buildSjukfallEnhetList(UUID.randomUUID().toString()));
    }

    @Test(expected = SRSServiceException.class)
    public void testPatientListExpectedExceptionIsThrownWhenCallToSRSFails() {
        when(srsIntegrationService.getRiskPreditionerForIntygsId(anyListOf(String.class))).thenThrow(new RuntimeException("FEL FEL FEL"));
        testee.updateSjukfallPatientListWithRiskPredictions(buildSjukfallPatientList(UUID.randomUUID().toString()));
    }

    @Test
    public void testRiskLevelOneIsFilteredOut() {
        String intygsId = UUID.randomUUID().toString();

        List<RiskSignal> riskSignals = Arrays.asList(
                new RiskSignal(intygsId, 2, "beskrivning2"),
                new RiskSignal(intygsId, 1, "beskrivning1"),
                new RiskSignal(intygsId, 0, "beskrivning0")
        );

        when(srsIntegrationService.getRiskPreditionerForIntygsId(anyListOf(String.class))).thenReturn(riskSignals);
        List<SjukfallEnhet> sjukfallEnhetList = buildSjukfallEnhetList(intygsId);
        testee.updateWithRiskPredictions(sjukfallEnhetList);

        assertEquals(1, sjukfallEnhetList.size());
    }

    private List<RiskSignal> buildRiskSignalList(String intygsId) {
        List<RiskSignal> list = new ArrayList<>();
        list.add(new RiskSignal(intygsId, 2, "beskrivning"));
        return list;
    }

    // sjukfall enhet builder.
    private List<SjukfallEnhet> buildSjukfallEnhetList(String intygsId) {
        List<SjukfallEnhet> list = new ArrayList<>();
        SjukfallEnhet se = new SjukfallEnhet();
        se.setAktivIntygsId(intygsId);
        list.add(se);
        return list;
    }

    private List<SjukfallEnhet> buildSjukfallEnhetList(int size) {
        List<SjukfallEnhet> list = new ArrayList<>();
        for (int a = 0; a < size; a++) {
            SjukfallEnhet se = new SjukfallEnhet();
            se.setAktivIntygsId("intyg-" + a);
            list.add(se);
        }
        return list;
    }

    // Patient data builders.
    private List<SjukfallPatient> buildSjukfallPatientList(String intygsId) {
        List<SjukfallPatient> list = new ArrayList<>();
        SjukfallPatient sjukfallPatient = new SjukfallPatient();
        sjukfallPatient.setIntyg(buildIntyg(intygsId));
        list.add(sjukfallPatient);
        return list;
    }

    private List<PatientData> buildIntyg(String intygsId) {
        List<PatientData> intyg = new ArrayList<>();
        intyg.add(buildPatientData(intygsId));
        return intyg;
    }

    private PatientData buildPatientData(String intygsId) {
        PatientData pd = new PatientData();
        pd.setIntygsId(intygsId);
        return pd;
    }

    // User builder
    private RehabstodUser buildUser(String unitHsaId) {
        RehabstodUser user = mock(RehabstodUser.class);
        SelectableVardenhet ve = mock(SelectableVardenhet.class);
        when(ve.getId()).thenReturn(unitHsaId);
        when(user.getValdVardenhet()).thenReturn(ve);
        Feature f = new Feature();
        f.setGlobal(true);
        Map<String, Feature> features = Collections
                .singletonMap(AuthoritiesConstants.FEATURE_SRS, f);
        when(user.getFeatures()).thenReturn(features);
        return user;
    }

}
