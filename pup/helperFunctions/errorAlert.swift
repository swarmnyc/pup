//
// Created by Alex Hartwell on 6/11/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class SNYError {

    init(alertTitle: String, alertText: String) {
        let alert = UIAlertView()
        alert.title = alertTitle
        alert.message = alertText
        alert.addButtonWithTitle("Got It")
        alert.show()
    }

    init(alertTitle: String, alertText: String, networkRequest: Bool) {
        println("network request");
        if (networkRequest && (Reachability.isConnectedToNetwork() == false)) {
            let alert = UIAlertView()
            alert.title = "You are not connected to the internet"
            alert.message = "Please connect to wifi or a cellular network and try again."
            alert.addButtonWithTitle("Got It")
            alert.show()


        } else {
            let alert = UIAlertView()
            alert.title = alertTitle
            alert.message = alertText
            alert.addButtonWithTitle("Got It")
            alert.show()
        }


    }

}