
//
// Created by Alex Hartwell on 6/12/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

class QuickBlox: NSObject, QBChatDelegate {


    var delegates: Array<QuickBloxDelegate> = [];
    var roomIDs: Array<String> = [];
    var rooms: Array<QBChatRoom> = [];
    var messageLimit = 20;


    override init() {
        super.init()

        //set up the application keys and data
        QBApplication.sharedApplication().applicationId = 24285;
        QBConnection.registerServiceKey("BeUfd9nMYp6OV8-");
        QBConnection.registerServiceSecret("QUBVCLgUjFY97cV");
        QBSettings.setAccountKey("pprqroRWUFPimxN8Lq89");

        //createSessionWithDefaultUser()
        if (currentUser.loggedIn()) {
            createSession()
        }



    }


    func createSession() {

       // println("create session with current user!!!")
        if (!QBChat.instance().isLoggedIn()) {

            var parameters = QBSessionParameters();
            parameters.userLogin = currentUser.data.userId
            parameters.userPassword = "swarmnyc"

           // println("creating session with actual user")

            QBRequest.createSessionWithExtendedParameters(parameters, successBlock: {
                response, session in

                var currentUser = QBUUser()
                currentUser.ID = session.userID;
                currentUser.password = "swarmnyc"

                self.addQBDelegates()
                QBChat.instance().loginWithUser(currentUser)
                QBChat.instance().autoReconnectEnabled = true;
                QBChat.instance().streamManagementEnabled = true;
                QBConnection.setAutoCreateSessionEnabled(true);
                println(QBChat.instance().delegates);

            }, errorBlock: {
                response in
                println("errrrooooorrrrr")
            }
            )
        }

    }










    func getMessages(chatRoom: QBChatRoom) {

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)) { //lets get em in the background


            var extendedRequest = ["sort_desc": "date_sent"];
            var resPage: QBResponsePage = QBResponsePage(limit: self.messageLimit, skip: 0);
            QBRequest.messagesWithDialogID(self.roomIDFromJID(chatRoom.JID as! String), extendedRequest: extendedRequest, forPage: resPage, successBlock: {
                response, messages, responcePage in

                    for (var i = 0; i < self.delegates.count; i++) {  //search through the delegates
//                    println(i)

                        if (self.roomIDToJID(self.delegates[i].QBChatRoomId) == chatRoom.JID) {  //make sure the messages match the delegates id
                            self.delegates[i].handOffMessages(response, messages: messages, responcePage: responcePage);
                        }
                    }

            }, errorBlock: {
                response in
                println("got messages error") //there were errors of some kind
                println(response.error);
            });

        }
    }

    func loadMoreHistory(messageSkip: Int, lobbyID: String, success: ((messages: NSArray) -> Void), failure: (() -> Void)) { //load messages with a skip amount

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)) {

            var extendedRequest = ["sort_desc": "date_sent"];
            var resPage: QBResponsePage = QBResponsePage(limit: self.messageLimit, skip: messageSkip);

            QBRequest.messagesWithDialogID(lobbyID, extendedRequest: extendedRequest, forPage: resPage, successBlock: {
                response, messages, responcePage in
                success(messages: messages);
            }, errorBlock: {
                response in
                failure();
            });

        }
    }

    func addQBDelegates() { //set the QuickBlox Delegate to this object
        QBChat.instance().removeAllDelegates();
        QBChat.instance().addDelegate(self as! QBChatDelegate)
    }


