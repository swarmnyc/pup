//
// Created by Alex Hartwell on 6/11/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import Alamofire

class SNYError: NSObject, UIAlertViewDelegate {
    let alert = UIAlertView()
    var messageText:String?
    var flaggedUser:String?
    var lobbyID: String?
    init(alertTitle: String, alertText: String) {
        super.init();

        alert.title = alertTitle
        alert.message = alertText
        alert.addButtonWithTitle("Got It")
        alert.show()
    }

    init(alertTitle: String, alertText: String, networkRequest: Bool) {
        super.init();

        println("network request");
        if (networkRequest && (Reachability.isConnectedToNetwork() == false)) {
            alert.title = "Seems like you've travelled back in time."
            alert.message = "There is no internet! Please return to the modern era or turn on data to use PUP."
            alert.addButtonWithTitle("Got It")
            alert.show()


        } else {
            alert.title = alertTitle
            alert.message = alertText
            alert.addButtonWithTitle("Got It")
            alert.show()
        }


    }
    
    init(alertTitle: String, alertText: String, alertStyle: UIAlertViewStyle) {
        super.init();
        alert.title = alertTitle
        alert.message = alertText
        alert.alertViewStyle = alertStyle
        alert.addButtonWithTitle("Cancel");

        alert.addButtonWithTitle("Flag Message")
        alert.delegate = self;

        
    }
    
    func showIt(message: String, user: String) {
        //slack token
//        xoxb-10317959939-aLRNvRdBAm92bj4OzkeefCAN
        self.alert.textFieldAtIndex(0)!.text = "";
        messageText = message;
        flaggedUser = user;
        alert.show()

    }
    
    func alertView(alertView: UIAlertView,
        clickedButtonAtIndex buttonIndex: Int) {
                println(buttonIndex);
                println("button index");
            if (self.flaggedUser != nil && self.lobbyID != nil && self.messageText != nil) {
                println(self.lobbyID!);
                println(self.flaggedUser!);
                println(self.messageText!);
                println(self.alert.textFieldAtIndex(0)!.text);
                
                var user = self.flaggedUser!;
                var message = self.messageText!;
                var lobbyId = self.lobbyID!;
                var userText = self.alert.textFieldAtIndex(0)!.text;
                
                SNYSlack.postToSlack(user, message: message, lobbyId: lobbyId, userText: userText);
                
                
            }
            
            //send message to slack
            
    }
   

}


public class SNYSlack {
    
    class func postToSlack(userName: String, message: String, lobbyId: String, userText: String) -> Void{
        println("sending message");
        var messageToPost = "Citizen's Arrest!!! '" + currentUser.data.name + "' has flagged '" + userName + "' for the message: '" + message + "' in the lobby: '" + lobbyId + "'";
        messageToPost += " their complaint is '" + userText + "'";
        
        
        let parameters = [
            "token": "xoxb-10317959939-aLRNvRdBAm92bj4OzkeefCAN",
            "channel": "#pup",
            "text": messageToPost,
            "username": "PUP-Police",
            "icon_url": "https://upload.wikimedia.org/wikipedia/en/b/b1/Police_man_ganson.jpg"
        ]
        
        Alamofire.request(.POST, "https://slack.com/api/chat.postMessage", parameters: parameters)
            .response { request, response, data, error in
                println(request)
                println("---");
                println(response)
                println("---");
                println(error)
        }
    }
}