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
package se.inera.intyg.rehabstod.service.sjukfall.pu;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.Lakare;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.PatientData;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;
import se.inera.intyg.schemas.contract.Personnummer;

/**
 * Created by eriklupander on 2017-09-06.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallPuServiceImplTest {

    private static final String TOLVANSSON_PNR = "19121212-1212";
    private static final String TOLVANSSON_PNR_INVALID = "19121212-1211";
    private static final String ENHET_1 = "enhet-1";
    private static final String ENHET_2 = "enhet-2";
    private static final String LAKARE1_HSA_ID = "lakare-1";
    private static final String LAKARE2_HSA_ID = "lakare-2";
    private static final String LAKARE1_NAMN = "Läkare Läkarsson";

    @Mock
    private PUService puService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SjukfallPuServiceImpl testee;

    @Before
    public void init() {
        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, false));
    }

    @Test
    public void testNoFilterWhenUserIsVardadmin() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(false, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsVardadmin() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(true, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testAvlidenIsFiltered() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(true, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsVardadminFilterOnly() {

        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsLakareOnOtherUnit() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_2);
        when(userService.getUser()).thenReturn(rehabstodUser);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    private void mockPersonSvar(boolean avliden, boolean sekretess) {
        Map<Personnummer, PersonSvar> personSvarMap = new HashMap<>();
        personSvarMap.put(createPnr(TOLVANSSON_PNR), buildPersonSvar(TOLVANSSON_PNR, sekretess, avliden));
        when(puService.getPersons(anyListOf(Personnummer.class))).thenReturn(personSvarMap);
    }

    private void mockPersonSvarError() {
        Map<Personnummer, PersonSvar> personSvarMap = new HashMap<>();
        personSvarMap.put(createPnr(TOLVANSSON_PNR), PersonSvar.error());
        when(puService.getPersons(anyListOf(Personnummer.class))).thenReturn(personSvarMap);
    }

    private void mockPersonSvarNotFound() {
        Map<Personnummer, PersonSvar> personSvarMap = new HashMap<>();
        personSvarMap.put(createPnr(TOLVANSSON_PNR), PersonSvar.notFound());
        when(puService.getPersons(anyListOf(Personnummer.class))).thenReturn(personSvarMap);
    }

    @Test
    public void testSekretessmarkeradIsFilteredWhenUserIsLakareOnOtherUnitFilterOnly() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_2);
        when(userService.getUser()).thenReturn(rehabstodUser);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsIncludedWhenUserIsLakareOnSameUnit() {
        RehabstodUser rehabstodUser = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(rehabstodUser);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsIncludedWhenUserIsLakareAsRehabkoordinatorAndSignedTheIntyg() {
        RehabstodUser rehabstodUser = buildLakareAsRehabkoordinator(ENHET_1);
        when(userService.getUser()).thenReturn(rehabstodUser);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, false));

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
    }

    @Test
    public void testSekretessmarkeradIsExcludedWhenUserIsLakareAsRehabkoordinatorOnSameUnitButIsNotSigning() {
        RehabstodUser rehabstodUser = buildLakareAsRehabkoordinator(ENHET_1);
        when(rehabstodUser.getHsaId()).thenReturn(LAKARE2_HSA_ID);
        when(userService.getUser()).thenReturn(rehabstodUser);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPersons(Arrays.asList(createPnr(TOLVANSSON_PNR)))).thenReturn(buildPersonMap());

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }


    @Test(expected = IllegalStateException.class)
    public void testExceptionIsThrownWhenPuUnavailable() {
        mockPersonSvarError();
        testee.enrichWithPatientNamesAndFilterSekretess(buildSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testNameIsAppliedFromPersonSvar() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvar(false, false);
        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals("Fornamn Efternamn", sjukfallList.get(0).getPatient().getNamn());
    }

    @Test
    public void testNameIsReplacedWhenLakareAccessesSekretessmarkerad() {
        RehabstodUser user = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        mockPersonSvar(false, true);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);

        assertEquals(sjukfallList.size(), 1);
        assertEquals(sjukfallList.get(0).getPatient().getNamn(), SjukfallPuService.SEKRETESS_SKYDDAD_NAME_PLACEHOLDER);
    }

    @Test
    public void testNameIsReplacedByPlaceholderIfFromPersonSvarWasNotFound() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        mockPersonSvarNotFound();

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(1, sjukfallList.size());
        assertEquals(SjukfallPuService.SEKRETESS_SKYDDAD_NAME_UNKNOWN, sjukfallList.get(0).getPatient().getNamn());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallNotAllowedForVardadminIfSekretessmarkering() {
        when(userService.getUser()).thenReturn(buildVardadmin());

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, false));
        testee.enrichWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallThrowsExceptionIfPuError() {

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                PersonSvar.error());
        testee.enrichWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testGetPatientSjukfallWhenPatientNotFoundInPu() {

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                PersonSvar.notFound());
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPatientSjukfallNotAllowedForLakareOtherUnitIfSekretessmarkering() {
        RehabstodUser user = buildLakare(ENHET_2);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(false);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, false));

        testee.enrichWithPatientNameAndFilterSekretess(buildPatientSjukfallList(TOLVANSSON_PNR));
    }

    @Test
    public void testGetPatientSjukfallAllowedForLakareSameUnitIfSekretessmarkering() {
        RehabstodUser user = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, false));

        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals(1, patientSjukfallList.get(0).getIntyg().size());
        assertEquals("Sekretessmarkerad uppgift", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test
    public void testGetPatientSjukfallAllowedForLakareSameUnitIfNotSekretessmarkering() {
        RehabstodUser user = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(user);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, false));

        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals(1, patientSjukfallList.get(0).getIntyg().size());
        assertEquals("Fornamn Efternamn", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test
    public void testFilterWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        when(puService.getPerson(createPnr(TOLVANSSON_PNR_INVALID))).thenReturn(PersonSvar.error());

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR_INVALID);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testFilterForSummaryWhenPatientAvliden() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        mockPersonSvar(true, false);

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR);
        testee.filterSekretessForSummary(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test
    public void testEnrichPatientsWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        when(puService.getPerson(createPnr(TOLVANSSON_PNR_INVALID))).thenReturn(PersonSvar.error());

        List<SjukfallEnhet> sjukfallList = buildSjukfallList(TOLVANSSON_PNR_INVALID);
        testee.enrichWithPatientNamesAndFilterSekretess(sjukfallList);
        assertEquals(0, sjukfallList.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnrichPatientWhenPersonnummerHasInvalidDigit() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        when(puService.getPerson(createPnr(TOLVANSSON_PNR_INVALID))).thenReturn(PersonSvar.error());

        List<SjukfallPatient> sjukfallPatientList = buildPatientSjukfallList(TOLVANSSON_PNR_INVALID);
        testee.enrichWithPatientNameAndFilterSekretess(sjukfallPatientList);
    }

    @Test
    public void testPatientNameSetToSekretessWhenApplicableOnPatientView() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, true, false));
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(1, patientSjukfallList.size());
        assertEquals("Sekretessmarkerad uppgift", patientSjukfallList.get(0).getIntyg().get(0).getPatient().getNamn());
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsExceptionWhenAvliden() {
        RehabstodUser lakare = buildLakare(ENHET_1);
        when(userService.getUser()).thenReturn(lakare);
        when(userService.isUserLoggedInOnEnhetOrUnderenhet(ENHET_1)).thenReturn(true);

        when(puService.getPerson(createPnr(TOLVANSSON_PNR))).thenReturn(
                buildPersonSvar(TOLVANSSON_PNR, false, true));
        List<SjukfallPatient> patientSjukfallList = buildPatientSjukfallList(TOLVANSSON_PNR);
        testee.enrichWithPatientNameAndFilterSekretess(patientSjukfallList);
        assertEquals(0, patientSjukfallList.size());
    }

    private List<SjukfallPatient> buildPatientSjukfallList(String pnr) {
        List<SjukfallPatient> sjukfallList = new ArrayList<>();
        sjukfallList.add(buildPatientSjukfall(pnr));
        return sjukfallList;
    }

    private SjukfallPatient buildPatientSjukfall(String pnr) {
        SjukfallPatient sjukfallPatient = new SjukfallPatient();
        sjukfallPatient.setIntyg(buildIntyg(pnr));
        return sjukfallPatient;
    }

    private List<PatientData> buildIntyg(String pnr) {
        List<PatientData> patientData = new ArrayList<>();
        patientData.add(buildPatientData(pnr));
        return patientData;
    }

    private PatientData buildPatientData(String pnr) {
        PatientData patientData = new PatientData();
        patientData.setVardenhetId(ENHET_1);
        patientData.setPatient(buildPatient(pnr));
        patientData.setLakare(new Lakare(LAKARE1_HSA_ID, LAKARE1_NAMN));
        return patientData;
    }

    private RehabstodUser buildLakare(String enhetHsaId) {
        Vardenhet valdVardenhet = new Vardenhet(enhetHsaId, "namnet");

        RehabstodUser user = mock(RehabstodUser.class);
        Map<String, Role> roleMap = mock(Map.class);
        when(roleMap.containsKey(eq("LAKARE"))).thenReturn(true);

        when(user.getValdVardenhet()).thenReturn(valdVardenhet);
        when(user.isLakare()).thenReturn(true);
        when(user.getRoles()).thenReturn(roleMap);
        when(user.getHsaId()).thenReturn(LAKARE1_HSA_ID);
        return user;
    }

    private RehabstodUser buildLakareAsRehabkoordinator(String enhetHsaId) {
        Vardenhet valdVardenhet = new Vardenhet(enhetHsaId, "namnet");

        RehabstodUser user = mock(RehabstodUser.class);
        Map<String, Role> roleMap = mock(Map.class);
        when(roleMap.containsKey(eq("LAKARE"))).thenReturn(false);

        when(user.getValdVardenhet()).thenReturn(valdVardenhet);
        when(user.isLakare()).thenReturn(true);
        when(user.getRoles()).thenReturn(roleMap);
        when(user.getHsaId()).thenReturn(LAKARE1_HSA_ID);
        return user;
    }

    private RehabstodUser buildVardadmin() {
        RehabstodUser user = new RehabstodUser("hsa-123", "user-123", false);
        user.setRoles(new HashMap<>());
        return user;
    }

    private PersonSvar buildPersonSvar(String pnr, boolean sekretess, boolean avliden) {
        return PersonSvar.found(buildPerson(pnr, sekretess, avliden));
    }

    private Person buildPerson(String pnr, boolean sekretess, boolean avliden) {
        return new Person(createPnr(pnr), sekretess, avliden, "Fornamn", null, "Efternamn",
                "Gatan 1", "11212", "Orten");
    }

    private List<SjukfallEnhet> buildSjukfallList(String pnr) {
        List<SjukfallEnhet> sjukfallList = new ArrayList<>();
        sjukfallList.add(buildSjukfall(pnr));
        return sjukfallList;
    }

    private SjukfallEnhet buildSjukfall(String pnr) {
        SjukfallEnhet sjukfall = new SjukfallEnhet();
        sjukfall.setPatient(buildPatient(pnr));
        sjukfall.setVardEnhetId(ENHET_1);
        sjukfall.setLakare(new Lakare(LAKARE1_HSA_ID, LAKARE1_NAMN));
        return sjukfall;
    }

    private Map<Personnummer, PersonSvar> buildPersonMap() {
        Map<Personnummer, PersonSvar> persons = new HashMap<>();
        persons.put(createPnr(TOLVANSSON_PNR),  buildPersonSvar(TOLVANSSON_PNR, true, false));
        return persons;
    }

    private Patient buildPatient(String pnr) {
        return new Patient(pnr, "Tolvan Tolvansson");
    }

    private Personnummer createPnr(String pnr) {
        return Personnummer
                .createPersonnummer(pnr)
                .orElseThrow(() -> new IllegalArgumentException("Cannot create Personnummer object with pnr: " + pnr));
    }
}
