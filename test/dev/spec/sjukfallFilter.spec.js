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

/*globals browser */
/*globals pages */
/*globals describe,it,helpers */
'use strict';

var specHelper = rhsTestTools.helpers.spec;

var startPage = rhsTestTools.pages.startPage;
var sjukfallPage = rhsTestTools.pages.sjukfallPage;

describe('Hantera filtret', function() {

    beforeAll(function() {
        browser.ignoreSynchronization = false;
        specHelper.login();
        startPage.clickMyUnit();
    });

    beforeEach(function() {
        startPage.clickSjukfall();
        expect(sjukfallPage.isAt()).toBe(true);
    });

    it('Göm filtret', function() {
        expect(sjukfallPage.resetButton().isPresent()).toBeTruthy();
        expect(sjukfallPage.settingsButton().isPresent()).toBeTruthy();

        sjukfallPage.clickHideFilter();

        expect(sjukfallPage.resetButton().isPresent()).toBeFalsy();
        expect(sjukfallPage.settingsButton().isPresent()).toBeFalsy();

        sjukfallPage.clickHideFilter();
    });

    it('Återställ filtret', function() {
        var freeTextValue = 'Hej';

        sjukfallPage.freeTextInput().sendKeys(freeTextValue);

        expect(sjukfallPage.freeTextInput().getAttribute('value')).toEqual(freeTextValue);

        sjukfallPage.clickResetFilter();

        expect(sjukfallPage.freeTextInput().getAttribute('value')).toEqual('');
    });

    it('Ändra glapp', function() {
        sjukfallPage.settingsButton().click();

        // TODO: Ändra glapp

        sjukfallPage.settingsSaveBtn.click();
    });

    it('Öppna och stäng inställningar', function() {
        sjukfallPage.settingsButton().click();

        expect(sjukfallPage.settingsSaveBtn.isPresent()).toBeTruthy();

        sjukfallPage.settingsCloseBtn.click();

        expect(sjukfallPage.settingsSaveBtn.isPresent()).toBeFalsy();
    });

});
