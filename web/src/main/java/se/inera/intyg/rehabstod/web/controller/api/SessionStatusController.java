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
package se.inera.intyg.rehabstod.web.controller.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.rehabstod.web.controller.api.dto.GetSessionStatusResponse;
import se.inera.intyg.rehabstod.web.filters.SessionTimeoutFilter;

/**
 * Reports basic information about the current session status.
 * This controller works in cooperation with a SessionTimeoutFilter that makes sure that requests to this controller
 * does not prolong the session.
 *
 * @see SessionTimeoutFilter
 * @see org.springframework.security.web.context.SecurityContextRepository SecurityContextRepository
 * @see org.springframework.security.web.context.HttpSessionSecurityContextRepository
 *      HttpSessionSecurityContextRepository
 */

@RestController
@RequestMapping(SessionStatusController.CONTROLLER_REQUEST_MAPPING)
public class SessionStatusController {

    static final String CONTROLLER_REQUEST_MAPPING = "/api/session-auth-check";
    static final String METHOD_REQUEST_MAPPING = "/ping";

    public static final String SESSION_STATUS_CHECK_URI = CONTROLLER_REQUEST_MAPPING + METHOD_REQUEST_MAPPING;

    @Autowired
    SecurityContextRepository repository;

    @RequestMapping(value = SessionStatusController.METHOD_REQUEST_MAPPING, method = RequestMethod.GET)
    public GetSessionStatusResponse getSessionStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
         // The sessionTimeoutFilter should have put a secondsLeft attribute in the request for us to use.
        Long secondsLeft = (Long) request.getAttribute(SessionTimeoutFilter.SECONDS_UNTIL_SESSIONEXPIRE_ATTRIBUTE_KEY);
        return new GetSessionStatusResponse(session != null, repository.containsContext(request), secondsLeft == null ? 0 : secondsLeft);
    }
}
