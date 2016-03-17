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

angular.module('rehabstodApp').factory('SjukfallProxy',
    function($http, $log, $q,
        ObjectHelper, networkConfig, $window) {
        'use strict';

        /*
         * Get sjukfall for selected Vardenhet
         */
        function _get(query) {

            var promise = $q.defer();

            var restPath = '/api/sjukfall';
            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.getsjukfall.title',
                    errorTextKey: 'server.error.default.text'
                },
                timeout: networkConfig.defaultTimeout
            };
            $http.post(restPath, query, config).success(function(data) {
                if (!ObjectHelper.isDefined(data)) {
                    promise.reject({errorCode: data, message: 'invalid data'});
                } else {
                    promise.resolve(data);
                }
            }).error(function(data, status) {
                $log.error('error ' + status);
                // Let calling code handle the error of no data response
                if (data === null) {
                    promise.reject({errorCode: data, message: 'no response'});
                } else {
                    promise.reject(data);
                }
            });

            return promise.promise;
        }

        function _exportResult(type, query) {

            var restPath = '/api/sjukfall/' + type;
            var inputs = '';

            inputs += _addInput('langdIntervall.max', query.langdIntervall.max);
            inputs += _addInput('langdIntervall.min', query.langdIntervall.min);
            inputs += _addInput('sortering.kolumn', query.sortering.kolumn);
            inputs += _addInput('sortering.order', query.sortering.order);
            inputs += _addInput('fritext', query.fritext);
            inputs += _addInput('maxIntygsGlapp', query.maxIntygsGlapp);

            angular.forEach(query.lakare, function(item) {
                inputs += _addInput('lakare', item);
            });

            angular.forEach(query.diagnosGrupper, function(item) {
                inputs += _addInput('diagnosGrupper', item);
            });

            angular.forEach(query.personnummer, function(item) {
                inputs += _addInput('personnummer', item);
            });

            //send request
            $window.jQuery('<form action="' + restPath + '" target="_blank" method="post">' + inputs + '</form>')
                .appendTo('body').submit().remove();
        }

        function _addInput(name, item) {
            return '<input type="hidden" name="' + name + '" value="' + item + '" />';
        }

        // Return public API for the service
        return {
            get: _get,
            exportResult: _exportResult
        };
    });
