//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import Alamofire

struct userInfo {
    var loggedIn = false
    var accessToken = ""
    var userId = ""
    var name = ""
}


class User {

    let localStorage = NSUserDefaults.standardUserDefaults()
    var data: userInfo = userInfo();
    var currentPage = "Find A Game";
    init() {
        checkStoredData()

    }

    func setPage(page: String) {
        currentPage = page;
    }

    func checkStoredData() {

        if let loggedIn = localStorage.valueForKey("loggedIn") as? Bool { // if loggedIn exists in localstorage
            data.loggedIn = loggedIn                                      // and is true, let's update the user info with the correct access tokens and values
            if (loggedIn) {
                data.accessToken = localStorage.valueForKey("accessToken") as! String
                println(data.accessToken)
                println("the access token")
                data.userId = localStorage.valueForKey("userId") as! String
                data.name = localStorage.valueForKey("name") as! String

            }

        }


    }


    func setLocalStorage() {

        localStorage.setObject(self.data.accessToken,forKey: "accessToken")
        localStorage.setObject(self.data.userId,forKey: "userId")
        localStorage.setObject(self.data.name,forKey: "name")
        localStorage.setBool(self.data.loggedIn, forKey: "loggedIn")

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
           setLocalStorage()

        }


    }

    func validData(registrationData: (image: UIImage?, username: String, email: String)) -> Bool {

        var validEmail = false;
        var validUsername = true;

        if (isValidEmail(registrationData.email)) {
            validEmail = true;
        }

        if (validEmail && validUsername) {
            return true;
        } else {
            return false;
        }



    }



    func register(registrationData: (image: UIImage?, username: String, email: String), success: () -> Void) {

        println(registrationData);
        println("REGISTERING!");
        let parameters = [
                "email": registrationData.email,
                "password": "swarmnyc",
                "username": registrationData.username
        ]
        if (registrationData.image == nil) { //they didn't give us an image
            println("NO IMAGE!");

             Alamofire.request(.POST, "\(urls.register)", parameters: parameters).responseJSON { (request, response, responseJSON, error) in
                var resp = responseJSON as! NSDictionary
                var userData: NSDictionary = resp["data"] as! NSDictionary
                println(resp)
                println(userData)
                 if (resp["success"] as! Bool) {
                     println(userData["accessToken"]!)

                     self.data.loggedIn = true;
                     self.data.accessToken = userData["accessToken"]! as! String;
                     self.data.name = userData["userName"]! as! String;
                     self.data.userId = userData["id"]! as! String;
                    println(self.data)
                     self.setLocalStorage()

                     success()
                 }

             };

        } else { //we have an image
            println("HAS AN IMAGE!");

            var image:UIImage = registrationData.image!

            let imageData:NSData = UIImageJPEGRepresentation(image, 1.0)


            SRWebClient.POST(urls.register)
           // .data(image64String, fieldName:"portrait", data: parameters)
//            .send({(response:AnyObject!, status:Int) -> Void in
//                println(response)
//                //process success response
//            },failure:{(error:NSError!) -> Void in
//                println(error)
//                //process failure response
//            })

        }


    }

}

