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

angular.module('rehabstodApp').directive('rhsTableSearchConnector',
    ['$timeout', 'SjukfallFilterViewState', 'sessionCheckService',
        function($timeout, SjukfallFilterViewState, sessionCheckService) {
            'use strict';

            return {
                restrict: 'E',
                require: '^stTable',
                link: function($scope, element, attr, table) {

                    var onFilterstateUpdated = function(newFilterParameters) {
                        $timeout(function() {
                            table.search(newFilterParameters, 'customSearch');
                        });
                        //Indicate that the user has interacted with the filter
                        sessionCheckService.registerUserAction();

                    };

                    //Watch for changes in current filter state
                    $scope.filterViewState = SjukfallFilterViewState;
                    $scope.$watch('filterViewState.getCurrentFilterState()', onFilterstateUpdated, true);

                    $scope.table = table;
                    $scope.$watch('table.getFilteredCollection()', function() {
                        //Indicate that the user has interacted with the table, either by chaing filter that triggered new content, or by sorting it
                        sessionCheckService.registerUserAction();
                    }, true);


                }
            };
        }]);
