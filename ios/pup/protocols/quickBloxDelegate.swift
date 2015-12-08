//
// Created by Alex Hartwell on 7/1/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


protocol QuickBloxDelegate: class {
    var QBChatRoomId: String {get set}
    func handOffMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage) //hand off messages initially
    func clearText() //clear the text after a message is successfully sent
    func addNewMessage(message: QBChatMessage) //add newly recieved messages to the lobby object


}