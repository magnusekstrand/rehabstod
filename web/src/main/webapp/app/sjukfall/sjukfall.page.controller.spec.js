describe('Controller: SjukfallPageCtrl', function () {
  'use strict';

  // load the controller's module
  beforeEach(module('rehabstodApp'));

  var scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function (_$httpBackend_, $controller, $rootScope) {
    scope = $rootScope.$new();
    $controller('SjukfallPageCtrl', {
      $scope: scope
    });
  }));
});