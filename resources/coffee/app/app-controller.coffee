app.controller("AppCtrl", ["$scope", "$routeParams", "$http", ($scope, $routeParams, $http) ->
  $scope.signinForm =
    username: ""
    password: ""

  $scope.registerForm =
    username: ""
    password: ""
    email: ""

  $scope.signin = () ->
    console.log "SIGN IN"
    console.log $scope.signinForm

  $scope.register = () ->
    console.log "REGISTER"
    $http.post("/api/v1/users", $scope.registerForm)
    console.log $scope.registerForm
])
