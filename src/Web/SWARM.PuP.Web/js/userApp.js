(function (angular) {
    "use strict";
    var app = angular.module("userApp", ["ngRoute", "angularMoment"]);

    app.constant("consts", {});

    app.constant('angularMomentConfig', {});

    app.config(function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/:userName', {
                templateUrl: 'list.html',
                controller: 'ListController'
            })
            .when(':userName/:lobbyId', {
                templateUrl: 'detail.html',
                controller: 'DetailController'
            });

        $locationProvider.html5Mode(true);
    });
    app.controller("MainController", function ($scope, $location , $routeParams, consts) {
        consts.root = "http://" + $location.host();
    });
    app.controller("ListController", function ($scope, $http, $location, $routeParams, consts) {
        $scope.userName = $routeParams.userName;
        $http.get("/api/Lobby?userName=" + $routeParams.userName).success(function (response) {
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
                lobby.pictureUrl = lobby.pictureUrl.replace("~", consts.root);
                lobby.thumbnailPictureUrl = lobby.thumbnailPictureUrl.replace("~", consts.root);
                if (lobby.platform === "PC") {
                    lobby.platform = "PC/STEAM";
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

        $scope.goToDetail = function (lobby) {
            data.lobby = lobby;
            $location.path("/User/" + $routeParams.userName + "/" + lobby.Id);
        };
    });

    
    app.controller("DetailController", function ($scope, $http, $routeParams, $location, consts) {
        if (!consts.lobby) {
            $location.path("/" + $routeParams.userName);
        }
        var lobby = data.lobby;
        $scope.lobby = lobby;

        for (var i = 0; i < data.lobby.users.length; i++) {
            if (data.lobby.users[i].isOwner) {
                $scope.lobby.owner = data.lobby.users[i];
            }
        }

        $http.get("/api/Lobby/Message/" + $routeParams.lobbyId).success(function (messages) {
            for (var i = 0; i < messages.length; i++) {
                var msg = messages[i];

                if (lobby.users[j].userId) {
                    msg.isSysMessage = false;
                    for (var j = 0; j < lobby.users.length; j++) {
                        if (lobby.users[j].userId === msg.userId) {
                            msg.user = lobby.users[j];
                        }
                    }
                } else {
                    msg.isSysMessage = true;
                }
            }

            $scope.messages = messages;
        });

        $scope.back = function () {
            $location.path("/User/" + $routeParams.userName);
        };
    });

    app.controller("ErrorController", function ($scope, $http, $routeParams, $location) {
        $scope.back = function () {
            $location.path("/User/" + $routeParams.userName);
        };
    });
}(window.angular));