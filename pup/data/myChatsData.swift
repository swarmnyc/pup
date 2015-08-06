//
// Created by Alex Hartwell on 6/18/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire

class MyChatsData {

    var lobbyReturn: ((lobbies: NSArray) -> Void)?
    var lobbies: Array<LobbyData> = [];
    var pageIndex = 0;
    var loadMore = true;
    var unreadCount = 0;
    var passOffAmount: ((Int) -> Void)?
    var removeCellAtIndex: ((Int) -> Void)?

    init() {

    }



    func getMyLobbies(onlyAddNew: Bool, success: () -> Void, failure: () -> Void) {
        if (currentUser.loggedIn()) {
            self.resetInfiniteScroll();
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)) {
                let URL = NSURL(string: urls.lobbies + "my")!
                println(URL);
                let mutableURLRequest = NSMutableURLRequest(URL: URL)
                mutableURLRequest.HTTPMethod = "GET"

                var JSONSerializationError: NSError? = nil
                //mutableURLRequest.HTTPBody = urlEnd;
                mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")

                Alamofire.request(mutableURLRequest).responseJSON {
                    (request, response, JSON, error) in
                    if (error == nil) {
                        println(JSON);
                        if (onlyAddNew == false) {
                            self.addLobbies(true, data: JSON as! NSArray)
                        } else {
                            self.addNewLobbies(JSON as! NSArray)
                        }
                        dispatch_async(dispatch_get_main_queue()) {
                            success();
                        }
                    } else {
                        failure();
                    }

                }
            }
        }
    }

    func resetInfiniteScroll() {
        self.pageIndex = 0;
        self.loadMore = true;
    }

    func getMoreLobbies(success: () -> Void, failure: () -> Void) {
        if (loadMore) {
            pageIndex++;
            let URL = NSURL(string: urls.lobbies + "my?" + "PageIndex=" + String(self.pageIndex))!
            println(URL);
            let mutableURLRequest = NSMutableURLRequest(URL: URL)
            mutableURLRequest.HTTPMethod = "GET"

            var JSONSerializationError: NSError? = nil
            //mutableURLRequest.HTTPBody = urlEnd;
            mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")
            loadMore = false;
            Alamofire.request(mutableURLRequest).responseJSON {
                (request, response, JSON, error) in
                if (error == nil) {
                    println(JSON);
                    self.addLobbies(false, data: JSON as! NSArray)
                    success();

                } else {
                    failure();
                }

            }
        }
    }

    func removeLobby(id:String) {
        for (var i = 0; i<self.lobbies.count; i++) {
            if (self.lobbies[i].id == id) {
                println(self.lobbies.count)
                self.lobbies[i].isMember = false;
                self.lobbies.removeAtIndex(i);
                self.passOffAmount?(i);
                println(self.lobbies.count)
                return;
            }
        }
    }

    func removeLobby(id: String, success: () -> Void, failure: () -> Void) {
        let URL = NSURL(string: urls.leaveLobby + id)!

        let mutableURLRequest = NSMutableURLRequest(URL: URL)
        mutableURLRequest.HTTPMethod = "POST"

        var JSONSerializationError: NSError? = nil
        //mutableURLRequest.HTTPBody = urlEnd;
        mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")

        Alamofire.request(mutableURLRequest).responseJSON {
            (request, response, JSON, error) in
            if (error == nil) {

                self.removeLobby(id);
                success();
            } else {
                failure();
                self.loadMore = true;

            }

        }
    }

    func changeUnreadCount(amountToAdd: Int) {
        self.unreadCount += amountToAdd;
        passOffAmount?(unreadCount);
    }

    func removeAllDelegates() {
        for (var i = 0; i < self.lobbies.count; i++) {

                myChatsListener.removeDelegate(self.lobbies[i] as! QuickBloxDelegate);

        }


        //removeDelegate(se
    }



    func addNewLobbies(data: NSArray) {
        for (var i = 0; i < data.count; i++) {
            var exists = false;

            for (var p = 0; p < lobbies.count; p++) {
                var lobby = data[i] as! NSDictionary;


                if (lobby.valueForKey("id") as! String == lobbies[p].id) {
                    exists = true;
                }

            }
            if (exists == false) {
                var lobby = data[i] as! NSDictionary;
                lobbies.insert(LobbyData(data: lobby), atIndex: 0);
            }


        }


    }

    func addLobbies(overwriteData: Bool, data: NSArray) {
        if (overwriteData) {
            self.unreadCount = 0;
            removeAllDelegates();
            lobbies = [];
        }

        if (data.count==0) {
            self.loadMore = false;
        } else {
            self.loadMore = true;

            for (var i = 0; i < data.count; i++) {
                var lobby = data[i] as! NSDictionary;
                println(lobby["name"])
                lobbies.append(LobbyData(data: lobby))
                lobbies[i].checkUnreadCounter = self.changeUnreadCount;
                self.changeUnreadCount(lobbies[i].unreadMessageCount);
            }


        }

    }


}

class MyChatsListener: NSObject, QBChatDelegate {

    var quickBloxConnect: QuickBlox?;
    var roomIDs: Array<String> = [];

    override init() {
        quickBloxConnect = QuickBlox();
       // self.quickBloxConnect.addQBDelegates();


//        quickBloxConnect?.addNewMessage = self.gotNewMessage;
//        quickBloxConnect?.sessionCreationSuccess = self.sessionCreationSuccess;

             quickBloxConnect?.createSession()


        println("my chats listener")

    }


    func addRoom(roomID: String) {
        println("add room")
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)) {
            self.quickBloxConnect?.roomIDs.append(roomID);
            self.quickBloxConnect?.joinRoom(roomID);
        }
    }

    func getDelegateByChatId(QBChatId: String) -> QuickBloxDelegate? {
        for (var i = 0; i < quickBloxConnect?.delegates.count; i++) {
            if (quickBloxConnect?.delegates[i].QBChatRoomId == QBChatId) {
                return quickBloxConnect?.delegates[i];
            }
        }

        return nil;

    }



    func addDelegate(delegate: QuickBloxDelegate) -> QuickBlox {
        println("add delegate")
        quickBloxConnect?.delegates.append(delegate);
        println(quickBloxConnect?.delegates);



        return quickBloxConnect!;
    }

    func removeDelegate(delegate: QuickBloxDelegate) {
        println("remove delegate!!!!!")
        for (var i = quickBloxConnect!.delegates.count - 1; i >= 0; i--) {
            if (quickBloxConnect?.delegates[i] === delegate) {
                    quickBloxConnect?.delegates.removeAtIndex(i);
            }
        }
    }

    func removeAllDelegates() {
        quickBloxConnect?.delegates = [];
    }

    func gotNewMessage(message: QBChatMessage) {
        println("message!!!!")

    }



}