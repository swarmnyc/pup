//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

protocol SimpleButtonDelegate: class {
    func touchDown(button: NSObject, type: String)
    func touchUp(button: NSObject, type: String)


}

protocol FABDelegate: class {
    func touchDown()
    func touchUp()
}