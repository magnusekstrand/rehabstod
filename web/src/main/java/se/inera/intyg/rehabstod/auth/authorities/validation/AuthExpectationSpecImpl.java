/*
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

package se.inera.intyg.rehabstod.auth.authorities.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.authorities.AuthoritiesException;
import se.inera.intyg.rehabstod.auth.authorities.Privilege;
import se.inera.intyg.rehabstod.auth.authorities.RequestOrigin;

/**
 * Created by marced on 18/12/15.
 */
public class AuthExpectationSpecImpl implements AuthExpectationSpecification {

    /*
     * Instance context state
     */
    private RehabstodUser user;
    private Optional<String> intygsTypeContext;

    /*
     * Constraints states
     */
    private Optional<String[]> featureConstraints = Optional.empty();
    private Optional<String[]> featureNotConstraints = Optional.empty();

   // private Optional<RehabstodUserOriginType[]> originConstraints = Optional.empty();
   // private Optional<RehabstodUserOriginType[]> originNotConstraints = Optional.empty();

    private Optional<String[]> roleConstraints = Optional.empty();
    private Optional<String[]> roleNotConstraints = Optional.empty();

    private Optional<String> privilegeConstraint = Optional.empty();
    private Optional<String> privilegeNotConstraint = Optional.empty();

    private List<String> errors = new ArrayList<String>();

    public AuthExpectationSpecImpl(RehabstodUser user, Optional<String> intygstyp) {
        this.user = user;
        this.intygsTypeContext = intygstyp;
    }

    @Override
    public boolean isVerified() {
        errors.clear();

        if (featureConstraints.isPresent() && Arrays.stream(featureConstraints.get()).noneMatch(fc -> this.checkHasFeature(fc))) {
            errors.add(String.format("mandatory features '%s' was not present in users features.", String.join(",", featureConstraints.get())));
        }

        if (featureNotConstraints.isPresent() && Arrays.stream(featureNotConstraints.get()).anyMatch(fc -> this.checkHasFeature(fc))) {
            errors.add(String.format("forbidden features '%s' was present in users features.", String.join(",", featureNotConstraints.get())));
        }

//        if (originConstraints.isPresent() && Arrays.stream(originConstraints.get()).noneMatch(oc -> this.checkHasOrigin(oc.name()))) {
//            errors.add(String.format("mandatory origins '%s' did not match users origin value of '%s'.",
//                    Arrays.stream(originConstraints.get()).map(o -> o.name()).collect(Collectors.joining(",")), this.user.getOrigin()));
//        }
//
//        if (originNotConstraints.isPresent() && Arrays.stream(originNotConstraints.get()).anyMatch(oc -> this.checkHasOrigin(oc.name()))) {
//            errors.add(String.format("forbidden features '%s' matched user orgin value '%s'",
//                    Arrays.stream(originNotConstraints.get()).map(o -> o.name()).collect(Collectors.joining(",")), this.user.getOrigin()));
//        }

        if (roleConstraints.isPresent() && Arrays.stream(roleConstraints.get()).noneMatch(rc -> this.checkRole(rc))) {
            errors.add(String.format("mandatory roles '%s' was not present in users roles.", String.join(",", roleConstraints.get())));
        }

        if (roleNotConstraints.isPresent() && Arrays.stream(roleNotConstraints.get()).anyMatch(rc -> this.checkRole(rc))) {
            errors.add(String.format("forbidden roles '%s' was present in users roles.", String.join(",", roleNotConstraints.get())));
        }

        if (privilegeConstraint.isPresent() && !checkHasPrivilege(privilegeConstraint.get())) {
            errors.add(String.format("user does not have mandatory privilege '%s'.", privilegeConstraint.get()));
        }

        if (privilegeNotConstraint.isPresent() && checkHasPrivilege(privilegeNotConstraint.get())) {
            errors.add(String.format("forbidden privilege '%s' was present in user privileges.", privilegeNotConstraint.get()));
        }

        return errors.isEmpty();
    }

    @Override
    public void orThrow() {
        if (!isVerified()) {
            throw new AuthoritiesException("Authorization Validation failed because " + String.join(", ", errors));
        }
    }

    @Override
    public AuthExpectationSpecification features(String... featureConstraints) {
        this.featureConstraints = Optional.of(featureConstraints);
        return this;
    }

    @Override
    public AuthExpectationSpecification notFeatures(String... featureNotConstraints) {
        this.featureNotConstraints = Optional.of(featureNotConstraints);
        return this;
    }

    private boolean checkHasFeature(String featureConstraint) {

        if (!this.user.getFeatures().contains(featureConstraint)) {
            return false;
        }

        if (this.intygsTypeContext.isPresent()) {
            String intygsFeatureConstraint = featureConstraint + "." + this.intygsTypeContext.get();
            return this.user.getFeatures().contains(intygsFeatureConstraint);
        }

        return true;
    }

//    @Override
//    public AuthExpectationSpecification origins(WebCertUserOriginType... validOriginTypes) {
//        this.originConstraints = Optional.of(validOriginTypes);
//        return this;
//    }
//
//    @Override
//    public AuthExpectationSpecification notOrigins(WebCertUserOriginType... invalidOriginTypes) {
//        this.originNotConstraints = Optional.of(invalidOriginTypes);
//        return this;
//    }

    private boolean checkHasOrigin(String originConstraint) {
        return this.user.getOrigin() != null && this.user.getOrigin().equals(originConstraint);

    }

    @Override
    public AuthExpectationSpecification roles(String... validRoles) {
        this.roleConstraints = Optional.of(validRoles);
        return this;
    }

    @Override
    public AuthExpectationSpecification notRoles(String... invalidRoles) {
        this.roleNotConstraints = Optional.of(invalidRoles);
        return this;
    }

    private boolean checkRole(String role) {
        return this.user.getRoles() != null && this.user.getRoles().containsKey(role);
    }

    @Override
    public AuthExpectationSpecification privilege(String privilegeConstraint) {
        this.privilegeConstraint = Optional.of(privilegeConstraint);
        return this;
    }

    @Override
    public AuthExpectationSpecification notPrivilege(String privilegeNotConstraint) {
        this.privilegeNotConstraint = Optional.of(privilegeNotConstraint);
        return this;
    }

    private boolean checkHasPrivilege(String privilegeConstraint) {
        final Privilege privilegeConfig = this.user.getAuthorities() != null ? this.user.getAuthorities().get(privilegeConstraint) : null;

        if (privilegeConfig == null) {
            return false;
        }

        if (this.intygsTypeContext.isPresent() && (privilegeConfig.getIntygstyper().size() > 0)
                && privilegeConfig.getIntygstyper().stream().noneMatch(t -> t.equals(this.intygsTypeContext.get()))) {
            return false;
        }

        // Does the previlege have requestOrigin constraint(s)?
        if (privilegeConfig.getRequestOrigins().size() > 0) {
            // The users origin must match one of them
            Optional<RequestOrigin> matchingOriginConfig = privilegeConfig.getRequestOrigins().stream()
                    .filter(ro -> ro.getName().equals(this.user.getOrigin())).findAny();
            if (!matchingOriginConfig.isPresent()) {
                return false;
            }

            // If the originConfig has a intygstypeConstraint - on of them must also match any given intygstype
            // context..
            if (this.intygsTypeContext.isPresent() && (matchingOriginConfig.get().getIntygstyper().size() > 0)
                    && matchingOriginConfig.get().getIntygstyper().stream().noneMatch(t -> t.equals(this.intygsTypeContext.get()))) {
                return false;
            }

        }

        // we passed all checks, so privilege is granted
        return true;

    }
}
