
//
// Created by Alex Hartwell on 6/12/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

class QuickBlox: NSObject, QBChatDelegate {


    var handOffMessages: ((response: QBResponse, messages: NSArray, responcePage: QBResponsePage) -> Void)?
    var lobbyId = "";
    var message: String?
    var roomID: String?
    var room: QBChatRoom?

    override init() {
        super.init()
        

        QBApplication.sharedApplication().applicationId = 24285;
        QBConnection.registerServiceKey("BeUfd9nMYp6OV8-");
        QBConnection.registerServiceSecret("QUBVCLgUjFY97cV");
        QBSettings.setAccountKey("pprqroRWUFPimxN8Lq89");

        //createSessionWithDefaultUser()
        
        


    }


    func createSession() {
        logoutOfChat();
        var parameters = QBSessionParameters();
        parameters.userLogin = currentUser.data.userId
        parameters.userPassword = "swarmnyc"

        println("creating session with actual user")

        QBRequest.createSessionWithExtendedParameters(parameters, successBlock: {
            response, session in

            var currentUser = QBUUser()
            currentUser.ID = session.userID;
            currentUser.password = "swarmnyc"

            QBChat.instance().addDelegate(self)
            QBChat.instance().loginWithUser(currentUser)

        }, errorBlock: {
            response in
            println("errrrooooorrrrr")
        }
        )
    }

    func createSessionWithDefaultUser() {
        logoutOfChat();
        var parameters = QBSessionParameters();
        parameters.userLogin = appData.QBDefaultUser
        parameters.userPassword = appData.QBDefaultPassword

        println("creating session with default user")

        QBRequest.createSessionWithExtendedParameters(parameters, successBlock: {
            response, session in

            var currentUser = QBUUser()
            currentUser.ID = session.userID;
            currentUser.password = appData.QBDefaultPassword

            QBChat.instance().addDelegate(self)
            QBChat.instance().loginWithUser(currentUser)

            self.getMessages();
        }, errorBlock: {
            response in
            println("errrrooooorrrrr")
        }
        )


    }


    func logoutOfChat() {
        if (QBChat.instance().isLoggedIn()) {
            QBChat.instance().logout()
        }
    }

    func getMessages() {

        var resPage: QBResponsePage = QBResponsePage(limit: 20, skip: 0);
        println("getting messages inside of quickblox object")
        QBRequest.messagesWithDialogID(lobbyId, extendedRequest: nil, forPage: resPage, successBlock: {
            response, messages, responcePage in
            println("got messages")
            self.handOffMessages?(response: response, messages: messages, responcePage: responcePage);
        }, errorBlock: {
            response in
            println(response.error);
        });
    }


    func joinRoom() {
        if (currentUser.loggedIn()) {
            if (QBChat.instance().isLoggedIn()) {
                println("is logged in")
            } else {
                println("is not logged in")

            }

            var dialog = QBChatDialog(dialogID: roomID!)
            dialog.roomJID = appData.QBAppId + "_" + roomID! + "@muc.chat.quickblox.com"
            dialog.userID = UInt(currentUser.data.QBChatId.toInt()!)
            var room = dialog.chatRoom;
            room.joinRoom()
        }
    }

    func sendMessage(message: String) {
//     roomID =  self.data.data.QBChatRoomId
        self.message = message;

        var qbmessage = QBChatMessage();
        qbmessage.text = self.message!;
        var params: NSMutableDictionary = ["userId": currentUser.data.userId, "save_to_history": true, "userName": currentUser.data.name]
        qbmessage.customParameters = params;
        QBChat.instance().sendChatMessage(qbmessage, toRoom: self.room!)

        println("sending message!!!!!!")
    }


//- (void)chatRoomDidEnter:(QBChatRoom *)room{
//    // joined here, now you can send and receive chat messages within this group dialog
//}

    func chatRoomDidEnter(room: QBChatRoom) {
        println("chatRoom did enter");
        self.room = room;

    }

    //QBChatDelegate
    func chatDidLogin() {
        println("logged in!")
        println(roomID);
        joinRoom();

    }

    func chatDidNotLogin() {

    }





}
