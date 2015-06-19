//
// Created by Alex Hartwell on 6/18/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire

class MyChatsData {

    var lobbyReturn: ((lobbies: NSArray) -> Void)?
    var lobbies: Array<LobbyData> = [];

    init() {

    }



    func getMyLobbyies(success: () -> Void, failure: () -> Void) {
        let URL = NSURL(string: urls.lobbies + "/my")!
        let mutableURLRequest = NSMutableURLRequest(URL: URL)
        mutableURLRequest.HTTPMethod = "GET"

        var JSONSerializationError: NSError? = nil
        //mutableURLRequest.HTTPBody = urlEnd;
        mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")

        Alamofire.request(mutableURLRequest).responseJSON { (request, response, JSON, error) in
            if (error == nil) {
                self.addLobbies(JSON as! NSArray)
                success();
            } else {
                failure();
            }

        }
    }

    func addLobbies(data: NSArray) {
        lobbies = [];

        for (var i = 0; i<data.count; i++) {
            var lobby = data[i] as! NSDictionary;
            lobbies.append(LobbyData(data: lobby))

        }

    }


}