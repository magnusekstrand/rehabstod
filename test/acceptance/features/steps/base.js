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

    this.Given(/^synns all innehåll$/, function(callback) {
        element(by.css('.navbar-header')).isPresent().then(function() {
            // rehabstodBase.navBar.isPresent().then(function(val) {
            logg('OK - Innehållet synns! : ');
        }, function(reason) {
            callback('FEL - Innehållet synns ej : ' + reason);
        }).then(callback);
    });

    this.Given(/^att jag är inloggad som "([^"]*)"$/, function(arg1, callback) {
        // expect(rehabstodBase.header.getText()).to.eventually.contain(arg1).then(function(val) {
        expect(element(By.css('.headerbox-user-profile')).getText()).to.eventually.contain(arg1).then(function(val) {
            logg('OK - Inloggad som : ' + val);
        }, function(reason) {
            callback('FEL - inloggning ej korrekt: ' + reason);
        }).then(callback);
    });

    this.Given(/^jag byter till flik "([^"]*)"$/, function(arg1, callback) {
        if (arg1 === 'Om Rehabstöd') {
            logg('Byter flik till: ' + arg1);
            element(by.css('.ng-binding.ng-scope')).sendKeys(protractor.Key.SPACE).then(callback);
        } else {
            logg('Byter flik till: ' + arg1);
            element(by.css('.ng-scope')).sendKeys(protractor.Key.SPACE).then(callback);
        }
    });

    this.Given(/^elementen "([^"]*)" synns$/, function(arg1, callback) {
        expect(element(by.css('.container.ng-scope'))).to.eventually.contain(arg1).then(function(elements) {
            logg('OK - sidan innehåller :' + elements);
        }, function(reason) {
            callback('FEL - fel: ' + reason);
        }).then(callback);
    });

    this.Given(/^loggas jag ut$/, function(callback) {
        expect(element(by.id('logoutLink'))).isPresent().to.become(true).then(function() {
            element(by.id('logoutLink')).sendKeys(protractor.Key.SPACE);
            logg('OK - Loggar ut');
        }, function(reason) {
            callback('FEL - fel: ' + reason);
        }).then(callback);
    });

};