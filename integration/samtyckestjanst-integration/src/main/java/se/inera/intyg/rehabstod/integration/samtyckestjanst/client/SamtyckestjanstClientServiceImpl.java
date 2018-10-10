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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.rehabstod.integration.sparrtjanst.util.SparrtjanstUtil;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.informationsecurity.authorization.blocking.CheckBlocks.v4.rivtabp21.CheckBlocksResponderInterface;
import se.riv.informationsecurity.authorization.blocking.CheckBlocksResponder.v4.CheckBlocksResponseType;
import se.riv.informationsecurity.authorization.blocking.CheckBlocksResponder.v4.CheckBlocksType;
import se.riv.informationsecurity.authorization.blocking.v4.InformationEntityType;

/**
 * Created by Magnus Ekstrand 2018-10-10.
 */
@Service
public class SamtyckestjanstClientServiceImpl implements SamtyckestjanstClientService {

    @Autowired
    private CheckConsentResponderInterface service;

    @Value("${samtyckestjanst.service.logicalAddress}")
    private String logicalAddress;

    @Override
    public CheckConsentResponseType getCheckConsent(String vgHsaId, String veHsaId, String userHsaId, String patientId,
            List<IntygData> intygLista) {

        return null;
    }

}
