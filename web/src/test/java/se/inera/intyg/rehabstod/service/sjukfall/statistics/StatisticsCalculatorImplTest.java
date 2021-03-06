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
package se.inera.intyg.rehabstod.service.sjukfall.statistics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosGruppLoader;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosGrupp;
import se.inera.intyg.rehabstod.service.sjukfall.dto.DiagnosGruppStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.GenderStat;
import se.inera.intyg.rehabstod.service.sjukfall.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by marced on 04/03/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class StatisticsCalculatorImplTest {

    private String lakareId1 = "hsaid1";
    private String lakareNamn1 = "Läkare1";
    private String lakareId2 = "hsaid2";
    private String lakareNamn2 = "Läkare2";
    private String patientId = "19121212-1212";
    private String patientNamn = "Tolvan Tolvansson";

    private static final DiagnosGrupp GRUPP1 = new DiagnosGrupp("M00-M99,N00-V99:En grupp");
    private static final DiagnosGrupp GRUPP2 = new DiagnosGrupp("Z00-Z99:En annan grupp");
    private static final DiagnosGrupp DIAGNOS_GRUPP_UNKNOWN = StatisticsCalculatorImpl.NON_MATCHING_GROUP;

    @Mock
    DiagnosGruppLoader diagnosGruppLoader;

    @InjectMocks
    private StatisticsCalculatorImpl testee;

    @Before
    public void init() throws IOException {

        when(diagnosGruppLoader.loadDiagnosGrupper()).thenReturn(createDiagnosGruppList());
        testee.init();
    }

    private List<DiagnosGrupp> createDiagnosGruppList() {
        List<DiagnosGrupp> grupper = new ArrayList<>();
        grupper.add(GRUPP1);
        grupper.add(GRUPP2);
        return grupper;
    }

    @Test
    public void testGetSjukfallSummaryNoInput() throws Exception {
        List<SjukfallEnhet> internalSjukfallList = new ArrayList<>();

        final SjukfallSummary summary = testee.getSjukfallSummary(internalSjukfallList);
        assertEquals(0, summary.getTotal());
        assertEquals(0, getGenderItem(Gender.F, summary.getGenders()).getCount());
        assertEquals(0, getGenderItem(Gender.M, summary.getGenders()).getCount());
    }

    @Test
    public void testGetSjukfallSummary() throws Exception {
        List<SjukfallEnhet> internalSjukfallList = new ArrayList<>();

        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F, "M16"));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F, "P16"));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F, "V16"));

        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.M, "Z16"));

        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.M, "A16"));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.M, null));

        final SjukfallSummary summary = testee.getSjukfallSummary(internalSjukfallList);
        assertEquals(6, summary.getTotal());
        assertEquals(3, getGenderItem(Gender.F, summary.getGenders()).getCount());
        assertEquals(3, getGenderItem(Gender.M, summary.getGenders()).getCount());

        final List<DiagnosGruppStat> returnedGroups = summary.getGroups();

        final List<DiagnosGruppStat> expectedGroups = new ArrayList<>();
        expectedGroups.add(new DiagnosGruppStat(GRUPP1, 3L));
        expectedGroups.add(new DiagnosGruppStat(DIAGNOS_GRUPP_UNKNOWN, 2L));
        expectedGroups.add(new DiagnosGruppStat(GRUPP2, 1L));
        assertEquals(expectedGroups, returnedGroups);
    }

    private GenderStat getGenderItem(Gender g, List<GenderStat> genders) {
        return genders.stream().filter(gs -> gs.getGender().equals(g)).findFirst().get();
    }

    @Test
    public void testGetSjukfallAllOneGender() throws Exception {
        List<SjukfallEnhet> internalSjukfallList = new ArrayList<>();

        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F, "M16"));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F, "M16"));

        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F, "M16"));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F, "M16"));

        final SjukfallSummary summary = testee.getSjukfallSummary(internalSjukfallList);
        assertEquals(4, summary.getTotal());
        assertEquals(4, getGenderItem(Gender.F, summary.getGenders()).getCount());
        assertEquals(0, getGenderItem(Gender.M, summary.getGenders()).getCount());

    }

    @Test
    public void testGetSjukfallDifferentSjukskrivningsGrad() throws Exception {
        List<SjukfallEnhet> internalSjukfallList = new ArrayList<>();

        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F, "M16", 25));
        internalSjukfallList.add(createInternalSjukfall(lakareId1, lakareNamn1, Gender.F, "M16", 25));

        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F, "M16", 50));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F, "M16", 75));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F, "M16", 100));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F, "M16", 100));
        internalSjukfallList.add(createInternalSjukfall(lakareId2, lakareNamn2, Gender.F, "M16", 100));

        final SjukfallSummary summary = testee.getSjukfallSummary(internalSjukfallList);
        assertEquals(2, summary.getSickLeaveDegrees().get(0).getCount());
        assertEquals(1, summary.getSickLeaveDegrees().get(1).getCount());
        assertEquals(1, summary.getSickLeaveDegrees().get(2).getCount());
        assertEquals(3, summary.getSickLeaveDegrees().get(3).getCount());

    }

    private SjukfallEnhet createInternalSjukfall(String lakareId, String lakareNamn, Gender patientKon, String diagnosKod, int aktivGrad) {
        SjukfallEnhet isf = createInternalSjukfall(lakareId, lakareNamn, patientKon, diagnosKod);
        isf.setAktivGrad(aktivGrad);
        return isf;
    }

    private SjukfallEnhet createInternalSjukfall(String lakareId, String lakareNamn, Gender patientKon, String diagnosKod) {
        SjukfallEnhet isf = new SjukfallEnhet();

        Lakare lakare = new Lakare(lakareId, lakareNamn);
        isf.setLakare(lakare);

        Patient patient = new Patient(patientId, patientNamn);
        patient.setKon(patientKon);
        isf.setPatient(patient);

        Diagnos diagnos = new Diagnos(diagnosKod, diagnosKod, "diagnosnamn");
        diagnos.setKapitel("M00-M99");
        isf.setDiagnos(diagnos);

        return isf;
    }
}
