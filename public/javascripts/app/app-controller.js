
app.controller("AppCtrl", [
  "$scope", "$routeParams", "$http", function($scope, $routeParams, $http) {
    $scope.signinForm = {
      username: "",
      password: ""
    };
    $scope.registerForm = {
      username: "",
      password: "",
      email: ""
    };
    $scope.signin = function() {
      console.log("SIGN IN");
      return console.log($scope.signinForm);
    };
    return $scope.register = function() {
      console.log("REGISTER");
      $http.post("/api/v1/users", $scope.registerForm);
      return console.log($scope.registerForm);
    };
  }
]);
