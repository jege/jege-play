"use strict"

app = angular.module("app", ["ngResource"])
  .constant("Config", {
    apiUrl: "http://localhost:9000\:9000/api/v1"
  })
  .constant("Events", {

  })
  .config(["$routeProvider", ($routeProvider) ->
    $routeProvider
      .when("/", {
        templateUrl: "/views/index",
        controller: "IndexCtrl"
      })
      .otherwise({
        redirectTo: "/"
      })
  ])
  .config(["$locationProvider", ($locationProvider) ->
    $locationProvider.html5Mode(true)
  ])

class Entity

class Service
