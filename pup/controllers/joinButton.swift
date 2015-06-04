//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class JoinButton: UIViewController, SimpleButtonDelegate {

    var parent: UIViewController?
    var joinButtonView: JoinButtonView = JoinButtonView();


    convenience init(parentController: UIViewController) {
        self.init()
        parent = parentController;

        self.view = joinButtonView
        joinButtonView.setUpViews(parent!.view, joinDelegate: self);

        println(self.view.frame)
        println("self view")
    }

    func touchUp(button: NSObject, type: String) {


    }

    func touchDown(button: NSObject, type: String) {

    }

    func setNewView(newView: UIView) {
        joinButtonView.setUpViews(newView, joinDelegate: self);



    }






}


