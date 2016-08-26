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
 * Created by marced
 */

'use strict';

var RehabstodBasePage = require('./rehabstod.base.page.js');

var RehabstodSelectUnitPageBase = RehabstodBasePage._extend({
    init: function init() {
        init._super.call(this);
        this.at = element(by.id('rhs-select-unit-page'));

    },
    get: function () {
        this.getPage('start');
    },
    isAt: function isAt() {
        return isAt._super.call(this);
    },
    selectUnit: function(hsaId) {
            element(by.id('rhs-vardenhet-selector-select-active-unit-' + hsaId + '-link')).click();
    },
    expandUnit: function(hsaId) {
        element(by.id('expand-enhet-' + hsaId)).click();
    }
});

module.exports = new RehabstodSelectUnitPageBase();
