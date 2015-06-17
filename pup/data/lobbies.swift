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
    var tags = []
    var QBChatRoomId: String = ""
    var messages: Array<Message> = [];
    var proPicDict: Dictionary<String, String> = ["": ""];
    var isMember = false;
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
        if (data["description"] != nil) {
            description = data["description"] as! String
        }
        platform = data["platform"] as! String
        platform = data["platform"] as! String
        playStyle = data["playStyle"] as! String
        skillLevel = data["skillLevel"] as! String
        startTimeUtc = data["startTimeUtc"] as! String
        tags = data["tags"] as! Array<Dictionary<String, String>>
        QBChatRoomId = tags[0]["value"] as! String
        println(tags[0])
        println(QBChatRoomId)

        addOwnerAndUsersToData(data);


    }

    func addOwnerAndUsersToData(detailed: NSDictionary) {
        var user = detailed["users"] as! NSArray

        for (var i = 0; i<user.count; i++) {
            var userName = user[i]["userName"] as! String

            //is the current user in this lobby?
            if userName == currentUser.data.name {
                self.isMember = true;
                println("IS A MEMBER!!!!")
            }

            //add profile pictures in dictionary for easy access
            proPicDict[userName] = ""
            if (user[i].objectForKey("portraitUrl") != nil) {
                proPicDict[userName] = user[i]["portraitUrl"] as! String
            }

            //add users and owners to data
            if (user[i]["isOwner"] as! Bool) {
                owner = SingleLobbyUser(data: user[i] as! NSDictionary)
            } else {
                users.append(SingleLobbyUser(data: user[i] as! NSDictionary));
            }
        }


    }

    init() {

        var isBreakdown = true
        var breakdownTitle = "Happening Soon (2)"
    }

    func addMessages(messages: NSArray) {
        self.messages = [];

        for (var i = 0; i<messages.count; i++) {
            self.messages.append(Message(messages: messages[i], propics: proPicDict));
        }


    }

}


class Message {
    var username = ""
    var message = ""
    var picture = ""


    init(messages: AnyObject, propics: Dictionary<String,String>) {
        //println(messages.customParameters)
        println("------")
        if (messages.customParameters?["userName"] != nil) {
            self.username = messages.customParameters?["userName"] as! String;
            self.picture = propics[self.username] as! String!
        } else {
            self.username = "system message"
            self.picture = ""
        }
        self.message = messages.valueForKey("text") as! String;

        println(self.message)
        println(self.username);
        println(self.picture);
//        println(propics[self.username])
//        println(propics);

    }

    init(newMessage: String) {
        username = currentUser.data.name;
        picture = currentUser.data.picture;
        message = newMessage;

    }
}


class SingleLobbyUser {
    var isLeave = false
    var id = "-1"
    var isOwner = false
    var name = ""
    var portraitUrl = ""
    init() {

    }

    init(data: NSDictionary) {


        isLeave = data["isLeave"] as! Bool
        isOwner = data["isOwner"] as! Bool
        id = data["id"] as! String
        name = data["userName"] as! String
        if (data.objectForKey("portraitUrl") != nil) {
            portraitUrl = data["portraitUrl"] as! String
            println(portraitUrl);
        }
    }

}



class LobbyList {  //collection of all the current games
    var games: Array<LobbyData>;

    var gamesOrganized = ["Happening Now": Array<LobbyData>(), "Happening Soon": Array<LobbyData>(), "Tomorrow": Array<LobbyData>(), "Later This Week": Array<LobbyData>(), "Next Week": Array<LobbyData>()]
    var gamesKey = ["Happening Now", "Happening Soon", "Tomorrow", "Later This Week", "Next Week"]
    var gameCount = 0;

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

               // println(resp);

                self.updateData(resp);
                success();
                self.updated = false;
            };




        }


    }


    func updateData(data: NSArray) {  //update data and call a redraw in the UI
        println(data)
        games.removeAll();
        gamesOrganized[gamesKey[0]] = []
        gamesOrganized[gamesKey[1]] = []
        gamesOrganized[gamesKey[2]] = []
        gamesOrganized[gamesKey[3]] = []
        gamesOrganized[gamesKey[4]] = []
        gameCount = 0;
        println(games)
        updated = true;


        for (var i = 0; i<data.count; i++) {
            var lobby = data[i] as! NSDictionary;
            var startTimeUtc = lobby["startTimeUtc"] as! String
            let date = NSDate(fromString: startTimeUtc, format: .ISO8601)
            if (date.minutesAfterDate(NSDate()) <= 30) {
                gamesOrganized[gamesKey[0]]!.append(LobbyData(data: lobby))
                gameCount++;
            } else if (date.isToday()) {
                gamesOrganized[gamesKey[1]]!.append(LobbyData(data: lobby))
                gameCount++;
            } else if (date.isTomorrow()) {
                gamesOrganized[gamesKey[2]]!.append(LobbyData(data: lobby))
                gameCount++;
            } else if (date.isThisWeek()) {
                gamesOrganized[gamesKey[3]]!.append(LobbyData(data: lobby))
                gameCount++;
            } else {
                gamesOrganized[gamesKey[4]]!.append(LobbyData(data: lobby))
                gameCount++;
            }

            //games.append(LobbyData(data: lobby))

        }




    }


    func sortByTime() {


    }


}