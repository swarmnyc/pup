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
    var midGray = "#b3b3b3"
    
}


struct miscData {
    var platforms: Array<String> = ["Steam or PC", "XBOX 360", "XBOX ONE", "PS3", "PS4"]
    var platformDict = ["Steam or PC": "pc", "XBOX 360": "Xbox360", "XBOX ONE": "XboxOne", "PS3": "PS3", "PS4": "PS4"];
  
    
    var QBDefaultUser = "553be08dcbf5a81a70567b13";
    var QBDefaultPassword = "swarmnyc"
    var QBAppId = "24285"
}


struct appURLS {
    var siteBase = "http://pup-dev.azurewebsites.net/"
    var apiBase: String {
        get {
            return "\(siteBase)api/"
        }
    }
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

    var OAuth: String {
        get {
            return "\(siteBase)OAuth/"
        }
    }

    var Tumblr: String {
        get {
            return "\(OAuth)Tumblr"
        }
    }

    var Twitter: String {
        get {
            return "\(OAuth)Twitter"
        }
    }

    var Reddit: String {
        get {
            return "\(OAuth)Reddit"
        }
    }

    var User: String {
        get {
            return "\(apiBase)user/"
        }
    }

    var updatePortrait: String {
        get {
            return "\(User)updateportrait"
        }
    }

    var Facebook: String {
        get {
            return "\(User)medium"
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

    var descriptionPlaceholder = "Let people know what you're looking for, the region (or dungeon) name of the particular game you want to get through, your play style, and whether this is a PVP or friendly server, etc."
    var usernamePlaceholder = "Unique username"
    var emailPlaceholder = "cats@cats.com"
    var messageBaseSize = 50.0;
    func getMessageAddition(message: String) -> CGFloat {
       return CGFloat(Int(count(message) / 26) * 12)
    }
}

