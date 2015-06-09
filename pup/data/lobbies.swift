//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire


class LobbyData {

    var gameId:String = ""
    var id:String = "232"
    var name:String = ""
    var pictureUrl:String = ""
    var thumbnailPictureUrl:String = ""
    var description:String = ""
    var platform:String = ""
    var playStyle:String = ""
    var skillLevel:String = ""
    var startTimeUtc:String = ""
    var isBreakdown:Bool = false;
    var breakdownTitle:String = ""
    var users: Array<SingleLobbyUser> = []
    var owner = SingleLobbyUser()

    var getTagText: String {
        get {
            return "\(playStyle), \(skillLevel)"
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


    init(data: NSDictionary) {
        println(data)

        gameId = data["gameId"] as! String
        id = data["id"] as! String
        name = data["name"] as! String
        pictureUrl = data["pictureUrl"] as! String
        thumbnailPictureUrl = data["thumbnailPictureUrl"] as! String
        description = data["description"] as! String
        description = data["description"] as! String
        platform = data["platform"] as! String
        platform = data["platform"] as! String
        playStyle = data["playStyle"] as! String
        skillLevel = data["skillLevel"] as! String
        startTimeUtc = data["startTimeUtc"] as! String



    }

    init() {

        var isBreakdown = true
        var breakdownTitle = "Happening Soon (2)"
    }

}


class SingleLobbyUser {
    var isLeave = false
    var id = "-1"
    var isOwner = false
    var name = ""

    init() {

    }

    init(data: NSDictionary) {

        println(data)

        isLeave = data["isLeave"] as! Bool
        isOwner = data["isOwner"] as! Bool
        id = data["id"] as! String
        name = data["userName"] as! String

    }

}



class LobbyList {  //collection of all the current games
    var games: Array<LobbyData>;

    var updated: Bool = false;
    var parent: LobbyListController;

    init(parentView: LobbyListController) {

        parent = parentView;

        games = Array<LobbyData>();



    }

    func makeNewRequest(search: String, platforms: Array<String>) -> String {
        var suffix = "?search=\(search)";
        if (platforms.count > 0) {
            for (var i = 0; i < platforms.count; i++) {
                suffix += "&platforms=\(platforms[i])";
            }
        }

        println(suffix);
        return suffix;
        //getLobbies(searchTerms: suffix);
    }

    func getLobbies(search: String, platforms: Array<String>, applyChange: Bool = true, success: () -> Void, failure: () -> Void) {
        var searchTerms = makeNewRequest(search, platforms: platforms);

        var urlAppend = "";
        if searchTerms != "" {
            urlAppend = "lobby\(searchTerms)";
        } else {
            urlAppend = "lobby"
        }
        var urlAppendEncoded = urlAppend.stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLHostAllowedCharacterSet())
        var url = "\(urls.apiBase)\(urlAppendEncoded!)"
        println(url)
        let requestUrl = NSURL(string: url)
        if (requestUrl != nil) {




            Alamofire.request(.GET, url).responseJSON { (request, response, responseJSON, error) in
                var resp: NSArray = responseJSON! as! NSArray

                println(resp);

                self.updateData(resp);
                success();
                self.updated = false;
            };




        }


    }


    func updateData(data: NSArray) {  //update data and call a redraw in the UI
        println(data)
        games.removeAll();

        println(games)
        updated = true;


        for (var i = 0; i<data.count; i++) {

            games.append(LobbyData(data: data[i] as! NSDictionary))

        }




    }


    func sortByTime() {


    }


}