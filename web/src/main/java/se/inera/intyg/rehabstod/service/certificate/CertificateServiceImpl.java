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
package se.inera.intyg.rehabstod.service.certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.integration.it.service.IntygstjanstIntegrationService;
import se.inera.intyg.rehabstod.service.certificate.dto.SjukfallSummary;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.riv.clinicalprocess.healthcond.rehabilitation.v1.IntygsData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2016-02-01.
 */
@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private IntygstjanstIntegrationService intygstjanstIntegrationService;

    @Autowired
    private UserService userService;

    @Override
    public List<IntygsData> getIntygsData() {
        String hsaId = userService.getUser().getValdVardenhet().getId();
        return intygstjanstIntegrationService.getIntygsDataForCareUnit(hsaId);
    }

    @Override
    public SjukfallSummary getSummary() {

        String hsaId = userService.getUser().getValdVardenhet().getId();
        List<IntygsData> intygsDataList = intygstjanstIntegrationService.getIntygsDataForCareUnit(hsaId);

        List<String> personNummer = intygsDataList.stream().map(e -> e.getPatient().getPersonId().getExtension()).distinct().collect(Collectors.toList());

        int total = personNummer.size();
        int menTotal = (int) personNummer.stream().filter(p -> p.substring(11, 12).matches("^\\d*[13579]$")).count();
        int womenTotal = total - menTotal;

        double men = (menTotal*1.0/total)*100;
        double women = (womenTotal*1.0/total)*100;

        return new SjukfallSummary(total, men, women);
    }
}