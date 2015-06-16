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


    }

    init() {

        var isBreakdown = true
        var breakdownTitle = "Happening Soon (2)"
    }

    func addMessages(messages: NSArray) {
        self.messages = [];

        for (var i = 0; i<messages.count; i++) {
            self.messages.append(Message(messages: messages[i], users: self.users, owner: self.owner));
        }


    }

}


class Message {
    var username = ""
    var message = ""
    var picture = ""


    init(messages: AnyObject, users: Array<SingleLobbyUser>, owner: SingleLobbyUser) {
        //println(messages.customParameters)
        println("------")
        if (messages.customParameters?["userName"] != nil) {
            self.username = messages.customParameters?["userName"] as! String;
        } else {
            self.username = "system message"
        }
        self.message = messages.valueForKey("text") as! String;

        for (var i = 0; i<users.count; i++) {
            if (self.username == users[i].name) {
                if (users[i].portraitUrl != "") {
                    self.picture = users[i].portraitUrl;
                }
            }
        }

        if (self.username == owner.name) {
            self.picture = owner.portraitUrl;
        }

        println(self.message)
        println(self.username);

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

        println(games)
        updated = true;


        for (var i = 0; i<data.count; i++) {

            games.append(LobbyData(data: data[i] as! NSDictionary))

        }




    }


    func sortByTime() {


    }


}