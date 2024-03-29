//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
import SwiftLoader

struct userInfo {
    var loggedIn = false
    var accessToken = ""
    var userId = ""
    var name = ""
    var tags = [["": ""]]
    var QBChatId = ""
    var picture: String {
        get {
            println("ttt " + picURL);
            if (picURL == "") {
                return "no image";
            }
            return picURL + "?t=" + Float(arc4random_uniform(2000)).description;
        }
        set(newVal) {
            picURL = newVal
        }
    }
    var picURL = "";
    var social: [String:Bool] = ["facebook": false, "twitter": false, "tumblr": false, "reddit": false];

    var fbAccessToken = "";
    var fbUserId = "";
}

class User {
//    var joinedLobbies: NSDictionary = ["": false];
    var joinedLobbies: Dictionary<String, Bool> = Dictionary<String, Bool>();
    let localStorage = NSUserDefaults.standardUserDefaults()
    var data: userInfo = userInfo();
    var currentPage = "Find A Game";
    var recentLoginChange = false;
    var socialConnect = SocialConnector(viewController: nil);
    init() {
        checkStoredData()

    }

    func setPage(page: String) {
        currentPage = page;
    }

    func checkStoredData() {
        if let loggedIn = localStorage.valueForKey("loggedIn") as? Bool {
            // if loggedIn exists in localstorage
            data.loggedIn = loggedIn                                      // and is true, let's update the user info with the correct access tokens and values
            if (loggedIn) {
                data.accessToken = localStorage.valueForKey("accessToken") as! String
                println(data.accessToken)
                println("the access token")
                data.userId = localStorage.valueForKey("userId") as! String
                data.name = localStorage.valueForKey("name") as! String
                data.QBChatId = localStorage.valueForKey("QBChatId") as! String
                data.picture = localStorage.valueForKey("picture") as! String
                data.social = localStorage.valueForKey("social") as! [String:Bool]
//                data.fbAccessToken = localStorage.valueForKey("fbAccessToken") as! String
//                data.fbUserId = localStorage.valueForKey("fbUserId") as! String
            }

        }


    }


    func setLocalStorage() {

        localStorage.setObject(self.data.accessToken, forKey: "accessToken")
        localStorage.setObject(self.data.userId, forKey: "userId")
        localStorage.setObject(self.data.name, forKey: "name")
        localStorage.setObject(self.data.QBChatId, forKey: "QBChatId")
        localStorage.setObject(self.data.picture, forKey: "picture")
        localStorage.setBool(self.data.loggedIn, forKey: "loggedIn")
        localStorage.setObject(self.data.social, forKey: "social")
        localStorage.setObject(self.data.fbAccessToken, forKey: "fbAccessToken")
        localStorage.setObject(self.data.fbUserId, forKey: "fbUserId")

    }

    func getSelfAsSingleLobbyUser() -> SingleLobbyUser {

        var data: NSDictionary = ["isLeave": false, "isOwner": false, "id": self.data.userId, "userName": self.data.name, "portraitUrl": self.data.picture];
        return SingleLobbyUser(data: data);

    }

    func setNewToken(fbData: (String, String, NSDate)) {
        socialConnect.sendFacebookDataToPup(fbData)
    }


    func loggedIn() -> Bool {

        if (data.loggedIn) {
            return true
        } else {
            return false;
        }

    }


    func login(email: String, password: String) {


    }

    func logout() {
        if (currentUser.loggedIn()) {
            self.data.accessToken = ""
            self.data.userId = ""
            self.data.loggedIn = false
            self.data.name = ""
            self.data.picture = ""
            self.data.social = ["facebook": false, "twitter": false, "tumblr": false, "reddit": false];
            self.data.QBChatId = "";
            setLocalStorage()
            recentLoginChange = true;
        }


    }

    func validData(registrationData: (image:UIImage?, username:String, email:String)) -> Bool {

        var validEmail = false;
        var validUsername = false;

        if (isValidEmail(registrationData.email)) {
            validEmail = true;
        }

        if (registrationData.username.replace(" ", replacement: "") != "") {
            validUsername = true;
        }

        if (validEmail && validUsername) {

           return true;


        } else {
            return false;
        }


    }


