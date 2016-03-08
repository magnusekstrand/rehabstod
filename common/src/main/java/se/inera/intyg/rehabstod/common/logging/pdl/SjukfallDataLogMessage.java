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
package se.inera.intyg.rehabstod.common.logging.pdl;

import se.inera.intyg.common.logmessages.ActivityPurpose;
import se.inera.intyg.common.logmessages.ActivityType;
import se.inera.intyg.common.logmessages.PdlLogMessage;

/**
 * Created by eriklupander on 2016-02-19.
 */
public abstract class SjukfallDataLogMessage {

    private static final long serialVersionUID = -4683928451142580674L;

    private SjukfallDataLogMessage() {

    }

    public static PdlLogMessage build() {
        return new PdlLogMessage(ActivityType.READ, ActivityPurpose.CARE_TREATMENT);
    }
}