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

angular.module('rehabstodApp').factory('SjukfallSummaryModel',
    function() {
        'use strict';

        var data = {};

        _reset();

        function _reset() {
            data.total = null;
            data.men = null;
            data.women = null;
            data.groups = null;

            return data;
        }

        function _set(newData) {
            data.total = newData.total;
            data.men = newData.men;
            data.women = newData.women;
            data.groups = newData.groups;
            data.diagnoseGroupData = [];

            angular.forEach(data.groups, function(group) {
                data.diagnoseGroupData.push(
                    {
                        id: group.grupp.id,
                        name: group.grupp.name,
                        y: group.count
                    } );
            });
        }

        function _get() {
            return data;
        }

        return {
            reset: _reset,
            set: _set,
            get: _get
        };
    }
);