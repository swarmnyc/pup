//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class JoinPupButton: UIViewController, SimpleButtonDelegate {

    var joinButtonView: JoinPupButtonView = JoinPupButtonView();
    var registrationController: RegistrationController?;
    var onSuccessJoin: (() -> Void)?
    var aboveTabBar: Bool = false;
    convenience init(aboveTabBar: Bool) {
        self.init()
        self.aboveTabBar = aboveTabBar;
        registrationController = RegistrationController(parentController: self)
        registrationController?.setUpView()
        self.view = joinButtonView

        joinButtonView.setUpView(self, aboveTabBar: aboveTabBar);


    }


    func bounceView(amount: CGFloat) {
        if (amount <= 15) {
            joinButtonView.bounce(amount);
        }
    }

    func setUpView(parentView: UIView) {
        self.joinButtonView.addAndConstrain(parentView);
    }

    func addToAppView() {
        joinButtonView.addToAppView()
        joinButtonView.setUpConstraints(aboveTabBar);
    }

    func shortenView(notification: NSNotification) {

        var keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.CGRectValue()
        var keyboardHeight = keyboardSize!.height;

    }




    func touchUp(button: NSObject, type: String) {
        println("touched")
       // parent?.view.addSubview(registrationController.view)




        registrationController?.addParentConstraints();
        registrationController?.onSuccessJoin = onSuccessJoin;


    }







    func removeRegistrationView() {
        println("removing registration view")
        registrationController?.registrationView.removeFromSuperview()
        self.joinButtonView.removeViews();
        self.view.removeFromSuperview()
    }

    func setUpConstraints() {
        self.joinButtonView.setUpConstraints(self.aboveTabBar)
    }





    func touchDown(button: NSObject, type: String) {

    }








}


