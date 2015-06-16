//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire


class singleLobby {
    var data = LobbyData();
    var empty = false;
    var isMember = false;
    var quickBloxConnect: QuickBlox?
    var passMessagesToController: (() -> Void)?

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


    func joinLobby(success: () -> Void, failure: () -> Void) {


        let URL = NSURL(string: urls.joinLobby + self.data.id)!
        let mutableURLRequest = NSMutableURLRequest(URL: URL)
        mutableURLRequest.HTTPMethod = "POST"

        var JSONSerializationError: NSError? = nil
        //mutableURLRequest.HTTPBody = urlEnd;
        mutableURLRequest.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")

        Alamofire.request(mutableURLRequest).responseJSON { (request, response, JSON, error) in
            success();
            self.quickBloxConnect!.logoutOfChat();
            self.quickBloxConnect!.createSession();
            println(error)
            println("---")
            println(JSON)
        }

    }


    func recieveMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage) {
        data.addMessages(messages);
        passMessagesToController?();
    }

    func addNewMessage(newMessage: String) {
        data.messages.append(Message(newMessage: newMessage))
        passMessagesToController?();
    }

    func setID() {
        quickBloxConnect?.roomID = data.QBChatRoomId;
    }



    func getDetailedAndThenGetMessages(success: () -> Void, failure: () -> Void) {

        let requestUrl = NSURL(string: "\(urls.lobbies)\(data.id)")

        Alamofire.request(.GET, requestUrl!).responseJSON { (request, response, responseJSON, error) in
            var resp: NSDictionary = responseJSON! as! NSDictionary

            println(resp);

            self.addOwnerAndUsersToData(resp);
            self.quickBloxConnect?.lobbyId = self.data.QBChatRoomId;
            self.loginToQuickBlox()

            success();

        };

    }


    func loginToQuickBlox() {
        if (self.isMember) {
            quickBloxConnect?.createSession();
        } else {
            quickBloxConnect?.createSessionWithDefaultUser();
        }

    }

    func logoutOfQuickBlox() {
        quickBloxConnect?.logoutOfChat()
    }

    func addOwnerAndUsersToData(detailed: NSDictionary) {
        var user = detailed["users"] as! NSArray

        for (var i = 0; i<user.count; i++) {
            var userName = user[i]["userName"] as! String
            println("'" + currentUser.data.name.removeWhitespace() + "'")
            println("'" + userName.removeWhitespace() + "'")
            if userName.removeWhitespace() == currentUser.data.name.removeWhitespace() {
                self.isMember = true;
            }

            if (user[i]["isOwner"] as! Bool) {
                data.owner = SingleLobbyUser(data: user[i] as! NSDictionary)
            } else {
                data.users.append(SingleLobbyUser(data: user[i] as! NSDictionary));
            }
        }


    }



}

