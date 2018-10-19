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
package se.inera.intyg.rehabstod.integration.samtyckestjanst.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.informationsecurity.authorization.consent.v2.ActionType;
import se.riv.informationsecurity.authorization.consent.v2.ActorType;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by Magnus Ekstrand on 2018-10-10.
 */
public class SamtyckestjanstStubRestApi {

    @Autowired
    private SamtyckestjanstStubStore store;

    @PUT
    @Path("/consent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addConsentForPerson(
            @QueryParam("vgHsaId") String vgHsaId,
            @QueryParam("veHsaId") String veHsaId,
            @QueryParam("personId") String personId,
            @QueryParam("userId") String userId,
            @QueryParam("from") String from,
            @QueryParam("to") String to) {

        ActorType actorType = new ActorType();
        actorType.setEmployeeId(userId);

        ActionType actionType = new ActionType();
        actionType.setRegisteredBy(actorType);
        actionType.setRegistrationDate(LocalDateTime.now());
        actionType.setRequestDate(LocalDateTime.now());
        actionType.setRequestedBy(actorType);

        String assertionId = UUID.randomUUID().toString();
        ConsentData consentData = new ConsentData.Builder(vgHsaId, veHsaId, personId, assertionId, actionType)
                .userHsaId(userId)
                .consentFrom(LocalDateTime.parse(from))
                .consentTo(LocalDateTime.parse(to))
                .build();

        store.add(consentData);
        return Response.ok().build();
    }

    @DELETE
    @Path("/consent/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeConsentForPerson(@PathParam("personId") String personId) {
        store.remove(personId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/consent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAllConsent() {
        store.removeAll();
        return Response.ok().build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllConsent() {
        return Response.ok(store.getAll()).build();

    }

}