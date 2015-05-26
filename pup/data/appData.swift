//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

struct appColors {
    var tealMain = "#49c1c3"
    var tealDark = "#2b9a9a"
    var orange = "#f16a22"
    var mainGrey = "#4d4d4d"
    var X360 = "#8CBC34"
    var XONE = "#009500"
    var PS4 = "#04a4ef"
    var PS3 = "#9391b4"
    var PC = "#1e1e1e"
}


struct appURLS {
    var apiBase = "http://pup-secondary.azurewebsites.net/api/"
    var lobbies = "http://pup-secondary.azurewebsites.net/api/lobby/"
    var games = "http://pup-secondary.azurewebsites.net/api/games/"


}


struct UIValues {
    var dividerWidth = 0.5
    var horizontalPadding = 13.0
    var halfHorizontalPadding: Double {
        get {
            return self.horizontalPadding/2

        }
    }
    var verticalPadding = 11.0
}

