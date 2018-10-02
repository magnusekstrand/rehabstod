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
package se.inera.intyg.rehabstod.service.sjukfall.dto;

import se.inera.intyg.rehabstod.web.model.SjukfallPatient;

import java.util.List;

/**
 * Created by marced on 2018-10-02.
 */
public class FilteredSjukFallByPatientResult {
    private final List<SjukfallPatient> rehabstodSjukfall;
    private final SjfMetaData sjfMetaData;

    public FilteredSjukFallByPatientResult(List<SjukfallPatient> rehabstodSjukfall, SjfMetaData sjfMetaData) {

        this.rehabstodSjukfall = rehabstodSjukfall;
        this.sjfMetaData = sjfMetaData;
    }

    public List<SjukfallPatient> getRehabstodSjukfall() {
        return rehabstodSjukfall;
    }

    public SjfMetaData getSjfMetaData() {
        return sjfMetaData;
    }
}
