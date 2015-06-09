//
// Created by Alex Hartwell on 6/9/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class SettingsView: UIView {
    
    var logout: UIButton = UIButton();


    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor=UIColor.whiteColor()

        clipsToBounds = true;

        setUpView();
        setUpConstraints();


    }





    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setDelegates(delegate: UIViewController) {
        logout.addTarget(delegate as! SettingsController, action: "buttonAction:", forControlEvents: UIControlEvents.TouchUpInside)
        logout.setTitle("Logout", forState: .Normal);
        logout.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        logout.setTitleColor(UIColor.whiteColor(), forState: .Selected)
        logout.layer.borderWidth = 0.5
        logout.layer.borderColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2).CGColor


        //logout.titleLabel!.font = titleLabel!.font.fontWithSize(11)
    }

    func setUpView() {
        self.addSubview(logout)
    }

    func setUpConstraints() {
        if (currentUser.loggedIn()) {
            logout.snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self).offset(225)
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.height.equalTo(100)
            }
        }
    }



}