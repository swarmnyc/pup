
//
// Created by Alex Hartwell on 6/12/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

class QuickBlox: NSObject, QBChatDelegate {


    var handOffMessages: ((response: QBResponse, messages: NSArray, responcePage: QBResponsePage) -> Void)?
    var lobbyId = "";
    override init() {
        super.init()
        

        QBApplication.sharedApplication().applicationId = 24285;
        QBConnection.registerServiceKey("BeUfd9nMYp6OV8-");
        QBConnection.registerServiceSecret("QUBVCLgUjFY97cV");
        QBSettings.setAccountKey("pprqroRWUFPimxN8Lq89");

        //createSessionWithDefaultUser()
        
        


    }


    func createSession() {
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

            self.getMessages();
        }, errorBlock: {
            response in
            println("errrrooooorrrrr")
        }
        )
    }

    func createSessionWithDefaultUser() {
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

    //QBChatDelegate
    func chatDidLogin() {
        println("logged in!")

    }

    func chatDidNotLogin() {

    }





}
