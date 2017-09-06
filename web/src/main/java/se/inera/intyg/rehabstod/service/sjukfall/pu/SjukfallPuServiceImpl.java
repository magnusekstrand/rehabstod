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
package se.inera.intyg.rehabstod.service.sjukfall.pu;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhetRS;
import se.inera.intyg.rehabstod.web.model.SjukfallPatientRS;
import se.inera.intyg.schemas.contract.Personnummer;

import java.util.Iterator;
import java.util.List;

/**
 * Created by eriklupander on 2017-09-05.
 */
@Service
public class SjukfallPuServiceImpl implements SjukfallPuService {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallPuServiceImpl.class);

    @Autowired
    private PUService puService;

    @Autowired
    private UserService userService;

    @Override
    public void enrichWithPatientNamesAndFilterSekretess(List<SjukfallEnhetRS> sjukfallList) {

        RehabstodUser user = userService.getUser();

        Iterator<SjukfallEnhetRS> i = sjukfallList.iterator();

        while (i.hasNext()) {
            SjukfallEnhetRS item = i.next();

            Personnummer pnr = Personnummer.createValidatedPersonnummerWithDash(item.getPatient().getId()).orElse(null);
            if (pnr == null) {
                i.remove();
                LOG.warn("Problem parsing a personnummer when looking up patient in PU service. Removing from list of sjukfall.");
                continue;
            }

            PersonSvar personSvar = puService.getPerson(pnr);
            if (personSvar.getStatus() == PersonSvar.Status.FOUND) {
                if (personSvar.getPerson().isSekretessmarkering()) {

                    // RS-US-GE-002: Om användaren EJ är läkare ELLER om intyget utfärdades på annan VE, då får vi ej visa
                    // sjukfall för s-märkt patient.
                    if (!user.isLakare() || !item.getVardEnhetId().equalsIgnoreCase(user.getValdVardenhet().getId())) {
                        i.remove();
                    }
                } else {
                    item.getPatient()
                            .setNamn(joinNames(personSvar));
                }
            } else if (personSvar.getStatus() == PersonSvar.Status.ERROR) {
                throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
            } else {
                LOG.warn("Patient with personnummer '{}' was not found in PU-service, not including in list of sjukfall", pnr.getPnrHash());
                // i.remove();
                item.getPatient().setNamn("Namn okänt");
            }
        }
    }

    @Override
    public void enrichWithPatientNameAndFilterSekretess(List<SjukfallPatientRS> patientSjukfall) {
        if (patientSjukfall.size() == 0) {
            return;
        }

        if (patientSjukfall.get(0).getIntyg().size() == 0) {
            throw new IllegalStateException("Cannot process sjukfall not consisting of at least one intyg.");
        }

        String pnr = patientSjukfall.get(0).getIntyg().get(0).getPatient().getId();
        Personnummer personnummer = Personnummer.createValidatedPersonnummerWithDash(pnr)
                .orElseThrow(() -> new IllegalArgumentException("Unparsable personnummer"));

        PersonSvar personSvar = puService.getPerson(personnummer);
        if (personSvar.getStatus() == PersonSvar.Status.FOUND) {
            RehabstodUser user = userService.getUser();

            if (personSvar.getPerson().isSekretessmarkering() && !(user.isLakare()
                    && patientSjukfall.get(0).getIntyg().get(0).getEnhetId().equalsIgnoreCase(user.getValdVardenhet().getId()))) {
                throw new IllegalStateException("Cannot show patient details for patient having sekretessmarkering");
            } else {
                // Uppdatera namnet på samtliga ingående intyg i sjukfallet.
                patientSjukfall.stream()
                        .flatMap(ps -> ps.getIntyg().stream())
                        .forEach(i -> i.getPatient().setNamn(joinNames(personSvar)));
            }
        } else if (personSvar.getStatus() == PersonSvar.Status.ERROR) {
            throw new IllegalStateException("Could not contact PU service, not showing any sjukfall.");
        } else {
            throw new IllegalStateException("Could not find patient in PU service, not showing any sjukfall.");
        }
    }

    private String joinNames(PersonSvar personSvar) {
        return Joiner.on(' ').skipNulls()
                .join(personSvar.getPerson().getFornamn(),
                        personSvar.getPerson().getMellannamn(),
                        personSvar.getPerson().getEfternamn());
    }
}
