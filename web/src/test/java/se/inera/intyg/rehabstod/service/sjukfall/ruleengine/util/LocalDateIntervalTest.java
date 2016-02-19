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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine.util;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by marced on 19/02/16.
 */
// CHECKSTYLE:OFF MagicNumber
public class LocalDateIntervalTest {

    @Test
    public void testGetDurationInDays() throws Exception {
        assertEquals(1, new LocalDateInterval(LocalDate.parse("2016-01-01"), LocalDate.parse("2016-01-01")).getDurationInDays());
        assertEquals(31, new LocalDateInterval(LocalDate.parse("2016-01-01"), LocalDate.parse("2016-01-31")).getDurationInDays());
        assertEquals(3, new LocalDateInterval(LocalDate.parse("2016-02-28"), LocalDate.parse("2016-03-01")).getDurationInDays());
        assertEquals(5, new LocalDateInterval(LocalDate.parse("2016-01-28"), LocalDate.parse("2016-02-01")).getDurationInDays());
    }
}