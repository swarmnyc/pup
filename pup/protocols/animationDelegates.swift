//
// Created by Alex Hartwell on 8/6/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

protocol MainScreenAnimationDelegate: class {
    func bringLobbiesBack();
    func animateLobbyCellsAway((() -> Void)?);
}