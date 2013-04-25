"use strict";
var Entity, Service, app;

app = angular.module("app", ["ngResource"]).constant("Config", {
  apiUrl: "http://localhost:9000\:9000/api/v1"
}).constant("Events", {}).config([
  "$routeProvider", function($routeProvider) {
    return $routeProvider.when("/", {
      templateUrl: "/views/index",
      controller: "IndexCtrl"
    }).otherwise({
      redirectTo: "/"
    });
  }
]).config([
  "$locationProvider", function($locationProvider) {
    return $locationProvider.html5Mode(true);
  }
]);

Entity = (function() {
  function Entity() {}

  return Entity;

})();

Service = (function() {
  function Service() {}

  return Service;

})();
