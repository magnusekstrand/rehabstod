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
package se.inera.intyg.rehabstod.testutil;

import com.google.common.collect.ImmutableMap;
import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.LangdIntervall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sortering;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper base class, provides data setup for tests.
 *
 * Created by eriklupander on 2016-02-24.
 */
public final class TestDataGen {

    private static final String USER_HSA_ID = "user-1";
    private static final String USER_NAME = "Läkar Läkarsson";
    private static final String CAREUNIT_ID = "careunit-1";
    private static final String CAREUNIT_NAME = "Vårdenhet 1";
    private static final String CAREGIVER_ID = "caregiver-1";
    private static final String CAREGIVER_NAME = "Vårdgivare 1";

    private TestDataGen() {

    }

    // CHECKSTYLE:OFF MagicNumber

    public static PrintSjukfallRequest buildPrintRequest() {
        PrintSjukfallRequest req = new PrintSjukfallRequest();
        req.setPersonnummer(buildPersonnummerList());
        req.setDiagnosGrupper(buildDiagnosGrupper());
        req.setLakare(buildLakare());
        req.setLangdIntervall(buildLangdIntervall());
        req.setMaxIntygsGlapp(5);
        req.setSortering(buildSortering());
        return req;
    }

    public static Sortering buildSortering() {
        Sortering sortering = new Sortering();
        sortering.setKolumn("Namn");
        sortering.setOrder("ASC");
        return sortering;
    }

    public static LangdIntervall buildLangdIntervall() {
        LangdIntervall langdIntervall = new LangdIntervall();
        langdIntervall.setMax("90");
        langdIntervall.setMin("30");
        return langdIntervall;
    }

    public static List<String> buildLakare() {
        List<Lakare> lakare = new ArrayList<>();
        lakare.add(createLakare("IFV1239877878-1049", "Jan Nilsson"));
        lakare.add(createLakare("IFV1239877878-1255", "Ove Mört"));

        return lakare.stream().map(l -> l.getNamn()).collect(Collectors.toList());
    }

    public static Lakare createLakare(String lakareId, String lakareNamn) {
        return new Lakare(lakareId, lakareNamn);
    }

    public static List<String> buildDiagnosGrupper() {
        List<String> diagnosGrupper = new ArrayList<>();
        diagnosGrupper.add("H00-H59: Sjukdomar i ögat och närliggande organ");
        diagnosGrupper.add("J00-J99: Andningsorganens sjukdomar");
        diagnosGrupper.add("M00-M99: Sjukdomar i muskuloskeletala systemet och bindväven");
        return diagnosGrupper;
    }

    public static List<String> buildPersonnummerList() {
        List<String> personnummerList = new ArrayList<>();
        personnummerList.add("19121212-1212");
        return personnummerList;
    }

    public static List<InternalSjukfall> buildSjukfallList(int num) {
        List<InternalSjukfall> sjukfallList = new ArrayList<>();
        for (int a = 0; a < num; a++) {
            sjukfallList.add(buildInternalSjukfall());
        }
        return sjukfallList;
    }

    public static InternalSjukfall buildInternalSjukfall() {
        InternalSjukfall isf = new InternalSjukfall();

        Lakare lakare = new Lakare("IFV1239877878-1049", "Jan Nilsson");
        isf.setLakare(lakare);

        isf.setPatient(buildPatient());
        isf.setDiagnos(buildDiagnos());
        isf.setGrader(buildGrader());

        isf.setAktivGrad(75);
        isf.setDagar(65);
        isf.setIntyg(2);

        isf.setStart(LocalDate.now().minusMonths(2));
        isf.setSlut(LocalDate.now().plusWeeks(2));

        return isf;
    }

    public static Patient buildPatient() {
        Patient patient = new Patient("19121212-1212", "Tolvan Tolvansson");
        patient.setAlder(54);
        patient.setKon(Gender.M);
        return patient;
    }

    public static List<Integer> buildGrader() {
        return Arrays.asList(50, 75);
    }

    public static Diagnos buildDiagnos() {
        Diagnos diagnos = new Diagnos("J22", "J22", "diagnosnamn");
        return diagnos;
    }

    public static RehabstodUser buildRehabStodUser() {
        RehabstodUser user = new RehabstodUser(USER_HSA_ID, USER_NAME);
        user.setValdVardenhet(buildValdVardenhet(CAREUNIT_ID, CAREUNIT_NAME));
        user.setValdVardgivare(buildValdGivare(CAREGIVER_ID, CAREGIVER_NAME));
        user.setMiuNamnPerEnhetsId(buildMiUPerEnhetsIdMap());
        user.setTitel("Överläkare");
        user.setRoles(ImmutableMap.of(AuthoritiesConstants.ROLE_LAKARE, new Role()));
        return user;
    }

    private static Map<String, String> buildMiUPerEnhetsIdMap() {
        Map<String, String> map = new HashMap<>();
        map.put(CAREUNIT_ID, "Läkare på " + CAREUNIT_NAME);
        return map;
    }

    private static SelectableVardenhet buildValdGivare(String hsaId, String namn) {
        return new Vardgivare(hsaId, namn);
    }

    private static SelectableVardenhet buildValdVardenhet(String hsaId, String namn) {
        return new Vardenhet(hsaId, namn);
    }

    // CHECKSTYLE:ON MagicNumber
}
