//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


struct gameData {
    var State = "activated"
    var Tags = ""
    var Name = "Battlefield 4"
    var ThumbnailPictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var PictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var Description = "Battlefield 4 is a 2013 first-person shooter video game developed by Swedish video game developer EA Digital Illusions CE (DICE) and published by Electronic Arts. It is a sequel to 2011's Battlefield 3 and was released on October 29, 2013 in North America, October 31, 2013 in Australia, November 1, 2013 in Europe and New Zealand and November 7, 2013 in Japan for Microsoft Windows, PlayStation 3, PlayStation 4, Xbox 360 and Xbox One."
    var ReleaseDateUtc = ""
    var GameTypes = ""
    var Rank = 0
    var Platforms = ["PS4", "PS3","XBOX360", "XBOXONE"];


}


struct lobbyData {
    var GameId = "5553c9f460635b5368e5a1d8"
    var Name = "Destiny"
    var PictureUrl = "http://upload.wikimedia.org/wikipedia/en/thumb/b/be/Destiny_box_art.png/220px-Destiny_box_art.png"
    var ThumbnailPictureUrl = "http://upload.wikimedia.org/wikipedia/en/thumb/b/be/Destiny_box_art.png/220px-Destiny_box_art.png"
    var Description = "The combat in this game was the one time in my life I ever became legitimately engraged playing a videogame. Need help!"
    var Platform = "PS3"
    var PlayStyle = "Normal"
    var SkillLevel = "Intermediate"
    var startTimeUtc = "2015-05-13T22:24:41.000Z"
    var isBreakdown = false
    var breakdownTitle = "Happening Soon (2)"

    var timeInHuman: String {
        get {
            let date = NSDate(fromString: self.startTimeUtc, format: .ISO8601)
            return date.toString(dateStyle: .ShortStyle, timeStyle: .ShortStyle, doesRelativeDateFormatting: true)

        }
    }

}





struct appColors {
    var tealMain = "#49c1c3"
    var tealDark = "#2b9a9a"
    var orange = "#f16a22"
    var mainGrey = "#4d4d4d"
    var X360 = "#4d4d4d"
    var XONE = "#009500"
    var PS4 = "#04a4ef"
    var PS3 = "#9391b4"
    var PC = "#1e1e1e"
}


struct appURLS {
    var api = "http://pup.azurewebsites.net/api/"


}


struct userData {
    var id = ""
    var isOwner = ""
    var isLeave = ""
    var name = ""


}

struct currentUser {
    var loggedIn = false;


}


class singleLobby {
    var simpleData = lobbyData();
    var users: Array<userData>



}


class lobbyList {  //collection of all the current games
    var games: Array<lobbyData>;

    var updated: Bool = false;
    var parent: lobbyListController;

    init(parentView: lobbyListController) {

        parent = parentView;

        games = Array<lobbyData>();
//        for i in 0...15 {
//
//            games.append(lobbyData());
//            if (i==0 || i==4 || i==10) {
//                games[i].isBreakdown = true;
//                if (i==4) {
//                    games[i].breakdownTitle = "Tomorrow (5)"
//                } else if (i==10) {
//                    games[i].breakdownTitle = "Later This Week (50)"
//                }
//
//            }
//
//        }


        getLobbies();




    }



    func getLobbies(searchTerms: String = "", applyChange: Bool = true) {

        var urlAppend = "";
        if searchTerms != "" {
            urlAppend = "lobby?search=\(searchTerms)";
        } else {
            urlAppend = "lobby"
        }

        println("\(urls.api)\(urlAppend)")
        let requestUrl = NSURL(string: "\(urls.api)\(urlAppend)")

        let task = NSURLSession.sharedSession().dataTaskWithURL(requestUrl!) {(data, response, error) in
            println(error)
            let jsonResponse = JSON(data: data)
            if (applyChange) {
                self.updateData(jsonResponse);
            }
        }

        task.resume();


    }


    func updateData(data: JSON) {  //update data and call a redraw in the UI
        println(data)
        games.removeAll();
        for (index: String, subJson: JSON) in data {
            println(subJson["pictureUrl"].stringValue)

            var GameId: String = subJson["gameId"].stringValue;
            var Name: String = subJson["name"].stringValue
            var PictureUrl: String = subJson["pictureUrl"].stringValue
            var ThumbnailPictureUrl: String = subJson["thumbnailPictureUrl"].stringValue
            var Description: String = subJson["description"].stringValue
            var Platform: String = subJson["platform"].stringValue
            var PlayStyle: String = subJson["playStyle"].stringValue
            var SkillLevel: String = subJson["skillLevel"].stringValue
            var startTimeUtc: String = subJson["startTimeUtc"].stringValue

            games.append(lobbyData(GameId: GameId, Name: Name, PictureUrl: PictureUrl, ThumbnailPictureUrl: ThumbnailPictureUrl, Description: Description, Platform: Platform, PlayStyle: PlayStyle, SkillLevel: SkillLevel, startTimeUtc: startTimeUtc, isBreakdown: false, breakdownTitle: ""))


//            var GameId = "5553c9f460635b5368e5a1d8"
//            var Name = "Destiny"
//            var PictureUrl = "http://upload.wikimedia.org/wikipedia/en/thumb/b/be/Destiny_box_art.png/220px-Destiny_box_art.png"
//            var ThumbnailPictureUrl = "http://upload.wikimedia.org/wikipedia/en/thumb/b/be/Destiny_box_art.png/220px-Destiny_box_art.png"
//            var Description = "The combat in this game was the one time in my life I ever became legitimately engraged playing a videogame. Need help!"
//            var Platform = "PS3"
//            var PlayStyle = "Normal"
//            var SkillLevel = "Intermediate"
//            var startTimeUtc = "2015-05-13T22:24:41.000Z"
//            var isBreakdown = false
//            var breakdownTitle = "Happening Soon (2)"


            println("----")
        }

        println(games)
        updated = true;
        
        dispatch_async(dispatch_get_main_queue(),{
             self.parent.table.reloadData()
            
        })
       

    }


    func sortByTime() {


    }


}