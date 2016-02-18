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
package se.inera.intyg.rehabstod.service.diagnos;

import java.util.List;

import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKapitel;
import se.inera.intyg.rehabstod.service.diagnos.dto.DiagnosKategori;

/**
 * Created by marced on 08/02/16.
 */

public interface DiagnosKapitelService {
    DiagnosKapitel OGILTIGA_DIAGNOSKODER_KAPITEL = new DiagnosKapitel(
            new DiagnosKategori(' ', 0),
            new DiagnosKategori(' ', 0),
            "Utan giltig diagnoskod");

    /**
     * Get list of all defined {@link DiagnosKapitel}.
     *
     * @return
     */
    List<DiagnosKapitel> getDiagnosKapitelList();

    /**
     * Try to match a diagnoskod string to a DiagnosKapitel.
     * Default to a unknown kapitel if no other match.
     *
     * @param diagnosKod
     * @return
     */
    DiagnosKapitel getDiagnosKapitel(String diagnosKod);

}