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
package se.inera.intyg.rehabstod.web.controller.api.dto;

import se.inera.intyg.rehabstod.service.Urval;

/**
 * Created by marced on 01/02/16.
 */
public class ChangeUrvalRequest {

    private Urval urval;

    public ChangeUrvalRequest() {
    }

    public ChangeUrvalRequest(Urval urval) {
        this.urval = urval;
    }

    public Urval getUrval() {
        return urval;
    }

    public void setUrval(Urval urval) {
        this.urval = urval;
    }
}