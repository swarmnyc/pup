//
// Created by Alex Hartwell on 5/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


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
        checkStored()

    }

    func setPage(page: String) {
        currentPage = page;
    }

    func checkStored() {

        if let loggedIn = localStorage.valueForKey("loggedIn") as? Bool { // if loggedIn exists in localstorage
            data.loggedIn = loggedIn                                      // and is true, let's update the user info with the correct access tokens and values
            if (loggedIn) {
                data.accessToken = localStorage.valueForKey("accessToken") as! String
                data.userId = localStorage.valueForKey("userId") as! String
                data.name = localStorage.valueForKey("name") as! String

            }

        }


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

    func register(email: String, password: String, username: String) {



    }

}