//    func getChats() { //get
//
//        var page: QBResponsePage = QBResponsePage(limit: 40, skip: 0);
//
//        QBRequest.dialogsForPage(page, extendedRequest: nil, successBlock: {
//            (response, dialogObject, dialogsUsersIDs, page) -> Void in
//
//
//        }, errorBlock: {
//            (response) -> Void in
//                println(response);
//
//        })
//
//
//    }


    //a few conversion functions to match room ids and jids

    func roomIDToJID(roomid: String) -> String {
        return appData.QBAppId + "_" + roomid + "@muc.chat.quickblox.com";
    }

    func roomIDFromJID(jid: String) -> String {
        return jid.replace(appData.QBAppId + "_",replacement: "").replace("@muc.chat.quickblox.com", replacement: "");

    }


    //join all of the rooms in the room id list
    func joinAllRooms() {

        for (var i = 0; i < self.roomIDs.count; i++) {
            self.joinRoom(self.roomIDs[i]);
        }

    }

    func joinRoom(roomid: String) { //join the room so we can get its information

        if (QBChat.instance().isLoggedIn()) { //you can only join a room if we are logged in
            var dialog = QBChatDialog(dialogID: roomid)
            dialog.roomJID = appData.QBAppId + "_" + roomid + "@muc.chat.quickblox.com"
            dialog.userID = UInt(currentUser.data.QBChatId.toInt()!)
            var room = dialog.chatRoom;
            room.joinRoomWithHistoryAttribute(["maxstanzas": "0"])

            var chatroom: QBChatRoom? = self.getQBChatRoomFromId(roomid); //if this isn't the first time you've entered one of these rooms the message's
                                                                    //won't load so lets check to see if we are in it and reload the messages
            if (chatroom != nil) {
                self.getMessages(chatroom!);
            }

        } else {  //if you aren't logged in call a timer to recall this function

            NSTimer.scheduledTimerWithTimeInterval(1.0, target: self, selector: "joinRoomTimer:", userInfo: roomid, repeats: false);

        }

    }

    //room join looper
    func joinRoomTimer(timer: NSTimer) {
        var roomid = timer.userInfo as! String;
        joinRoom(roomid);
    }

    func sendMessage(message: String, roomid: String) {

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)) {
            var theRoom: QBChatRoom?

            for (var i = 0; i < self.rooms.count; i++) { //get the room object so you can send a message
                if (self.rooms[i].JID == self.roomIDToJID(roomid)) {
                    theRoom = self.rooms[i];
                }
            }

            if (theRoom != nil) { //if the room exists

                var qbmessage = QBChatMessage();
                qbmessage.text = message;
                var params: NSMutableDictionary = ["userId": currentUser.data.userId, "save_to_history": true, "userName": currentUser.data.name]
                qbmessage.customParameters = params;

                var didSend = QBChat.instance().sendChatMessage(qbmessage, toRoom: theRoom)

                dispatch_async(dispatch_get_main_queue()) {
                    SwiftLoader.show(title: "Sending Message", animated: false);
                }
                if (didSend) {

                    dispatch_async(dispatch_get_main_queue()) {
                        SwiftLoader.hide();
                        for (var i = 0; i < self.delegates.count; i++) {
                            if (self.delegates[i].QBChatRoomId == roomid) {
                                self.delegates[i].clearText(); //clear the text in the input box
                            }
                        }
                    }
                } else {
                    dispatch_async(dispatch_get_main_queue()) {
                        SwiftLoader.hide();
                        Error(alertTitle: "Couldn't Send Message", alertText: "Please, try again!")
                    }
                }
            } else {  //if the room doesn't exist
                dispatch_async(dispatch_get_main_queue()) {
                    Error(alertTitle: "Couldn't Send Message", alertText: "Please, try again!")
                }

            }


        }
    }





    func getQBChatRoomFromId(roomid: String) -> QBChatRoom? {
        var JID = roomIDToJID(roomid);
        for (var i = 0; i<self.rooms.count; i++) {
            if (self.rooms[i].JID == JID) {
                return self.rooms[i];
            }
        }

        return nil;

    }

    func chatRoomDidEnter(room: QBChatRoom) { //we entered the room, so let's save it and get its messages
        self.rooms.append(room)
        self.getMessages(room);

    }


    //QBChatDelegate
    func chatDidLogin() {
        joinAllRooms()
        //this object will only have a roomID if we are in a room



    }




    func getLobbyID(roomJID: String) -> String {

        for (var i = 0; i<delegates.count; i++) {
            if (self.roomIDToJID(self.delegates[i].QBChatRoomId) == roomJID) {
                var lobby = self.delegates[i] as! LobbyData;
                return lobby.id;
            }
        }
        return "";
    }

    func chatRoomDidReceiveMessage(message: QBChatMessage!, fromRoomJID roomJID: String) {


        for (var i = 0; i<delegates.count; i++) {
            if (self.roomIDToJID(self.delegates[i].QBChatRoomId) == roomJID) {
                self.delegates[i].addNewMessage(message);
            }
        }



    }



    func chatDidNotLogin() {
        //neeeded for delegate subclass
    }







}
