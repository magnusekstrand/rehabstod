/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

'use strict';

var specHelper = rhsTestTools.helpers.spec;
var _wp = rhsTestTools.pages.welcomePage;
var rehabstodBase = rhsTestTools.pages.rehabstodBase;

module.exports = function() {

    this.Given(/^jag är inloggad som läkare$/, function(callback) {

        expect(element(By.css('.headerbox-user-profile')).getText()).to.eventually.contain(roleName + ' - ' + userObj.fornamn + ' ' + userObj.efternamn).then(function(val) {
            logg('OK - Inloggad som läkare: ' + val);
        }, function(reason) {
            callback('FEL - inloggning ej korrekt: ' + reason);
        }).then(callback);

    });

    this.Given(/^synns all innehåll$/, function(callback) {
        // Write code here that turns the phrase above into concrete actions
        callback.pending();
    });

};