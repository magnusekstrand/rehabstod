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

/* globals browser */

/**
 * Created by erik
 */

'use strict';

var RehabstodBasePage = require('./rehabstod.base.page.js');

var RehabstodStartPageBase = RehabstodBasePage._extend({
    init: function init() {
        init._super.call(this);
        this.at = element(by.id('startPage'));
    },
    get: function () {
        browser.get('#');
    },
    isAt: function isAt() {
        return isAt._super.call(this);
    },
    clickFullUnit: function() {
        element(by.id('rhs-startPage-allBtn')).click();
    },
    clickMyUnit: function() {
        element(by.id('rhs-startPage-myBtn')).click();
    },
    clickBytVardenhet: function() {
        element(by.id('rhs-header-otherLocations')).click();
    }
});

module.exports = new RehabstodStartPageBase();