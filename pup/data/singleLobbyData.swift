//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire


class singleLobby {
    var data = LobbyData();
    var empty = false;
    var quickBloxConnect: QuickBlox?
    var passMessagesToController: (() -> Void)?
    var recievedMessages = false;
    init() {
        quickBloxConnect = QuickBlox();
        quickBloxConnect?.handOffMessages = self.recieveMessages;
        quickBloxConnect?.addNewMessage = self.addNewMessage;
    }


    func isEmpty() -> Bool {
        if data.users.count==1 {
            return true
        }

        return false;


    }

    func hasMessages() -> Bool {
        if (data.messages.count==0 && recievedMessages == false) {
            return false
        } else {
            return true
        }

    }


    func joinLobby(success: () -> Void, failure: () -> Void) {


        let URL = NSURL(string: urls.joinLobby + self.data.id)!
        let mutableURLRequest = NSMutableURLRequest(URL: URL)
        mutableURLRequest.HTTPMethod = "POST"

        var JSONSerializationError: NSError? = nil
        //mutableURLRequest.HTTPBody = urlEnd;
        mutableURLRequest.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")

        Alamofire.request(mutableURLRequest).responseJSON { (request, response, JSON, error) in
           if (error == nil) {
               self.data.isMember = true;
            success();
            self.quickBloxConnect!.logoutOfChat();
            self.quickBloxConnect!.createSession();
           } else {
               Error(alertTitle: "Couldn't Join Room", alertText: "We had some trouble getting you in, please try again...")
               failure()
           }
            println(error)
            println("---")
            println(JSON)
        }

    }


    func recieveMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage) {
        data.addMessages(messages);
        recievedMessages = true;
        passMessagesToController?();
    }

    func addNewMessage(newMessage: String) {
        data.messages.append(Message(newMessage: newMessage))
        passMessagesToController?();
    }

    func setID() {
        quickBloxConnect?.roomID = data.QBChatRoomId;
    }



    func setRoomIDAndLogIn() {


            self.quickBloxConnect?.lobbyId = self.data.QBChatRoomId;
            self.loginToQuickBlox()


    }


    func loginToQuickBlox() {
        if (self.data.isMember) {
            quickBloxConnect?.createSession();
        } else {
            quickBloxConnect?.createSessionWithDefaultUser();
        }

    }

    func logoutOfQuickBlox() {
        quickBloxConnect?.logoutOfChat()
    }




}

