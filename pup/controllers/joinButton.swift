//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class JoinPupButton: UIViewController, SimpleButtonDelegate {

    var parent: UIViewController?
    var joinButtonView: JoinPupButtonView = JoinPupButtonView();
    var registrationController: RegistrationController?;
    var onSuccessJoin: (() -> Void)?

    convenience init(parentController: UIViewController) {
        self.init()
        parent = parentController;
        registrationController = RegistrationController(parentController: self)
        registrationController?.setUpView()
        self.view = joinButtonView

        joinButtonView.setUpView(self);


    }


    func bounceView(amount: CGFloat) {
        if (amount <= 15) {
            joinButtonView.bounce(amount);
        }
    }


    func touchUp(button: NSObject, type: String) {
        println("touched")
       // parent?.view.addSubview(registrationController.view)
        println(parent!.view)




        registrationController?.addParentConstraints(parent!.view);
        registrationController?.onSuccessJoin = onSuccessJoin;


    }







    func removeRegistrationView() {
        println("removing registration view")
        registrationController?.registrationView.removeFromSuperview()
        self.joinButtonView.removeViews();
        self.view.removeFromSuperview()
    }

    func setUpConstraints() {
        self.joinButtonView.setUpConstraints()
    }





    func touchDown(button: NSObject, type: String) {

    }








}


