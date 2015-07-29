var app = angular.module("userApp", ["ngRoute","ngResource"]);
app.config(function($routeProvider, $locationProvider) {
  $routeProvider
   .when('/User/:userName', {
    templateUrl: 'list.html',
    controller: 'ListController'    
  })
  .when('/User/:userName/:lobbyId', {
    templateUrl: 'detail.html',
    controller: 'DetailController'
  })
  .when('/Error', {
    templateUrl: 'error.html'    
  })
  .otherwise({
        redirectTo: '/Error'
  });    
});
app.controller("ListController", function ($scope, $resource, $routeParams) {
   $scope.userName = $routeParams.userName;
    
   var root = location.origin;                   
   root = "http://pup-dev.azurewebsites.net/"; //TODO: remove
   //var $api = $resource(root + "/api/Lobby?userId=@ViewData["UserId"]"); //TODO: remove
   var $api = $resource(root + "/api/Lobby?userName=" + $scope.userName);
   
   $api.get(function (response) {
       var result = response.result;

       var today = new Date();
       today = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0);

       var tomorrow = new Date(today);
       tomorrow.setDate(tomorrow.getDate() + 1);

       var thisWeek = new Date(today);
       thisWeek.setDate(tomorrow.getDate() + 2);

       var nextWeek = new Date(today);
       nextWeek.setDate(tomorrow.getDate() + 7);

       var s = [[], [], [], []];
       for (var i = 0; i < result.length; i++) {
           var lobby = result[i];
           lobby.startTimeUtc = new Date(lobby.startTimeUtc);
           lobby.pictureUrl = lobby.pictureUrl.replace("~", root);
           lobby.thumbnailPictureUrl = lobby.thumbnailPictureUrl.replace("~", root);
           if (lobby.platform === "PC") {
               lobby.platform = "PC/STEAM"
           }

           if (lobby.startTimeUtc >= nextWeek) {
               s[3].push(lobby);
           } else if (lobby.startTimeUtc >= thisWeek) {
               s[2].push(lobby)
           } else if (lobby.startTimeUtc >= tomorrow) {
               s[1].push(lobby)
           } else {
               s[0].push(lobby)
           }
       }

       $scope.sections = [];
       for (var i = 0; i < s.length; i++) {
           if (s[i].length != 0) {
               var name;
               switch (i) {
                   case 0:
                       name = "Today (" + s[i].length + ")";
                       break;
                   case 1:
                       name = "Tomorrow (" + s[i].length + ")";
                       break;
                   case 2:
                       name = "Later this week (" + s[i].length + ")";
                       break;
                   case 3:
                       name = "Next week (" + s[i].length + ")";
                       break;
               }

               $scope.sections.push({
                   name: name,
                   lobbies: s[i]
               });
           }
       }

       $scope.ready = true;
   });
});

app.controller("DetailController", function ($scope, $resource, $routeParams, $location) {
    $scope.back = function(){
        $location.path("/User/" + $routeParams.userName);
    };    
});