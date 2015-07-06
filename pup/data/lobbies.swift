//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire


class LobbyData: QuickBloxDelegate {

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
    var unreadMessageCount = 0;
    var updatedAtUtc = "";
    var lastMessageAt = "";
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


    var recievedMessages = false;
    var setMessagesRecieved: (() -> Void)?
    var clearTheText: (() -> Void)?
    var reloadData: (() -> Void)?
    var checkUnreadCounter: ((Int) -> Void)?


    init(data: NSDictionary) {
      // println(data)

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

        if (data.objectForKey("unreadMessageCount") != nil) {
            unreadMessageCount = data["unreadMessageCount"] as! Int
        }

        lastMessageAt = data["lastMessageAt"] as! String;
        updatedAtUtc = data["updatedAtUtc"] as! String;
        addOwnerAndUsersToData(data);


        if (self.isMember) {
            println("adding delegate")
            myChatsListener.addDelegate(self as! QuickBloxDelegate)
            myChatsListener.addRoom(self.QBChatRoomId);

        }


        loadInitialMessages();


    }

    func addSelfAsMember() {
        self.isMember = true;
        myChatsListener.addDelegate(self as! QuickBloxDelegate)
        myChatsListener.addRoom(self.QBChatRoomId);
    }



    func addOwnerAndUsersToData(detailed: NSDictionary) {
        var user = detailed["users"] as! NSArray

        for (var i = 0; i<user.count; i++) {
            var userName = user[i]["userName"] as! String

            //is the current user in this lobby?
            if userName == currentUser.data.name {
                self.isMember = true;
            }

            //add profile pictures in dictionary for easy access
            proPicDict[userName] = ""
            if (user[i].objectForKey("portraitUrl") != nil) {
                proPicDict[userName] = user[i]["portraitUrl"] as! String
            }
                proPicDict[currentUser.data.name] = currentUser.data.picture;

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

            self.messages.append(Message(messages: messages[i], propics: proPicDict, users: users, owner: owner));
        }


    }


    func loadInitialMessages() {
        var url = urls.lobbies + "/message/" + self.id;

        sendRequest(url, clearData: true, success: {
            println(self.name)
            println("setting recieved messages")
            self.setMessagesRecieved?();
            self.recievedMessages = true;
        }, failure: {

        })


    }


    func sendRequest(url: String, clearData: Bool, success: () -> Void, failure: () -> Void) {

            Alamofire.request(.GET, url).responseJSON { (request, response, responseJSON, error) in
                var resp: NSArray = responseJSON! as! NSArray
                // println(resp.count);
                if (self.messages.count == 0) {
                    for (var i = 0; i < resp.count; i++) {
                        var message = resp[i] as! NSDictionary
                        var userId = message["userId"] as? String;
                        if (userId == nil) {
                            userId = "";
                        }

                        var text = message["message"] as! String;
                        var timeCreated = message["created_at"] as! String;
//
                        self.messages.append(Message(userId: userId!, message: text, timeSent: timeCreated, propics: self.proPicDict, users: self.users, owner: self.owner))
                    }
                }
                success();
            };

    }



    func addMessageAtStart(message: AnyObject) {

            self.messages.insert(Message(messages: message, propics: proPicDict, users: users, owner: owner), atIndex: 0);
            reloadData?();

    }

    func setAllRead() {
        checkUnreadCounter?(Int(Int(-1) * Int(self.unreadMessageCount)));
        self.unreadMessageCount = 0;
    }

    func addSingleMessage(message: QBChatMessage) -> Message {
        self.messages.append(Message(messages: message, propics: proPicDict, users: users, owner: owner));
        self.unreadMessageCount++;
        checkUnreadCounter?(1);
        reloadData?();

        return self.messages[self.messages.count-1];
    }

    func sendMessage(message: String) {

        println(message);
        myChatsListener.quickBloxConnect?.sendMessage(message, roomid: QBChatRoomId);
    }


    func handOffMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage) {
        self.recievedMessages = true;

        setMessagesRecieved?();
        self.addMessages(messages);
//        println(self.name)
//        println(messages);

    }



    func addNewMessage(message: QBChatMessage) {
//
//        println("adding new message")
        var lastMessage = self.addSingleMessage(message);
        println(lastMessage)
    }

    func clearText() {
//        println("clear text");
//        println("message just sent")
        println(clearTheText);
        clearTheText?();
    }

    func handOffChats() {
        println("hand off chats")
    }


}




class Message {
    var username = ""
    var message = ""
    var picture = ""
    var timesent = ""



    init(userId: String, message: String, timeSent: String, propics: Dictionary<String,String>, users: Array<SingleLobbyUser>, owner: SingleLobbyUser) {

        if (userId != "") {
            self.username = getUserName(userId, users: users, owner: owner);
            self.picture = propics[self.username] as! String!;
        } else {
            self.username = "system message"
            self.picture = "";
        }
        self.message = message;
        self.timesent = timeSent.stringByReplacingOccurrencesOfString(" ", withString: "T", options: NSStringCompareOptions.LiteralSearch, range: nil) + "Z";
    }

    init(messages: AnyObject, propics: Dictionary<String,String>, users: Array<SingleLobbyUser>, owner: SingleLobbyUser) {

        if ((messages.customParameters! != nil) && (messages.customParameters?["userId"] != nil)) {
            var id = messages.customParameters?["userId"] as! String;
            self.username = getUserName(id, users: users, owner: owner);
            self.picture = propics[self.username] as! String!
        } else {
            self.username = "system message"
            self.picture = ""
        }


        var datetime = messages.valueForKey("datetime") as! NSDate;

        var dateFormatter = NSDateFormatter();
        var timeZone = NSTimeZone(name: "UTC")
        dateFormatter.timeZone = timeZone;
        dateFormatter.dateFormat = "yyy-MM-dd HH:mm:ss";
        var dateString = dateFormatter.stringFromDate(datetime) as! String


        self.timesent = dateString.stringByReplacingOccurrencesOfString(" ", withString: "T", options: NSStringCompareOptions.LiteralSearch, range: nil) + "Z";
        self.message = messages.valueForKey("text") as! String;



    }

