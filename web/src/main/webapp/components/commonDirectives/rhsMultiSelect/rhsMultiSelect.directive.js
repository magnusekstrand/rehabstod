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
angular.module('rehabstodApp').directive('rhsMultiSelect',
    function() {
        'use strict';

        return {
            restrict: 'E',
            replace: true,
            scope: {
                noneSelectedTitle: '@',
                highLightEnabled: '=',
                itemsModel: '='
            },

            templateUrl: 'components/commonDirectives/rhsMultiSelect/rhsMultiSelect.directive.html',
            controller: function($scope) {

                //Template just need access to the array
                $scope.items = $scope.itemsModel.get();

                //Calculate title based on whether item(s) are selected or not.
                $scope.getTitle = function() {
                    var nrItemsSelected = $scope.itemsModel.getSelected().length;
                    if (nrItemsSelected === 0) {
                        return $scope.noneSelectedTitle;
                    } else if (nrItemsSelected === 1) {
                        return $scope.itemsModel.getSelected()[0].displayValue;
                    } else {
                        return nrItemsSelected + ' valda';
                    }
                };

                $scope.toggleItem = function(item) {
                    item.selected = !item.selected;
                };

            }
        };
    });

