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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine.testdata.builders;

import se.riv.clinicalprocess.healthcond.certificate.types.v2.PersonId;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.Patient;

/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class PatientT {

    private PatientT() {
    }

    public static class PatientBuilder implements Builder<Patient> {

        private PersonId personId;
        private String fornamn;
        private String efternamn;
        private String mellannamn;

        public PatientBuilder() {
        }

        public PatientBuilder personId(PersonId personId) {
            this.personId = personId;
            return this;
        }

        public PatientBuilder fornamn(String fornamn) {
            this.fornamn = fornamn;
            return this;
        }

        public PatientBuilder efternamn(String efternamn) {
            this.efternamn = efternamn;
            return this;
        }

        public PatientBuilder mellannamn(String mellannamn) {
            this.mellannamn = mellannamn;
            return this;
        }

        @Override
        public Patient build() {
            Patient patient = new Patient();
            patient.setPersonId(personId);
            patient.setFornamn(fornamn);
            patient.setEfternamn(efternamn);
            patient.setMellannamn(mellannamn);

            return patient;
        }
    }
}
