
app.controller("AppCtrl", [
  "$scope", "$routeParams", function($scope, $routeParams) {
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
      return console.log($scope.registerForm);
    };
  }
]);
