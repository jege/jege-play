(function() {
  'use strict';
  var Entity, Service, app;

  app = angular.module('app', ['ngResource']).constant('apiUrl', 'http://localhost:9000\:9000/api/v1').config([
    '$routeProvider', function($routeProvider) {
      return $routeProvider.when('/', {
        templateUrl: '/views/index',
        controller: 'IndexCtrl'
      }).otherwise({
        redirectTo: '/'
      });
    }
  ]).config([
    '$locationProvider', function($locationProvider) {
      return $locationProvider.html5Mode(true);
    }
  ]);

  app.controller('AppCtrl', ['$scope', '$routeParams', function($scope, $routeParams) {}]);

  app.controller('IndexCtrl', ['$scope', '$routeParams', function($scope, $routeParams) {}]);

  Entity = (function() {

    function Entity() {}

    return Entity;

  })();

  Service = (function() {

    function Service() {}

    return Service;

  })();

}).call(this);
