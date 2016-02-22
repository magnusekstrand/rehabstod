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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosBeskrivningService;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosKapitelService;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKod;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.util.SjukfallLangdCalculator;
import se.inera.intyg.rehabstod.web.controller.api.dto.GetSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Diagnos;
import se.inera.intyg.rehabstod.web.model.Gender;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Enhet;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Formaga;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class SjukfallEngine {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallEngine.class);

    private static final int AGE_START = 0;
    private static final int AGE_END = 8;
    private static final int GENDER_START = 11;
    private static final int GENDER_END = 12;

    protected Clock clock;

    @Autowired
    protected DiagnosBeskrivningService diagnosBeskrivningService;

    @Autowired
    protected DiagnosKapitelService diagnosKapitelService;

    @Autowired
    InternalIntygsDataResolver resolver;

    public SjukfallEngine() {
        clock = Clock.system(ZoneId.of("Europe/Paris"));
    }


    // - - -  API  - - -

    public List<InternalSjukfall> calculate(List<IntygsData> intygsData, String hsaId, Urval urval, GetSjukfallRequest requestData) {

        org.joda.time.LocalDate aktivtDatum = getAktivtDatum();

        Map<String, List<InternalIntygsData>> resolvedIntygsData =
                resolver.resolve(intygsData, requestData.getMaxIntygsGlapp(), aktivtDatum);

        // Assemble Sjukfall objects
        return resolvedIntygsData.entrySet().stream()
                .map(e -> toInternalSjukfall(e.getValue(), aktivtDatum))
                .collect(Collectors.toList());

    }


    // - - -  Protected scope  - - -

    protected Diagnos getDiagnos(IntygsData intyg) {
        String cleanedDiagnosKod = DiagnosKod.cleanKod(intyg.getDiagnoskod());
        String description = diagnosBeskrivningService.getDiagnosBeskrivning(cleanedDiagnosKod);
        DiagnosKapitel diagnosKaptiel = diagnosKapitelService.getDiagnosKapitel(cleanedDiagnosKod);

        Diagnos diagnos = new Diagnos();
        diagnos.setIntygsVarde(intyg.getDiagnoskod());
        diagnos.setKapitel(diagnosKaptiel.getId());
        diagnos.setKod(cleanedDiagnosKod);
        diagnos.setBeskrivning(description);

        return diagnos;
    }

    protected Patient getPatient(IntygsData intyg) {
        se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient intygPatient = intyg.getPatient();

        String id = intygPatient.getPersonId().getExtension();

        // Age
        int age = getPatientAge(id);

        // Gender
        Gender gender = null;
        if (id.length() > GENDER_END) {
            gender = Gender.getGenderFromString(id.substring(GENDER_START, GENDER_END));
        }

        Patient patient = new Patient();
        patient.setAlder(age);
        patient.setId(id);
        patient.setNamn(getPatientName(intygPatient));
        patient.setKon(gender);

        return patient;
    }


    // - - -  Package scope  - - -

    org.joda.time.LocalDate getAktivtDatum() {
        return org.joda.time.LocalDate.now();
    }

    InternalSjukfall toInternalSjukfall(List<InternalIntygsData> list, org.joda.time.LocalDate aktivtDatum) {

        // Find the active object
        InternalIntygsData aktivtIntyg = list.stream()
                .filter(o -> o.isAktivtIntyg())
                .findFirst()
                .orElseThrow(() -> new SjukfallEngineException("Unable to find a 'aktivt intyg'"));

        // Build Sjukfall object
        Sjukfall sjukfall = buildSjukfall(list, aktivtIntyg, aktivtDatum);

        // Build the internal Sjukfall object
        return buildInternalSjukfall(sjukfall, aktivtIntyg);
    }

    InternalSjukfall buildInternalSjukfall(Sjukfall sjukfall, InternalIntygsData aktivtIntyg) {
        Enhet ve = aktivtIntyg.getSkapadAv().getEnhet();

        InternalSjukfall internalSjukfall = new InternalSjukfall();
        internalSjukfall.setSjukfall(sjukfall);
        internalSjukfall.setVardGivareId(ve.getVardgivare().getVardgivarId().getExtension());
        internalSjukfall.setVardGivareNamn(ve.getVardgivare().getVardgivarnamn());
        internalSjukfall.setVardEnhetId(ve.getEnhetsId().getExtension());
        internalSjukfall.setVardEnhetNamn(ve.getEnhetsnamn());

        return internalSjukfall;
    }

    Sjukfall buildSjukfall(List<InternalIntygsData> values, InternalIntygsData aktivtIntyg, org.joda.time.LocalDate aktivtDatum) {
        Sjukfall sjukfall = new Sjukfall();

        sjukfall.setPatient(getPatient(aktivtIntyg));
        sjukfall.setDiagnos(getDiagnos(aktivtIntyg));

        sjukfall.setStart(getMinimumDate(values));
        sjukfall.setSlut(getMaximumDate(values));
        sjukfall.setDagar(SjukfallLangdCalculator.getEffectiveNumberOfSickDays(values));
        sjukfall.setIntyg(values.size());

        sjukfall.setGrader(getGrader(aktivtIntyg.getArbetsformaga().getFormaga()));
        sjukfall.setAktivGrad(getAktivGrad(aktivtIntyg.getArbetsformaga().getFormaga(), aktivtDatum));

        sjukfall.setLakare(aktivtIntyg.getSkapadAv().getFullstandigtNamn());

        return sjukfall;
    }


    // - - -  Private scope  - - -

    private Integer getAktivGrad(List<Formaga> list, org.joda.time.LocalDate aktivtDatum) {
        return list.stream()
                .filter(f -> f.getStartdatum().compareTo(aktivtDatum) < 1 && f.getSlutdatum().compareTo(aktivtDatum) > -1)
                .findFirst()
                .orElseThrow(() -> new SjukfallEngineException("Unable to find an active 'arbetsförmåga'"))
                .getNedsattning();
    }

    private List<Integer> getGrader(List<Formaga> list) {
        return list.stream()
                .sorted((f1, f2) -> f1.getNedsattning() - f2.getNedsattning())
                .map(f -> f.getNedsattning()).collect(Collectors.toList());
    }

    private org.joda.time.LocalDate getMinimumDate(List<InternalIntygsData> list) {
        return list.stream().min((d1, d2) -> d1.getStartDatum().compareTo(d2.getStartDatum())).get().getStartDatum();
    }

    private org.joda.time.LocalDate getMaximumDate(List<InternalIntygsData> list) {
        return list.stream().max((d1, d2) -> d1.getSlutDatum().compareTo(d2.getSlutDatum())).get().getSlutDatum();
    }

    private String getPatientName(se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient intygPatient) {
        StringBuilder name = new StringBuilder();
        name.append(intygPatient.getFornamn()).append(" ");

        if (intygPatient.getMellannamn() != null && !intygPatient.getMellannamn().isEmpty()) {
            name.append(intygPatient.getMellannamn()).append(" ");
        }
        name.append(intygPatient.getEfternamn());

        return name.toString();
    }

    private int getPatientAge(String patientId) {
        long age;
        try {
            LocalDate start = LocalDate.parse(patientId.substring(AGE_START, AGE_END), DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate end = LocalDate.now(clock);
            age = ChronoUnit.YEARS.between(start, end);
        } catch (DateTimeParseException e) {
            age = 0;
            LOG.error("Couldn't parse patient id", e);
        }

        return (int) age;
    }


}
