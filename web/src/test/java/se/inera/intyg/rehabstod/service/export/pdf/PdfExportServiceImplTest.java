/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.service.export.pdf;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;

import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.infra.security.common.service.CommonFeatureService;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.feature.RehabstodFeature;
import se.inera.intyg.rehabstod.testutil.TestDataGen;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * Created by marced on 24/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PdfExportServiceImplTest {

    RehabstodUser user;

    @Mock
    private DiagnosKapitelService diagnosKapitelService;

    @Mock
    private CommonFeatureService featureService;

    @InjectMocks
    private PdfExportService testee = new PdfExportServiceImpl();

    private static SjukfallEnhet createSjukFall(int index, String personNummer) {
        SjukfallEnhet isf = new SjukfallEnhet();

        Lakare lakare = new Lakare("123456-0987", "Hr Doktor");
        isf.setLakare(lakare);

        Patient patient = new Patient(personNummer, "patient " + personNummer);
        patient.setAlder(50 + index / 2);
        patient.setKon(index % 2 == 0 ? Gender.M : Gender.F);
        isf.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        Diagnos diagnos = new Diagnos("M16", "M16", "diagnosnamn");
        diagnos.setKapitel("M00-M99");
        isf.setDiagnos(diagnos);

        isf.setStart(LocalDate.now().plusDays(index));
        isf.setSlut(isf.getStart().plusDays(index));
        isf.setDagar(index * 2 + index % 3);
        isf.setIntyg(1);
        isf.setGrader(index % 3 == 0 ? Arrays.asList(25, 50) : Arrays.asList(50, 75));
        isf.setAktivGrad(50);

        return isf;
    }

    @Before
    public void setUp() throws Exception {

        user = new RehabstodUser("HSA1111", "Johannes Nielsen-Kornbach");
        user.setValdVardenhet(new SelectableVardenhet() {
            @Override
            public String getId() {
                return "1111";
            }

            @Override
            public String getNamn() {
                return "Gläntans vårdcentral";
            }

            @Override
            public List<String> getHsaIds() {
                return null;
            }
        });
        user.setValdVardgivare(new SelectableVardenhet() {
            @Override
            public String getId() {
                return "VG1";
            }

            @Override
            public String getNamn() {
                return "Vardgivare1";
            }

            @Override
            public List<String> getHsaIds() {
                return null;
            }
        });

        DiagnosKapitel diagnosKapitel = mock(DiagnosKapitel.class);
        when(diagnosKapitel.getName()).thenReturn("Diagnoskapitlets namn");
        when(diagnosKapitelService.getDiagnosKapitel(anyString())).thenReturn(diagnosKapitel);
        // Use srs as default in tests
        when(featureService.getActiveFeatures(anyString())).thenReturn(ImmutableSet.of(RehabstodFeature.SRS.getName()));
    }

    @Test
    public void testExportIssuedByMe() throws Exception {
        Map<String, Role> roles = new HashMap<>();
        roles.put(AuthoritiesConstants.ROLE_LAKARE, null);
        user.setRoles(roles);
        final byte[] export = testee.export(createSjukFallList(), TestDataGen.buildPrintRequest(), user, 3);
        assertTrue(export.length > 0);
        // Files.write(Paths.get("./test_issued_by_me.pdf"), export);
    }

    @Test
    public void testExportAll() throws Exception {
        Map<String, Role> roles = new HashMap<>();
        roles.put(AuthoritiesConstants.ROLE_KOORDINATOR, null);
        user.setRoles(roles);
        final byte[] export = testee.export(createSjukFallList(), TestDataGen.buildPrintRequest(), user, 3);
        assertTrue(export.length > 0);

        // Files.write(Paths.get("./test_all.pdf"), export);
    }
    @Test
    public void testExportAllWithoutSrs() throws Exception {
        // Use srs as default in tests
        when(featureService.getActiveFeatures(anyString())).thenReturn(ImmutableSet.of(""));

        Map<String, Role> roles = new HashMap<>();
        roles.put(AuthoritiesConstants.ROLE_KOORDINATOR, null);
        user.setRoles(roles);
        final byte[] export = testee.export(createSjukFallList(), TestDataGen.buildPrintRequest(), user, 3);
        assertTrue(export.length > 0);

        // Files.write(Paths.get("./test_all.pdf"), export);
    }

    private List<SjukfallEnhet> createSjukFallList() {
        List<SjukfallEnhet> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(createSjukFall(i, "19121212-" + i));
        }
        return list;
    }

}
