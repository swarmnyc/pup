//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation



class singleLobby {
    var data = lobbyData();


    init() {

    }

    func addDetailed(detailed: JSON) {

        addOwnerAndUsersToData(detailed);
        println(data.users.count)

    }



    func addOwnerAndUsersToData(detailed: JSON) {


        for (index: String, subJson: JSON) in detailed["users"] {
            //println(subJson)
            var isLeave = subJson["isLeave"].boolValue
            var isOwner = subJson["isOwner"].boolValue
            var id = subJson["id"].stringValue
            var name = subJson["name"].stringValue
            if (isOwner) {
                data.owner = singleLobbyUser(isLeave: isLeave, id: id, isOwner: isOwner, name: name)
            }
            data.users.append(singleLobbyUser(isLeave: isLeave,  id: id, isOwner: isOwner, name: name))
        }

    }



}

