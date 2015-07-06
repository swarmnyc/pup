
//
// Created by Alex Hartwell on 6/12/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

class QuickBlox: NSObject, QBChatDelegate {


    var delegates: Array<QuickBloxDelegate> = [];



    var messageToSend: String?

    var roomIDs: Array<String> = [];
    var rooms: Array<QBChatRoom> = [];

    var messageLimit = 20;
    var messageSkip = 0;

    var newMessages = 0;

    override init() {
        super.init()


        QBApplication.sharedApplication().applicationId = 24285;
        QBConnection.registerServiceKey("BeUfd9nMYp6OV8-");
        QBConnection.registerServiceSecret("QUBVCLgUjFY97cV");
        QBSettings.setAccountKey("pprqroRWUFPimxN8Lq89");

        //createSessionWithDefaultUser()
        if (currentUser.loggedIn()) {
            createSession(true)
        }



    }


    func createSessionWithCurrentUser() {

       // println("create session with current user!!!")
        if (QBChat.instance().isLoggedIn()) {
            self.sessionCreationSuccess()



        } else {
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
                self.sessionCreationSuccess()

            }, errorBlock: {
                response in
                println("errrrooooorrrrr")
            }
            )
        }

    }

    func createSession(isMember: Bool) {
       //logoutOfChat();
        if (isMember) {
            createSessionWithCurrentUser();
        } else {
            createSessionWithDefaultUser();
        }
    }

    func createSessionWithDefaultUser() {
//        var parameters = QBSessionParameters();
//        parameters.userLogin = appData.QBDefaultUser
//        parameters.userPassword = appData.QBDefaultPassword
//
//        println("creating session with default user")
//
//        QBRequest.createSessionWithExtendedParameters(parameters, successBlock: {
//            response, session in
//
//            var currentUser = QBUUser()
//            currentUser.ID = session.userID;
//            currentUser.password = appData.QBDefaultPassword
//
//            self.addQBDelegates()
//            QBChat.instance().loginWithUser(currentUser)
//            QBChat.instance().autoReconnectEnabled = true;
//            QBChat.instance().streamManagementEnabled = true;
//            println(QBChat.instance().delegates);
//            self.sessionCreationSuccess();
//        }, errorBlock: {
//            response in
//            println("errrrooooorrrrr")
//        }
//        )


        //call and get chat data from our api

    }


    func sessionCreationSuccess() {


    }






    func getMessages(chatRoom: QBChatRoom) {

        dispatch_async(dispatch_get_global_queue(priority, 0)) {

            if (QBChat.instance().isLoggedIn()) {
                //println("logged in.....")
            }
            println("getting messages")

            var extendedRequest = ["sort_desc": "date_sent"];
            var resPage: QBResponsePage = QBResponsePage(limit: self.messageLimit, skip: self.messageSkip);
            QBRequest.messagesWithDialogID(self.roomIDFromJID(chatRoom.JID as! String), extendedRequest: extendedRequest, forPage: resPage, successBlock: {
                response, messages, responcePage in
                println("got messages")
                println(self.delegates.count)
                dispatch_async(dispatch_get_main_queue()) {

                    for (var i = 0; i < self.delegates.count; i++) {
//                    println(i)

                        if (self.roomIDToJID(self.delegates[i].QBChatRoomId) == chatRoom.JID) {
                            self.delegates[i].handOffMessages(response, messages: messages, responcePage: responcePage);
                        }
                    }
                }
            }, errorBlock: {
                response in
                println("got messages error")
                println(response.error);
            });

        }
    }

    func loadMoreHistory(success: ((messages: NSArray) -> Void), failure: (() -> Void)) {
//        self.messageSkip += self.messageLimit;
//
//        var extendedRequest = ["sort_desc": "date_sent"];
//
//        var resPage: QBResponsePage = QBResponsePage(limit: messageLimit, skip: messageSkip);
//        QBRequest.messagesWithDialogID(roomID!, extendedRequest: extendedRequest, forPage: resPage, successBlock: {
//            response, messages, responcePage in
//            println("got messages")
//            success(messages: messages);
//        }, errorBlock: {
//            response in
//            println(response.error);
//            failure();
//        });


    }

    func addQBDelegates() {
        QBChat.instance().removeAllDelegates();
        QBChat.instance().addDelegate(self as! QBChatDelegate)
    }


    func getChats() {

        var page: QBResponsePage = QBResponsePage(limit: 40, skip: 0);

        QBRequest.dialogsForPage(page, extendedRequest: nil, successBlock: {
            (response, dialogObject, dialogsUsersIDs, page) -> Void in
//                println(dialogObject)
//                println(dialogsUsersIDs)
//                println(page)

        }, errorBlock: {
            (response) -> Void in
                println(response);

        })


    }


    func roomIDToJID(roomid: String) -> String {
        return appData.QBAppId + "_" + roomid + "@muc.chat.quickblox.com";
    }

    func roomIDFromJID(jid: String) -> String {
        return jid.replace(appData.QBAppId + "_",replacement: "").replace("@muc.chat.quickblox.com", replacement: "");

    }


    func joinAllRooms() {

        for (var i = 0; i < self.roomIDs.count; i++) {
            self.joinRoom(self.roomIDs[i]);
        }

    }

    func joinRoom(roomid: String) {


        if (QBChat.instance().isLoggedIn()) {
                 //  println("is logged in")
            var dialog = QBChatDialog(dialogID: roomid)
            dialog.roomJID = appData.QBAppId + "_" + roomid + "@muc.chat.quickblox.com"
            dialog.userID = UInt(currentUser.data.QBChatId.toInt()!)
            var room = dialog.chatRoom;
            room.joinRoomWithHistoryAttribute(["maxstanzas": "0"])

//            println("joinging room!!!");
            var chatroom: QBChatRoom? = self.getRoomFromId(roomid); //if this isn't the first time you've entered one of these rooms the message's
                                                                    //won't load so lets check to see if we are in it and reload the messages
            if (chatroom != nil) {
                self.getMessages(chatroom!);
            }

        } else {

            NSTimer.scheduledTimerWithTimeInterval(1.0, target: self, selector: "joinRoomTimer:", userInfo: roomid, repeats: false);

        }

    }


    func joinRoomTimer(timer: NSTimer) {
        var roomid = timer.userInfo as! String;
        joinRoom(roomid);
    }

    func sendMessage(message: String, roomid: String) {
//     roomID =  self.data.data.QBChatRoomId

        dispatch_async(dispatch_get_global_queue(priority, 0)) {
            var theRoom: QBChatRoom?
            for (var i = 0; i < self.rooms.count; i++) {
                if (self.rooms[i].JID == self.roomIDToJID(roomid)) {
                    theRoom = self.rooms[i];
                }
            }

            if (theRoom != nil) {
                var qbmessage = QBChatMessage();
                qbmessage.text = message;
                var params: NSMutableDictionary = ["userId": currentUser.data.userId, "save_to_history": true, "userName": currentUser.data.name]
                qbmessage.customParameters = params;
                println("sending message!!!!!!")

                var didSend = QBChat.instance().sendChatMessage(qbmessage, toRoom: theRoom)

                dispatch_async(dispatch_get_main_queue()) {
                    SwiftLoader.show(title: "Sending Message", animated: false);
                }
                if (didSend) {
                    println("whooo")
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
            } else {
                dispatch_async(dispatch_get_main_queue()) {
                    Error(alertTitle: "Couldn't Send Message", alertText: "Please, try again!")
                }

            }


        }
    }




//- (void)chatRoomDidEnter:(QBChatRoom *)room{
//    // joined here, now you can send and receive chat messages within this group dialog
//}


    func getRoomFromId(roomid: String) -> QBChatRoom? {
        var JID = roomIDToJID(roomid);
        for (var i = 0; i<self.rooms.count; i++) {
            if (self.rooms[i].JID == JID) {
                return self.rooms[i];
            }
        }

        return nil;

    }

    func chatRoomDidEnter(room: QBChatRoom) {
        println("chatRoom did enter");
        self.rooms.append(room)
        self.getMessages(room);

    }


    //QBChatDelegate
    func chatDidLogin() {
        println("logged in!")
        joinAllRooms()
        //this object will only have a roomID if we are in a room



    }

//    func chatDidReceiveMessage(message: QBChatMessage) {
//        println("room recieved message!!!!")
//        println(message);
//
//    }


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
        println("room recieved message, with JID!")
        println(message);

        for (var i = 0; i<delegates.count; i++) {
            if (self.roomIDToJID(self.delegates[i].QBChatRoomId) == roomJID) {
                self.delegates[i].addNewMessage(message);
            }
        }
        messageSkip++;


    }



    func chatDidNotLogin() {

    }







}
