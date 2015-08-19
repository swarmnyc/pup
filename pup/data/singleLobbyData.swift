//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire


class singleLobby {
    var data: LobbyData = LobbyData();
    var empty: Bool = false;
    var quickBloxConnect: QuickBlox?
    var passMessagesToController: (() -> Void)?
    var recievedMessages: Bool = false;
    var reloadData:(() -> Void)?
    var clearInputText: (() -> Void)?

    init() {
//        quickBloxConnect = QuickBlox();
//        quickBloxConnect?.handOffMessages = self.recieveMessages;
//        quickBloxConnect?.clearText = self.clearText;
//        quickBloxConnect?.addNewMessage = self.addNewMessage;
//        quickBloxConnect?.sessionCreationSuccess = self.getMessages;

//        func handOffMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage)
//        func clearText()
//        func addNewMessage(message: QBChatMessage)
//        func handOffChats()
        println("setting clear the text")


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

    func messagesReceived() {
        println("messages recieved")
        self.recievedMessages = true;
    }



    func sendMessage(message: String) {
        self.data.sendMessage(message);
    }

    func showingShare() -> Bool {
//        println("showing sharing")
//        println(data.messages.count);
        if (data.messages.count <= 0 && recievedMessages) {
            return true
        } else {
            return false
        }
    }



    func clearText() {
        println("clear text 1")
        clearInputText?();
    }

    func addSelfToMembers() {
        data.users.append(currentUser.getSelfAsSingleLobbyUser());
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
            self.data.addSelfAsMember();
               //self.data.addQuickBloxConnect();
           } else {
               SNYError(alertTitle: "Couldn't Join Room", alertText: "We had some trouble getting you in, please try again...", networkRequest: true)
               failure()
           }
            println(error)
            println("---")
            println(JSON)
        }

    }


    func loadMoreHistory(success: ((messages: NSArray) -> Void), failure: (() -> Void), quickBloxNotConnectedYet: (() -> Void)) {
        if (self.data.quickBloxConnect != nil) {
            self.data.messageSkip += self.data.quickBloxConnect!.messageLimit;
            self.data.quickBloxConnect?.loadMoreHistory(self.data.messageSkip, lobbyID: self.data.QBChatRoomId, success: success, failure: failure);

        } else {
            quickBloxNotConnectedYet();
        }
    }


    func insertMessagesToStartAndGetHeightOfMessages(messages: NSArray) -> CGFloat {
           var height: CGFloat = 0.0;
            for (var i = 0; i<messages.count; i++) {
                self.data.addMessageAtStart(messages[i])
                height += CGFloat(UIConstants.messageBaseSize) + UIConstants.getMessageHeightAdditionBasedOnTextLength(messages[i].valueForKey("text") as! String);
            }
            return height;
    }

    func handOffMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage) {

        self.data.addMessages(messages);

        if ((messages.count==0) && (self.recievedMessages == false)) {
            println("set up sharing")
            reloadData?();
        }

        reloadData?();
        self.recievedMessages = true;

        self.quickBloxConnect?.addQBDelegates();


    }



    func addNewMessage(message: QBChatMessage) {
        self.recievedMessages = true;
        println("adding new message")
        var lastMessage = data.addSingleMessage(message);

        reloadData?();

    }

    func handOffChats() {
        println("hand off chats!")
    }

    func lastMessageIsUser() -> Bool {
        //if you are logged in, there are messages, and then if the last message was made by you
        if (currentUser.loggedIn() && data.messages.count>2 && (data.messages[data.messages.count - 1].username == currentUser.data.name)) {
            return true
        }

        return false
    }






}

