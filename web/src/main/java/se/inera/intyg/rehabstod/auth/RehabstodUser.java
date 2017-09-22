/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.rehabstod.auth;

import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesConstants;
import se.inera.intyg.rehabstod.auth.pdl.PDLActivityEntry;
import se.inera.intyg.rehabstod.service.Urval;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pebe on 2015-08-11.
 */
public class RehabstodUser extends IntygUser implements Serializable {

    private static final long serialVersionUID = 8711015219408194075L;

    // Handles PDL logging state
    private Map<String, List<PDLActivityEntry>> storedActivities;

    private boolean pdlConsentGiven = false;

    public RehabstodUser(String hsaId, String namn) {
        super(hsaId);
        this.storedActivities = new HashMap<>();
        this.hsaId = hsaId;
        this.namn = namn;
    }

    /** The copy-constructor. */
    public RehabstodUser(IntygUser intygUser, boolean pdlConsentGiven) {
        super(intygUser.getHsaId());
        this.privatLakareAvtalGodkand = intygUser.isPrivatLakareAvtalGodkand();
        this.personId = intygUser.getPersonId();

        this.namn = intygUser.getNamn();
        this.titel = intygUser.getTitel();
        this.forskrivarkod = intygUser.getForskrivarkod();
        this.authenticationScheme = intygUser.getAuthenticationScheme();
        this.vardgivare = intygUser.getVardgivare();
        this.befattningar = intygUser.getBefattningar();
        this.specialiseringar = intygUser.getSpecialiseringar();
        this.legitimeradeYrkesgrupper = intygUser.getLegitimeradeYrkesgrupper();

        this.valdVardenhet = intygUser.getValdVardenhet();
        this.valdVardgivare = intygUser.getValdVardgivare();
        this.authenticationMethod = intygUser.getAuthenticationMethod();

        this.features = intygUser.getFeatures();
        this.roles = intygUser.getRoles();
        this.authorities = intygUser.getAuthorities();
        this.origin = intygUser.getOrigin();

        this.storedActivities = new HashMap<>();

        this.miuNamnPerEnhetsId = intygUser.getMiuNamnPerEnhetsId();
        this.pdlConsentGiven = pdlConsentGiven;
    }

    public Urval getUrval() {
        // If we dont have a role, we can't decide which urval change is allowed, so..
        if (roles == null) {
            return null;
        }

        // Case 1: Lakare should get ISSUED_BY_ME
        if (roles.containsKey(AuthoritiesConstants.ROLE_LAKARE)) {
            return Urval.ISSUED_BY_ME;
        }

        // Case 2: Koordinator should get ALL
        if (roles.containsKey(AuthoritiesConstants.ROLE_KOORDINATOR)) {
            return Urval.ALL;
        }

       return null;
    }

    public Map<String, List<PDLActivityEntry>> getStoredActivities() {
        return storedActivities;
    }


    public Urval getDefaultUrval() {
        return roles.containsKey(AuthoritiesConstants.ROLE_LAKARE) ? Urval.ISSUED_BY_ME : Urval.ALL;
    }

    @Override
    public int getTotaltAntalVardenheter() {
        // count all hasid's in the datastructure
        return (int) getVardgivare().stream().flatMap(vg -> vg.getHsaIds().stream()).count();
    }


    // private scope

    /**
     * If the currently selected vardenhet is not null and is an underenhet/mottagning, this method returns true.
     *
     * @return true if UE, false if not.
     */
    public boolean isValdVardenhetMottagning() {
        if (valdVardenhet == null) {
            return false;
        }

        for (Vardgivare vg : vardgivare) {
            for (Vardenhet ve : vg.getVardenheter()) {
                if (ve.getId().equals(valdVardenhet.getId())) {
                    return false;
                }
                for (Mottagning m : ve.getMottagningar()) {
                    if (m.getId().equals(valdVardenhet.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPdlConsentGiven() {
        return pdlConsentGiven;
    }

    public void setPdlConsentGiven(boolean pdlConsentGiven) {
        this.pdlConsentGiven = pdlConsentGiven;
    }

    private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

}
