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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.testdata.IntygsDataGenerator;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 10/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallResolverReduceRightTest {
    // CHECKSTYLE:OFF MagicNumber

    private static final String LOCATION_INTYGSDATA = "classpath:SjukfallResolverTest/intygsdata-resolver-right.csv";

    private static List<IntygsData> intygsDataList;

    private SjukfallResolverImpl resolver;
    private SjukfallMapper mapper;


    @BeforeClass
    public static void initTestData() throws IOException {
        IntygsDataGenerator generator = new IntygsDataGenerator(LOCATION_INTYGSDATA);
        intygsDataList = generator.generate().get();
    }

    @Before
    public void setup() {
        mapper = new SjukfallMapperImpl();
        resolver = new SjukfallResolverImpl(mapper);
    }

    @Test
    public void testFall1() {
        List<SortableIntygsData> result = getTestData("fall-1-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
    }

    @Test
    public void testFall2() {
        List<SortableIntygsData> result = getTestData("fall-2-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 2 but was " + result.size(), result.size() == 2);
        assertEquals("fall-2-intyg-1", result.get(0).getIntygsId());
        assertEquals("fall-2-intyg-2", result.get(1).getIntygsId());
    }

    @Test
    public void testFall3() {
        List<SortableIntygsData> result = getTestData("fall-3-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 0 but was " + result.size(), result.size() == 0);
    }

    @Test
    public void testFall4() {
        List<SortableIntygsData> result = getTestData("fall-4-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
        assertEquals("fall-4-intyg-1", result.get(0).getIntygsId());
        assertEquals("fall-4-intyg-2", result.get(1).getIntygsId());
        assertEquals("fall-4-intyg-3", result.get(2).getIntygsId());
    }

    @Test
    public void testFall5() {
        List<SortableIntygsData> result = getTestData("fall-5-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
        assertEquals("fall-5-intyg-4", result.get(0).getIntygsId());
        assertEquals("fall-5-intyg-1", result.get(1).getIntygsId());
        assertEquals("fall-5-intyg-2", result.get(2).getIntygsId());
    }

    @Test
    public void testFall6() {
        List<SortableIntygsData> result = getTestData("fall-6-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 2 but was " + result.size(), result.size() == 2);
    }

    private List<SortableIntygsData> getTestData(String key, String aktivtDatum , int maxIntygsGlapp, String initialtDatum) {
        Map<String, List<SortableIntygsData>> data = getTestData(aktivtDatum);
        return resolver.reduceRight(data.get(key), maxIntygsGlapp, LocalDate.parse(initialtDatum));
    }

    private Map<String, List<SortableIntygsData>> getTestData(String aktivtDatum) {
        return resolver.toMap(intygsDataList, LocalDate.parse(aktivtDatum));
    }

    private Map<String, List<SortableIntygsData>> getTestData(LocalDate aktivtDatum) {
        return resolver.toMap(intygsDataList, aktivtDatum);
    }

    // CHECKSTYLE:ON

}