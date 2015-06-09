//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class JoinButton: UIViewController, SimpleButtonDelegate {

    var parent: UIViewController?
    var joinButtonView: JoinButtonView = JoinButtonView();
    var registrationController: RegistrationController?;
    var onSuccessJoin: (() -> Void)?

    convenience init(parentController: UIViewController) {
        self.init()
        parent = parentController;
        registrationController = RegistrationController(parentController: self as UIViewController)
        registrationController?.setUpView()
        self.view = joinButtonView
        joinButtonView.setUpViews(parent!.view, joinDelegate: self);

        println(self.view.frame)
        println("self view")
    }

    func touchUp(button: NSObject, type: String) {
        println("touched")
       // parent?.view.addSubview(registrationController.view)
        println(parent!.view)




        registrationController?.addParentConstraints(parent!.view);
        registrationController?.onSuccessJoin = onSuccessJoin;


    }





    func removeRegistrationView() {
        registrationController?.view.removeFromSuperview()
        self.view.removeFromSuperview()
    }





    func touchDown(button: NSObject, type: String) {

    }

    func setNewView(newView: UIView) {
        joinButtonView.setUpViews(newView, joinDelegate: self);



    }






}


