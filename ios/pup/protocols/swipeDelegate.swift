//
// Created by Alex Hartwell on 6/1/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

protocol PanGestureDelegate: class {
    func swiped(sender: UIPanGestureRecognizer)


}

protocol SwipeGestureDelegate: class {
    func swiped(direction: String)


}