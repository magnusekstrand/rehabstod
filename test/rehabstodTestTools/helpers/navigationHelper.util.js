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

/*globals pages */
/*globals describe,it,helpers */
'use strict';

var pages = require('./../pages/pages.js');
var startPage = pages.startPage;
var sjukfallPage = pages.sjukfallPage;
var aboutPage = pages.aboutPage;

module.exports = {

    goToStart: function(success) {
        startPage.clickStart();
        expect(startPage.isAt()).toBe(success || true);
    },

    goToSjukfall: function(success) {
        startPage.clickSjukfall();
        expect(sjukfallPage.isAt()).toBe(success || true);
    }
};