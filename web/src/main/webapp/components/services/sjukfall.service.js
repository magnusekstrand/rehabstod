angular.module('rehabstodApp').factory('SjukfallService', [
    '$log', 'StringHelper', 'messageService', 'SjukfallProxy', 'SjukfallModel', 'SjukfallFilterViewState',
    function($log, StringHelper, messageService, SjukfallProxy, SjukfallModel, SjukfallFilterViewState) {
        'use strict';

        var loading = false;

        function _loadSjukfall(force, skipReset) {

            if (loading) {
                return;
            }

            var empty = SjukfallModel.get().length === 0;

            if (force || empty) {
                loading = true;
                if (!skipReset) {
                    SjukfallFilterViewState.reset();
                    SjukfallModel.reset();
                }

                var query = {
                    maxIntygsGlapp: SjukfallFilterViewState.get().glapp
                };

                return SjukfallProxy.get(query).then(function(successData) {
                    SjukfallModel.set(successData);
                    loading = false;
                }, function(errorData) {
                    $log.debug('Failed to get sjukfall.');
                    $log.debug(errorData);

                    SjukfallModel.reset();
                    loading = false;
                });
            }
        }

        function _stripHtmlEntities(html) {
            return StringHelper.replaceAll(html,'&shy;','');
        }

        function _exportResult(type, personnummer, sortState) {
            var sort = {
                kolumn: _stripHtmlEntities(messageService.getProperty('label.table.column.' + angular.lowercase(sortState.kolumn))),
                order: messageService.getProperty('label.table.column.sort.' + sortState.order)
            };

            var filterState = SjukfallFilterViewState.getCurrentFilterState();

            var query = {
                sortering: sort,
                maxIntygsGlapp: filterState.glapp,
                fritext: filterState.freeText,
                langdIntervall: {
                    min: '' + filterState.sjukskrivningslangd[0],
                    max: '' + (filterState.sjukskrivningslangd[1] === null? '365+' : filterState.sjukskrivningslangd[1])
                },
                lakare: filterState.lakare,
                diagnosGrupper: filterState.diagnosKapitel,
                personnummer: personnummer
            };

            return SjukfallProxy.exportResult(type, query);
        }

        function _isLoading() {
            return loading;
        }

        return {
            loadSjukfall: _loadSjukfall,
            exportResult: _exportResult,
            isLoading: _isLoading
        };
    }]);