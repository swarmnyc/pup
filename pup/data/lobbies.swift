//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

struct lobbyData {
    var GameId = "5553c9f460635b5368e5a1d8"
    var id = "391837284737198374ech1"
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
    var users: Array<singleLobbyUser> = []
    var owner = singleLobbyUser()
    var getTagText: String {
        get {
            return "\(PlayStyle), \(SkillLevel)"
        }
    }
    var timeInHuman: String {
        get {
            let date = NSDate(fromString: self.startTimeUtc, format: .ISO8601)
            var today = date.isToday()
            var tomorrow = date.isTomorrow()
            var hoursUntilEvent = date.hoursAfterDate(NSDate())

            var timeWithAMPM = date.toString(dateStyle: .NoStyle, timeStyle: .ShortStyle, doesRelativeDateFormatting: true)

            if (hoursUntilEvent < -1) {
                return "Finished"
            } else if (today) {
                return date.toString(dateStyle: .ShortStyle, timeStyle: .ShortStyle, doesRelativeDateFormatting: true)
            } else if (tomorrow) {
                return "Tomorrow \(date.monthToString()) \(date.day()) \(timeWithAMPM)"

            } else {
                return "\(date.monthToString()) \(date.day()), \(timeWithAMPM)"
            }


        }
    }

}


struct singleLobbyUser {
    var isLeave = false
    var id = "-1"
    var isOwner = false
    var name = ""
}



class lobbyList {  //collection of all the current games
    var games: Array<lobbyData>;

    var updated: Bool = false;
    var parent: LobbyListController;

    init(parentView: LobbyListController) {

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

        println("\(urls.apiBase)\(urlAppend)")
        let requestUrl = NSURL(string: "\(urls.apiBase)\(urlAppend)")

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
            var id: String = subJson["id"].stringValue
            var Name: String = subJson["name"].stringValue
            var PictureUrl: String = subJson["pictureUrl"].stringValue
            var ThumbnailPictureUrl: String = subJson["thumbnailPictureUrl"].stringValue
            var Description: String = subJson["description"].stringValue
            var Platform: String = subJson["platform"].stringValue
            var PlayStyle: String = subJson["playStyle"].stringValue
            var SkillLevel: String = subJson["skillLevel"].stringValue
            var startTimeUtc: String = subJson["startTimeUtc"].stringValue

            games.append(lobbyData(GameId: GameId, id: id, Name: Name, PictureUrl: PictureUrl, ThumbnailPictureUrl: ThumbnailPictureUrl, Description: Description, Platform: Platform, PlayStyle: PlayStyle, SkillLevel: SkillLevel, startTimeUtc: startTimeUtc, isBreakdown: false, breakdownTitle: "", users: [], owner: singleLobbyUser()))


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