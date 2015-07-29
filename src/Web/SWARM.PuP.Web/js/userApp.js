var app = angular.module("userApp", ["ngRoute","ngResource",""]);

app.factory("data",function(){
    return {};
});
 
app.config(function($routeProvider, $locationProvider, $location, data) {
  data.root = "http://" + $location.host();
  
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
  
  $locationProvider.html5Mode(true);    
});

app.controller("ListController", function ($scope, $http, $location, $routeParams, data) {

   $scope.userName = $routeParams.userName;
   
   $http.get("/api/Lobby?userName=" + $scope.userName).success(function(response) {
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
               lobby.startTime = new Date(lobby.startTimeUtc);
               lobby.pictureUrl = lobby.pictureUrl.replace("~", $scope.data);
               lobby.thumbnailPictureUrl = lobby.thumbnailPictureUrl.replace("~", $scope.data);
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
   
   $scope.goToDetail = function(lobby) {
       data.lobby = lobby;
       $location.path("/User/" + $routeParams.userName + "/" + lobby.Id);
   };
});

app.controller("DetailController", function ($scope, $http, $routeParams, $location, data) {
    if (!data.start) {
        $location.path("/User/" + $routeParams.userName);
    }
    var lobby = data.lobby;
    $scope.lobby = lobby;
    
    for (var i = 0; i < data.lobby.users.length; i++) {
        if (data.lobby.users[i].isOwner) {
            $scope.lobby.owner = data.lobby.users[i];        
        }        
    }
    
    $http.get("/api/Lobby/Message/" + $routeParams.lobbyId).success(function(messages) {
        for (var i = 0; i < messages.length; i++) {
            var msg = messages[i];
            
            if (lobby.users[j].userId) {
                msg.isSysMessage = false;
                for (var j = 0; j < lobby.users.length; j++) {
                    if (lobby.users[j].userId === msg.userId) {                       
                        msg.user = lobby.users[j];
                    }                                
                }
            } else{
                msg.isSysMessage = true;
            }
        }
        
        $scope.messages = messages;        
    });
    
    $scope.back = function(){
        $location.path("/User/" + $routeParams.userName);
    };    
});

app.controller("ErrorController", function ($scope, $http, $routeParams, $location) {
    $scope.back = function(){
        $location.path("/User/" + $routeParams.userName);
    };    
});