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

angular.module('rehabstodApp').directive('rhsTableHeader', ['SjukfallFilterViewState',
    function(SjukfallFilterViewState) {
        'use strict';

        return {
            restrict: 'A',
            scope: {
                user : '=',
                glapp : '='
            },
            templateUrl: '/components/appDirectives/sjukfall/rhsTableHeader/rhsTableHeader.directive.html',
            link: function (scope, element) {
                scope.filterViewState = SjukfallFilterViewState;

                element.find('th[st-sort]').bind('click', function sortClick () {
                    $('body, thead *').css('cursor', 'wait');
                });
            },
            controller: function($scope) {
                $scope.hasFeature = function(feature) {
                    for (var a = 0; a < $scope.user.features.length; a++) {
                        if ($scope.user.features[a] === feature) {
                            return true;
                        }
                    }
                    return false;
                };
            }
        };
    }]);
