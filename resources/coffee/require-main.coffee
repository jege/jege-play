require.config(
  paths:
    jquery: "vendors/jquery/jquery.min"
    angular: "http://ajax.googleapis.com/ajax/libs/angularjs/1.1.4/angular.min"

  shim:
    "main":
      deps: ["jquery", "angular"]
      export: "main"
    "app/app":
      deps: ["main"]
    "app/index":
      deps: ["main"]
)

require(["jquery", "angular", "main", ($, angular, main) ->
  console.log "Require.js"
  console.log "Running jQuery %s", $().jquery
])
