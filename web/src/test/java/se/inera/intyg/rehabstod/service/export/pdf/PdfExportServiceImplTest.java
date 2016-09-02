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
package se.inera.intyg.rehabstod.service.export.pdf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.common.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.inera.intyg.rehabstod.web.model.Sortering;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by marced on 24/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PdfExportServiceImplTest {
    RehabstodUser user;

    @Mock
    private DiagnosKapitelService diagnosKapitelService;

    @InjectMocks
    private PdfExportService testee = new PdfExportServiceImpl();

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
    }

    @Test
    public void testExportIssuedByMe() throws Exception {
        user.changeSelectedUrval(Urval.ISSUED_BY_ME);
        final byte[] export = testee.export(createSjukFallList(), createPrintRequest(), user, 3);
        assertTrue(export.length > 0);
        // Files.write(Paths.get("./test_issued_by_me.pdf"), export);
    }

    @Test
    public void testExportAll() throws Exception {
        user.changeSelectedUrval(Urval.ALL);
        final byte[] export = testee.export(createSjukFallList(), createPrintRequest(), user, 3);
        assertTrue(export.length > 0);

        // Files.write(Paths.get("./test_all.pdf"), export);
    }

    private PrintSjukfallRequest createPrintRequest() {
        PrintSjukfallRequest r = new PrintSjukfallRequest();

        r.setDiagnosGrupper(Arrays.asList("M00-M99", "J00-J99"));
        r.setFritext("Fritext");
        r.setLakare(Arrays.asList("Per Karlsson", "Johan Nilsson"));
        LangdIntervall langdIntervall = new LangdIntervall();

        langdIntervall.setMin("1");
        langdIntervall.setMax("365+");
        r.setLangdIntervall(langdIntervall);
        r.setMaxIntygsGlapp(30);
        final Sortering sortering = new Sortering();
        sortering.setKolumn("Personnummer");
        sortering.setOrder("Stigande");
        r.setSortering(sortering);
        return r;
    }

    private List<InternalSjukfall> createSjukFallList() {
        List<InternalSjukfall> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(createSjukFall(i, "19121212-" + i));
        }
        return list;
    }

    private static InternalSjukfall createSjukFall(int index, String personNummer) {
        Sjukfall sjukfall = new Sjukfall();

        Patient patient = new Patient();
        patient.setId(personNummer);
        patient.setAlder(50 + index / 2);
        patient.setNamn("patient " + personNummer);
        patient.setKon(index % 2 == 0 ? Gender.M : Gender.F);
        sjukfall.setPatient(patient);

        // Not really interested in these properties, but the sjukfall equals /hashcode will fail without them
        final Diagnos diagnos = new Diagnos();
        diagnos.setKapitel("M00-M99");
        diagnos.setKod("M16");
        diagnos.setIntygsVarde("M16" + index);

        sjukfall.setDiagnos(diagnos);
        sjukfall.setStart(LocalDate.now().plusDays(index));
        sjukfall.setSlut(sjukfall.getStart().plusDays(index));
        sjukfall.setDagar(index * 2 + index % 3);
        sjukfall.setIntyg(1);
        sjukfall.setGrader(index % 3 == 0 ? Arrays.asList(25, 50) : Arrays.asList(50, 75));
        sjukfall.setAktivGrad(50);

        Lakare lakare = new Lakare();
        lakare.setNamn("Doktor Glas");
        sjukfall.setLakare(lakare);

        InternalSjukfall is = new InternalSjukfall();
        is.setSjukfall(sjukfall);

        return is;
    }
}
