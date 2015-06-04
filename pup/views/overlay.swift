//
// Created by Alex Hartwell on 6/4/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class Overlay: UIView {
        var overlayDelegate: OverlayDelegate?


        override func touchesBegan( touches: Set<NSObject>, withEvent event: UIEvent) {

        }

        override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
            overlayDelegate?.hideEverything();



        }

        func setDelegate(overlayDelegate: OverlayDelegate) {
            self.overlayDelegate = overlayDelegate;
            self.userInteractionEnabled = true;

            self.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.layer.opacity = 0;

        }





}