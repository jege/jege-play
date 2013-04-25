app.controller("AppCtrl", ["$scope", "$routeParams", "$http", ($scope, $routeParams, $http) ->
  $scope.data =
    test: "Salut tout le monde"
    number: 42

  $scope.signinForm =
    username: ""
    password: ""

  $scope.registerForm =
    username: ""
    password: ""
    email: ""

  $scope.signin = () ->
    console.log "SIGN IN 3"
    console.log $scope.signinForm

  $scope.register = () ->
    console.log "REGISTER"
    $http.post("/api/v1/users", $scope.registerForm)
    console.log $scope.registerForm
])
