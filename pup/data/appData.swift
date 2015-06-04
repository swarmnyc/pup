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
    var lightGray = "#f1f1f1"
}


struct miscData {
    var platforms: Array<String> = ["Steam or PC", "XBOX 360", "XBOX ONE", "PS3", "PS4"]
    var platformDict = ["Steam or PC": "Steam", "XBOX 360": "Xbox360", "XBOX ONE": "XboxOne", "PS3": "PS3", "PS4": "PS4"];

}


struct appURLS {
    var apiBase = "http://pup-secondary.azurewebsites.net/api/"
    var lobbies: String {
        get {
          return  "\(apiBase)lobby/"
        }
    }

    var joinLobby: String {
        get {
          return  "\(apiBase)lobby/join/"
        }
    }

    var leaveLobby: String {
        get {
          return  "\(apiBase)lobby/leave/"
        }
    }

    var games: String {
        get {
          return  "\(apiBase)game/"
        }
    }


    var login: String {
        get {
          return  "\(apiBase)login/"
        }
    }


    var register: String {
        get {
            return "\(apiBase)register"
        }
    }



}


struct UIValues {
    var dividerWidth = 0.5
    var horizontalPadding = 11.0
    var halfHorizontalPadding: Double {
        get {
            return self.horizontalPadding/2

        }
    }
    var halfVerticalPadding: Double {
        get {
            return self.verticalPadding/2
        }
    }
    var verticalPadding = 11.0
    var buttonHeight = 75.0;
    var justBelowSearchBar = 75.0;
    var lobbyImageHeight = 250.0;
    var halfLobbyImage: Double {
        get {
            return self.lobbyImageHeight/2
        }
    }
}

