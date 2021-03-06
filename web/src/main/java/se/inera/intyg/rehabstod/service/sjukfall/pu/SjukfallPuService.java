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

import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import java.util.List;

/**
 * Created by eriklupander on 2017-09-05.
 */
public interface SjukfallPuService {

    String SEKRETESS_SKYDDAD_NAME_PLACEHOLDER = "Sekretessmarkerad uppgift";
    String SEKRETESS_SKYDDAD_NAME_UNKNOWN = "Namn okänt";

    /**
     * Removes sjukfall belonging to patients with sekretessmarkering. Ignores PU errors, will not exclude such
     * sjukfall. Removes patient name rather than enriching with PU info since the purpose of this method is for
     * enabling stats aggregation.
     *
     * @param sjukfallList
     */
    void filterSekretessForSummary(List<SjukfallEnhet> sjukfallList);

    /**
     * Filters out sjukfall if the patient has sekretessmarkering and the user doesn't have the requisite privilege.
     *
     * If the PU-service cannot be reached, an Exception must be thrown.
     *
     * @param sjukfallList
     */
    void enrichWithPatientNamesAndFilterSekretess(List<SjukfallEnhet> sjukfallList);

    /**
     * Filters out sjukfall if the patient has sekretessmarkering and the user doesn't have the requisite privilege.
     *
     * If the PU-service cannot be reached, an Exception must be thrown.
     *
     * @param patientSjukfall
     */
    void enrichWithPatientNameAndFilterSekretess(List<SjukfallPatient> patientSjukfall);
}
