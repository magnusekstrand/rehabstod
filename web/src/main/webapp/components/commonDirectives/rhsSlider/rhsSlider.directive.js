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

angular.module('rehabstodApp').directive('rhsSlider',
    ['messageService',
        function(/*messageService*/) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    sliderModel: '=',
                    range: '=',
                    min: '=',
                    max: '=',
                    ticksPositions: '=',
                    ticksLabels: '=',
                    ticks: '=',
                    position: '='
                },
                controller: function($scope) {

                    $scope.value = $scope.sliderModel;
                    $scope.step = 1;
                    $scope.ticksSnapBounds = 1;

                    $scope.formatterFn =  function(value) {
                        var text;

                        if ($scope.range) {
                            if (value === $scope.sliderModel[1]) {
                                text = 'Till';
                            } else {
                                text = 'Från';
                            }

                            text += '\n';

                            if (value === $scope.max) {
                                text += '365+';
                            } else {
                                text += value;
                            }
                        }
                        else {
                            text = value;
                        }


                        if (value === 1) {
                            text += ' dag';
                        } else {
                            text += ' dagar';
                        }

                        return text;
                    };
                },
                templateUrl: 'components/commonDirectives/rhsSlider/rhsSlider.directive.html'
            };
        }]);