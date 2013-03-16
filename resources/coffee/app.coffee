'use strict'

app = angular.module('app', ['ngResource'])
  .constant('apiUrl', 'http://localhost:9000\:9000/api/v1')
  .config(['$routeProvider', ($routeProvider) ->
    $routeProvider
      .when('/', {
      templateUrl: '/views/index',
      controller: 'IndexCtrl'
      })
      .otherwise({
      redirectTo: '/'
      })
  ])
  .config(['$locationProvider', ($locationProvider) ->
    $locationProvider.html5Mode(true)
  ])

app.controller('AppCtrl', ['$scope', '$routeParams', ($scope, $routeParams) ->
])

app.controller('IndexCtrl', ['$scope', '$routeParams', ($scope, $routeParams) ->
])


class Entity

class Service