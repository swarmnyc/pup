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
    var isMember: Bool {
        get {
           if (currentUser.loggedIn()) {
               if (currentUser.joinedLobbies[self.id] != nil) {
                   return currentUser.joinedLobbies[self.id]!;
               }
               return false;
           } else {
               return false;
           }
        }
        set(value) {
            if (currentUser.loggedIn()) {
                currentUser.joinedLobbies[self.id] = value;
            }
        }
    };
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

    weak var quickBloxConnect: QuickBlox?
    var recievedMessages = false;
    var setMessagesRecieved: (() -> Void)?
    var clearTheText: (() -> Void)?
    var reloadData: (() -> Void)?
    var checkUnreadCounter: ((Int) -> Void)?

    var messageSkip = 0;

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

        if (data.objectForKey("unreadMessageCount") != nil) {
            unreadMessageCount = data["unreadMessageCount"] as! Int
        }

        lastMessageAt = data["lastMessageAt"] as! String;
        updatedAtUtc = data["updatedAtUtc"] as! String;
        self.isMember = false;
        addOwnerAndUsersToData(data);


        if (self.isMember) {
            var dateString = self.updatedAtUtc.replace("T",replacement: " ").replace("Z",replacement: ""); //figure out when the lobby was last updated
            var lastPostDate = NSDate(fromString: dateString, format: .Custom("yyyy-MM-dd HH:mm:ss"));

            if (lastPostDate.daysBeforeDate(NSDate()) < 19) { //let's check to see if it was more recently than 19 days
                 //ok cool, lets listen for new messages all the time
                self.quickBloxConnect = myChatsListener.addDelegate(self as! QuickBloxDelegate) //let's save a reference to the quickblox object so we can post new messages
                myChatsListener.addRoom(self.QBChatRoomId);  //we need to add the room to the listener and join it
            }

        }


        loadInitialMessages();


    }


    func addQuickBloxConnect() {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)) {
            self.quickBloxConnect = myChatsListener.addDelegate(self as! QuickBloxDelegate) //let's save a reference to the quickblox object so we can post new messages
            myChatsListener.addRoom(self.QBChatRoomId); //we need to add the room to the listener and join it
        }
    }


    func addSelfAsMember() {
        self.isMember = true;
        self.addQuickBloxConnect();
    }



    func addOwnerAndUsersToData(detailed: NSDictionary) {
        var user = detailed["users"] as! NSArray

        for (var i = 0; i<user.count; i++) {
            var userName = user[i]["userName"] as! String

            //is the current user in this lobby?
            if userName == currentUser.data.name {
                if (user[i]["isLeave"] as! Bool == false) {
                    self.isMember = true;
                }
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

    func updateUsers() {


    }


    func loadInitialMessages() {

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)) {
            var url = urls.lobbies + "/message/" + self.id;

            self.sendRequest(url, reverseMessage: true, clearData: true, success: {

                    self.setMessagesRecieved?();
                    self.recievedMessages = true;
                    self.reloadData?();

            }, failure: {

            })

        }


    }


    func sendRequest(url: String, reverseMessage: Bool, clearData: Bool, success: () -> Void, failure: () -> Void) {

            Alamofire.request(.GET, url).responseJSON { (request, response, responseJSON, error) in

                var respo: NSArray? = responseJSON as? NSArray
                if (respo != nil) {
                    // println(resp.count);
                    var resp = respo!;
                    if (reverseMessage) {
                       resp = respo!.reverseObjectEnumerator().allObjects;
                    }
                    if (self.messages.count == 0) {
                        for (var i = 0; i < resp.count; i++) {
                            var message = resp[i] as! NSDictionary
                            var userId = message["userId"] as? String;
                            if (userId == nil) {
                                userId = "";
                            }

                            
                            
                            var text = message["message"] as? String;
                            var timeCreated = message["created_at"] as! String;
                            
                            if (text != nil) {
//
                            self.messages.append(Message(userId: userId!, message: text!, timeSent: timeCreated, propics: self.proPicDict, users: self.users, owner: self.owner))
                            }
                        }
                    }
                    success();
                }
            };

    }



    func addMessageAtStart(message: AnyObject) {

            self.messages.insert(Message(messages: message, propics: proPicDict, users: users, owner: owner), atIndex: 0);
           // reloadData?();

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



    func addMember(name: String, id: String, portrait: String?) {
        var alreadyAMember = false;
        for (var i = 0; i < self.users.count; i++) {
            if (self.users[i].id == id) {
               alreadyAMember = true;
            }

        }

        if (alreadyAMember == false) {
            self.proPicDict[name] = "";

            if (portrait != nil) {
                self.proPicDict[name] = portrait!;
                var memberData: NSDictionary = ["isLeave": false, "isOwner": false, "id": id, "userName": name, "portraitUrl": portrait!]
                self.users.append(SingleLobbyUser(data: memberData));


            } else {
                var memberData: NSDictionary = ["isLeave": false, "isOwner": false, "id": id, "userName": name]
                self.users.append(SingleLobbyUser(data: memberData));
            }


        }


    }


    //QuickBlox Delegate

    func clearText() {
        println(clearTheText);
        clearTheText?();
    }

    func sendMessage(message: String) {
        myChatsListener.quickBloxConnect?.sendMessage(message, roomid: QBChatRoomId);
    }


    func handOffMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage) {
        self.recievedMessages = true;
        setMessagesRecieved?();
        self.addMessages(messages.reverseObjectEnumerator().allObjects); //reverse them so that they display the proper way
    }



    func addNewMessage(message: QBChatMessage) {

        self.addSingleMessage(message);
        self.messageSkip++; //make it so that when autoloading older messages there aren't repeats, we know to skip some more

        var codeBody = message.customParameters?["codeBody"] as? String
        if (codeBody != nil) {
           var memberData: (String, String, String?)? = prepareMember(codeBody!);
            if (memberData != nil) {
                addMember(memberData!.0, id: memberData!.1, portrait: memberData!.2);
            }
        }


    }

    func prepareMember(codeBody: String) -> (String, String, String?)? {
        var data: NSData = codeBody.dataUsingEncoding(NSUTF8StringEncoding)!
        var error: NSError?

        var dataAsJson: AnyObject? = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.allZeros, error: nil)
        var userData = dataAsJson as? NSArray;
        if (userData != nil) {
            var userName: String? = userData![0]["userName"] as? String;
            var userId: String? = userData![0]["id"] as? String;
            var portraitUrl: String? = userData![0]["portraitUrl"] as? String;
            if (userName != nil) {
                return (userName!, userId!, portraitUrl)

            }
        }

        return nil
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
            var pic = propics[self.username] as? String?;
            if (pic != nil) {

                self.picture = propics[self.username] as! String!
            } else {
            self.picture = "";
            }
        } else {
            self.username = "system message"
            self.picture = ""

            //var newMemberInfo = messages.customParameters?["codeBody"] as! NSDictionary;
            //println(newMemberInfo);

        }


        var datetime = messages.valueForKey("datetime") as! NSDate;

        var dateFormatter = NSDateFormatter();
        var timeZone = NSTimeZone(name: "UTC")
        dateFormatter.timeZone = timeZone;
        dateFormatter.dateFormat = "yyy-MM-dd HH:mm:ss";
        var dateString = dateFormatter.stringFromDate(datetime) as! String


        self.timesent = dateString.stringByReplacingOccurrencesOfString(" ", withString: "T", options: NSStringCompareOptions.LiteralSearch, range: nil) + "Z";
        var message = messages.valueForKey("text") as? String
        if (message != nil) {
            self.message = message!;
        } else {
            self.message = "";
        }


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
            self.sendRequest(url,onlyAddNewData: true, clearData: true, success: success, failure: failure)
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
               self.sendRequest(url,onlyAddNewData: false, clearData: false, success: success, failure: {

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

           self.sendRequest(url,onlyAddNewData: false, clearData: true, success: success, failure: {
               Error(alertTitle: "Oops, no avaliable games.", alertText: "Try Again soon, or create a game")

           })


        }


    }



    func sendRequest(url: String, onlyAddNewData: Bool, clearData: Bool, success: () -> Void, failure: () -> Void) {
        Alamofire.request(.GET, url).responseJSON { (request, response, responseJSON, error) in

            if (responseJSON != nil) {
                var respDict = responseJSON! as? NSDictionary;


                if let resp: NSArray = respDict!["result"] as? NSArray {

                    // println(resp.count);
                    if (resp.count > 0) {
                        if (onlyAddNewData == false) {
                            self.updateData(resp, clearData: clearData);
                        } else {
                            self.addNewLobbies(resp)
                        }
                        success();
                        self.updated = false;
                    } else {
                        self.loadMore = false;
                        failure();
                    }
                }
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


    }


    func addNewLobbies(data: NSArray) {
        for (var i = 0; i < data.count; i++) {
            var exists = false;
            var lobby = data[i] as! NSDictionary;
            var lobbyObject: LobbyData?
            var startTimeUtc = lobby["startTimeUtc"] as! String
            let date = NSDate(fromString: startTimeUtc, format: .ISO8601)
            var gameKeyIndex = 0;
            if (date.minutesAfterDate(NSDate()) <= 30) {
                gameKeyIndex = 0;
            } else if (date.isToday()) {
                gameKeyIndex = 1;
            } else if (date.isTomorrow()) {
                gameKeyIndex = 2;
            } else if (date.isThisWeek()) {
                gameKeyIndex = 3;
            } else {
                gameKeyIndex = 4;
            }

            for (var p = 0; p < gamesOrganized[gamesKey[gameKeyIndex]]!.count; p++) {
                if (lobby.valueForKey("id") as! String == gamesOrganized[gamesKey[gameKeyIndex]]![p].id) {
                    exists = true;
                }

            }
            if (exists == false) {
                var lobby = data[i] as! NSDictionary;
                gamesOrganized[gamesKey[gameKeyIndex]]!.insert(LobbyData(data: lobby), atIndex: 0);
            }


        }






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