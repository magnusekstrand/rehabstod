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

import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;
import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import java.util.List;

/**
 * Created by eriklupander on 2017-09-05.
 */
public interface SjukfallPuService {

    String SEKRETESS_SKYDDAD_NAME_PLACEHOLDER = "Sekretessmarkerad uppgift";
    String SEKRETESS_SKYDDAD_NAME_UNKNOWN = "Namn okänt";

    void enrichWithPatientNamesAndFilterSekretess(List<SjukfallEnhet> sjukfallList);

    void enrichWithPatientNameAndFilterSekretess(List<SjukfallPatient> patientSjukfall);
}
