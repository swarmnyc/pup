//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire


class singleLobby {
    var data = LobbyData();
    var empty = false;

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
            if (user[i]["isOwner"] as! Bool) {
                data.owner = SingleLobbyUser(data: user[i] as! NSDictionary)
            } else {
                data.users.append(SingleLobbyUser(data: user[i] as! NSDictionary));
            }
        }
//        var user = detailed["users"] as! NSDictionary
//        for (var i = 0;)
//        for (index: String, subJson: JSON) in detailed["users"] {
//            //println(subJson)
//            var isLeave = subJson["isLeave"].boolValue
//            var isOwner = subJson["isOwner"].boolValue
//            var id = subJson["id"].stringValue
//            var name = subJson["userName"].stringValue
//            if (isOwner) {
//                data.owner = SingleLobbyUser(isLeave: isLeave, id: id, isOwner: isOwner, name: name)
//            }
//            data.users.append(SingleLobbyUser(isLeave: isLeave,  id: id, isOwner: isOwner, name: name))
//        }

    }



}

