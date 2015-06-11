//
// Created by Alex Hartwell on 6/11/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class Error {

    init(alertTitle: String, alertText: String) {
        let alert = UIAlertView()
        alert.title = alertTitle
        alert.message = alertText
        alert.addButtonWithTitle("Got It")
        alert.show()
    }

}