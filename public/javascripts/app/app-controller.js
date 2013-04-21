(function() {

  app.controller("AppCtrl", [
    "$scope", "$routeParams", "$http", function($scope, $routeParams, $http) {
      $scope.data = {
        test: "Test",
        number: 42
      };
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
        console.log("SIGN IN 3");
        return console.log($scope.signinForm);
      };
      return $scope.register = function() {
        console.log("REGISTER");
        $http.post("/api/v1/users", $scope.registerForm);
        return console.log($scope.registerForm);
      };
    }
  ]);

}).call(this);
