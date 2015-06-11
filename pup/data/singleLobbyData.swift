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
    init() {

    }

//    func addDetailed(detailed: NSDictionary) {
//
//        addOwnerAndUsersToData(detailed);
//        println(data.users.count)
//        empty = isEmpty();
//    }

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
            println(error)
            println("---")
            println(JSON)
        }

    }


    func getDetailed(success: () -> Void, failure: () -> Void) {

        let requestUrl = NSURL(string: "\(urls.lobbies)\(data.id)")

        Alamofire.request(.GET, requestUrl!).responseJSON { (request, response, responseJSON, error) in
            var resp: NSDictionary = responseJSON! as! NSDictionary

            println(resp);

            self.addOwnerAndUsersToData(resp);
            success();

        };

    }

    func addOwnerAndUsersToData(detailed: NSDictionary) {
        var user = detailed["users"] as! NSArray

        for (var i = 0; i<user.count; i++) {

            if user[i]["userName"] as! String == currentUser.data.name {
                isMember = true;

            }

            if (user[i]["isOwner"] as! Bool) {
                data.owner = SingleLobbyUser(data: user[i] as! NSDictionary)
            } else {
                data.users.append(SingleLobbyUser(data: user[i] as! NSDictionary));
            }
        }


    }



}