    func saveData(userData: NSDictionary) {
        println(userData["accessToken"]!)

        self.data.loggedIn = true;
        self.data.accessToken = userData["accessToken"]! as! String;
        var name = userData["userName"]! as! NSString;
        self.data.name = name as String
        println(self.data.name + "the NAME")
        self.data.userId = userData["id"]! as! String;
        if (userData["portraitUrl"] != nil) {
            self.data.picture = userData["portraitUrl"]! as! String;
        } else {
            self.data.picture = ""

        }

        self.data.tags = userData["tags"] as! Array<Dictionary<String, String>>
        self.data.QBChatId = self.data.tags[0]["value"] as String!
        println(self.data)
        self.setLocalStorage()
    }

    func enableMyGames() {
        var myGames = nav!.tabBar.items?[1] as! UITabBarItem
        myGames.enabled = true;

        var settingsControl = (nav!.viewControllers?[3] as! UINavigationController).viewControllers[0] as! SettingsController;
        settingsControl.settingsView?.initView(settingsControl)
        settingsControl.settingsView?.setDelegates(settingsControl)


    }

    func updatePortrait(image: UIImage, success: () -> Void, failure: () -> Void) {

        var img: UIImage = image

        let imageData: NSData = NSData(data: UIImageJPEGRepresentation(img, 1.0))

        SRWebClient.POST(appURLS().updatePortrait)
        .headers(["Authorization": "Bearer " + self.data.accessToken])
        .data(imageData, fieldName: "portrait", data: ["": ""])
        .send({
            (response: AnyObject!, status: Int) -> Void in
            println(response);
            self.data.picture = response as! String;
            self.setLocalStorage();
            success();
            //process success response
        }, failure: {
            (error: NSError!) -> Void in
            failure();
            //process failure response
        })

    }



    func register(registrationData: (image:UIImage?, username:String, email:String), success: () -> Void, failure: () -> Void) {

        println(registrationData);
        println("REGISTERING!");
        let parameters = [
                "email": registrationData.email,
                "password": "swarmnyc",
                "username": registrationData.username
        ]
        if (registrationData.image == nil) {
            //they didn't give us an image
            println("NO IMAGE!");

            Alamofire.request(.POST, "\(urls.register)", parameters: parameters).responseJSON {
                (request, response, responseJSON, error) in
                println(responseJSON);
                println(error);
                println((responseJSON as! NSDictionary)["success"]);
                if ((error != nil) || ((responseJSON as! NSDictionary)["success"] == nil)) {
                    var alert = SNYError(alertTitle: "Whoops", alertText: "Sorry, there was an error while registering you", networkRequest: true)
                    failure();
                } else {
                    println(responseJSON)
                    var resp = responseJSON as! NSDictionary
                    println(resp)

                    if (resp["success"] as! Bool == false) {
                        var alert = SNYError(alertTitle: "Whoops", alertText: "There's already a user with that email or username, try another.", networkRequest: true)
                        failure();
                    } else {
                        var userData: NSDictionary = resp["data"] as! NSDictionary
                        println(error)
                        println("error^")
                        if (resp["success"] as! Bool) {
                            println(userData["accessToken"]!)

                            self.saveData(userData)
                            self.enableMyGames()
                            success()
                        }
                    }

                }

            };

        } else {
            //we have an image
            println("HAS AN IMAGE!");

            var image: UIImage = registrationData.image!

            let imageData: NSData = NSData(data: UIImageJPEGRepresentation(image, 1.0))

            SRWebClient.POST(appURLS().register)
            .data(imageData, fieldName: "portrait", data: parameters)
            .send({
                (response: AnyObject!, status: Int) -> Void in
                println("success");
                println(response);
                if ((response as? Dictionary<String, AnyObject>) != nil) {
                    var resp = response as! Dictionary<String, AnyObject>
                    println(resp)

                    if ((resp["success"] as? Bool) != nil) {
                        if (resp["success"] as! Bool) {
                            var userData = resp["data"] as! Dictionary<String, AnyObject>
                            println(userData)
                            self.saveData(userData)
                            self.enableMyGames()
                            self.recentLoginChange = true;

                            success()

                        } else {
                            failure();
                            var alert = SNYError(alertTitle: "Whoops", alertText: "There's already a user with that email or username, try another.", networkRequest: true)

                        }
                    }
                } else {
                    failure();
                    var alert = SNYError(alertTitle: "Whoops", alertText: "Sorry, there was an error while registering you", networkRequest: true)
                }

            }, failure: {
                (error: NSError!) -> Void in
                println("failure")
                failure();
                var alert = SNYError(alertTitle: "Whoops", alertText: "Sorry, there was an error while registering you", networkRequest: true)
                //process failure response
            })


        }


    }

}

