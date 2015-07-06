//
// Created by Alex Hartwell on 7/1/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


protocol QuickBloxDelegate: class {
    var QBChatRoomId: String {get set}
    func handOffMessages(response: QBResponse, messages: NSArray, responcePage: QBResponsePage)
    func clearText()
    func addNewMessage(message: QBChatMessage)
    func handOffChats()

}