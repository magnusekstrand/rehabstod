angular.module('rehabstodApp').directive('rhsStatPanel',
    ['UnitCertificateSummaryModel', 'UnitCertificateSummaryProxy', '$rootScope',
        function(UnitCertificateSummaryModel, UnitCertificateSummaryProxy, $rootScope) {
            'use strict';

            return {
                restrict: 'E',
                scope: {
                    vardenhet: '='
                },
                controller: function($scope) {

                    /**
                     * Private functions
                     */
                    function _loadData() {
                        UnitCertificateSummaryProxy.get().then(function(data) {
                            UnitCertificateSummaryModel.set(data);
                        });
                    }

                    var unregisterFn = $rootScope.$on('SelectedUnitChanged', function(/*event, value*/) {
                        _loadData();
                    });
                    //rootscope on event listeners aren't unregistered automatically when 'this' directives
                    //scope is destroyed, so let's take care of that.
                    $scope.$on('$destroy', unregisterFn);

                    /**
                     * Exposed scope properties
                     */
                    $scope.model = UnitCertificateSummaryModel.get();

                    if ($scope.model.total === null) {
                        _loadData();
                    }
                },
                templateUrl: 'components/commonDirectives/rhsStatPanel/rhsStatPanel.directive.html'
            };
        }]);