    init(newMessage: String) {
        username = currentUser.data.name;
        picture = currentUser.data.picture;
        message = newMessage;

    }

    func getUserName(id: String, users: Array<SingleLobbyUser>, owner: SingleLobbyUser) -> String {

        for (var i = 0; i<users.count; i++) {
            if (users[i].id == id) {
                return users[i].name;
            }
        }

        if (owner.id == id) {
            return owner.name;
        }

        return "";
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
    var pageIndex = 0;
    var loadMore = true;
    var lastSearchSuffix: String = ""

    init(parentView: LobbyListController) {

        parent = parentView;

        games = Array<LobbyData>();



    }


    func refreshRequest(success: (() -> Void), failure: (() -> Void)) {
        pageIndex = 0;
        loadMore = true;


        var suffix = self.lastSearchSuffix + "&PageIndex=" + String(self.pageIndex)
        var urlAppend = "lobby\(suffix)";
        var urlAppendEncoded = urlAppend.stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLHostAllowedCharacterSet())
        var url = "\(urls.apiBase)\(urlAppendEncoded!)"
        let requestUrl = NSURL(string: url)

        if (requestUrl != nil) {
            self.sendRequest(url, clearData: true, success: success, failure: failure)
        } else {
            failure();
        }

    }

    func makeNewRequest(search: String, platforms: Array<String>) -> String {
        var suffix = "?search=\(search)";
        if (platforms.count > 0) {
            for (var i = 0; i < platforms.count; i++) {
                suffix += "&platforms=\(platforms[i])";
            }
        }
        suffix += "&PageSize=20"
        println(suffix)
        self.lastSearchSuffix = suffix;
        println(suffix)
        return suffix;
        //getLobbies(searchTerms: suffix);
    }

    func getMoreResults(success: () -> Void) {
       if (self.loadMore) {
           self.loadMore = false;
           self.pageIndex++;
           println(self.lastSearchSuffix)
           var suffix = self.lastSearchSuffix + "&PageIndex=" + String(self.pageIndex)
           println(suffix)
           var urlAppend = "lobby\(suffix)";
            println(urlAppend)
           var urlAppendEncoded = urlAppend.stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLHostAllowedCharacterSet())
           var url = "\(urls.apiBase)\(urlAppendEncoded!)"
            println(url)
           let requestUrl = NSURL(string: url)

           if (requestUrl != nil) {
               self.sendRequest(url, clearData: false, success: success, failure: {

               });
           }
       }
    }

    func resetPageCount() {
        self.loadMore = false;
        self.pageIndex = 0;
    }

    func getLobbies(search: String, platforms: Array<String>, applyChange: Bool = true, success: () -> Void, failure: () -> Void) {
        self.resetPageCount();
        var searchTerms = makeNewRequest(search, platforms: platforms);

        var urlAppend = "";
        if searchTerms != "" {
            urlAppend = "lobby\(searchTerms)";
        } else {
            urlAppend = "lobby"
        }

        var urlAppendEncoded = urlAppend.stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLHostAllowedCharacterSet())
        var url = "\(urls.apiBase)\(urlAppendEncoded!)"
        let requestUrl = NSURL(string: url)

        if (requestUrl != nil) {

           self.sendRequest(url, clearData: true, success: success, failure: {
               Error(alertTitle: "Couldn't load games", alertText: "Sorry about that, try again soon.")

           })


        }


    }

    func sendRequest(url: String, clearData: Bool, success: () -> Void, failure: () -> Void) {
        Alamofire.request(.GET, url).responseJSON { (request, response, responseJSON, error) in
            var resp: NSArray = responseJSON! as! NSArray
           // println(resp.count);
            if (resp.count>0) {
                self.updateData(resp, clearData: clearData);
                success();
                self.updated = false;
            } else {
                self.loadMore = false;
                failure();
            }
        };
    }

    func removeAllDelegates() {
        for (var i = 0; i < gamesKey.count; i++) {
            for (var p = 0; p < gamesOrganized[gamesKey[i]]!.count; p++) {
                myChatsListener.removeDelegate(gamesOrganized[gamesKey[i]]![p] as! QuickBloxDelegate);
            }
        }


            //removeDelegate(se
    }


    func updateData(data: NSArray, clearData: Bool) {  //update data and call a redraw in the UI
        if (clearData) {
            removeAllDelegates();
            games.removeAll();
            gamesOrganized[gamesKey[0]] = []
            gamesOrganized[gamesKey[1]] = []
            gamesOrganized[gamesKey[2]] = []
            gamesOrganized[gamesKey[3]] = []
            gamesOrganized[gamesKey[4]] = []
            gameCount = 0;
        }
        updated = true;

        for (var i = 0; i<data.count; i++) {
            var lobby = data[i] as! NSDictionary;

            var lobbyObject: LobbyData?


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

//    func addGameToGamesKey(key: Int, lobby: LobbyData?, data: NSDictionary) {
//        if (lobby == nil) {
//            lobby = LobbyData(data: data);
//        }
//        gamesOrganized[gamesKey[key]]!.append(lobby!)
//        gameCount++;
//    }


    func sortByTime() {


    }


}