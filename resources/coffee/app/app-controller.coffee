app.controller("AppCtrl", ["$scope", "$routeParams", ($scope, $routeParams) ->
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
    console.log $scope.registerForm
])
