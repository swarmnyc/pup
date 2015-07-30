(function (angular) {
    "use strict";
    var app = angular.module("userApp", ["ngRoute", "angularMoment"]);

    app.config(function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/:userName', {
                templateUrl: 'list.html',
                controller: 'ListController'
            })
            .when('/:userName/:lobbyId', {
                templateUrl: 'detail.html',
                controller: 'DetailController'
            });

        $locationProvider.html5Mode(true);
    });
    app.controller("MainController", function ($scope, $rootScope, $location, $routeParams) {
        $rootScope.root = $location.protocol() + "://" + $location.host() + ":" + $location.port();

        $rootScope.$on('$routeChangeStart', function (event, next, current) {
            current = (current || {});
            next = (next || {});
            var currentPath = current.originalPath;
            var nextPath = next.originalPath;

            console.log('Starting to leave %s to go to %s', currentPath, nextPath);
        });
    });
    app.controller("ListController", function ($scope, $rootScope, $http, $location, $routeParams) {
        $rootScope.userName = $routeParams.userName;

        $http.get("/api/Lobby?userId=" + $rootScope.userId).success(function (response) {
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
            var i;
            for (i = 0; i < result.length; i++) {
                var lobby = result[i];
                lobby.startTime = new Date(lobby.startTimeUtc);
                lobby.pictureUrl = lobby.pictureUrl.replace("~", $rootScope.root);
                lobby.thumbnailPictureUrl = lobby.thumbnailPictureUrl.replace("~", $rootScope.root);
                if (lobby.platform === "PC") {
                    lobby.platform = "PC/STEAM";
                }

                if (lobby.startTime >= nextWeek) {
                    s[3].push(lobby);
                } else if (lobby.startTime >= thisWeek) {
                    s[2].push(lobby);
                } else if (lobby.startTime >= tomorrow) {
                    s[1].push(lobby);
                } else {
                    s[0].push(lobby);
                }
            }

            $scope.sections = [];
            for (i = 0; i < s.length; i++) {
                if (s[i].length !== 0) {
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
        });

        $scope.goToDetail = function (lobby) {
            $rootScope.lobby = lobby;
            $location.path("/" + $routeParams.userName + "/" + lobby.id);
        };
    });

    app.controller("DetailController", function ($scope, $rootScope, $http, $routeParams, $location) {
        if (!$rootScope.lobby) {
            $location.path("/" + $routeParams.userName);
            return;
        }

        var lobby = $rootScope.lobby;
        $scope.lobby = lobby;

        for (var i = 0; i < lobby.users.length; i++) {
            if (lobby.users[i].portraitUrl) {
                lobby.users[i].portraitUrl = lobby.users[i].portraitUrl.replace("~", $rootScope.root);
            }

            if (lobby.users[i].isOwner) {
                lobby.owner = lobby.users[i];
            }
        }

        $http.get("/api/Lobby/Message/" + $routeParams.lobbyId).success(function (messages) {
            var precious = {};
            $scope.messages = [];
            for (var i = 0; i < messages.length; i++) {
                var msg = messages[i];

                if (msg.userId) {
                    msg.isSysMessage = false;
                    if (precious.userId === msg.userId) {
                        precious.message += "\n" + msg.message;
                    } else {
                        for (var j = 0; j < lobby.users.length; j++) {
                            if (lobby.users[j].id === msg.userId) {
                                msg.portraitUrl = lobby.users[j].portraitUrl;
                            }
                        }

                        precious = msg;
                        $scope.messages.splice(0, 0, msg);
                    }
                } else {
                    msg.isSysMessage = true;
                    $scope.messages.splice(0, 0, msg);
                }
            }
        });
    });
}(window.angular));