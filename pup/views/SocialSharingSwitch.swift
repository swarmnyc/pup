//
// Created by Alex Hartwell on 6/24/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SocialSharingSwitch: UIView {

    var whichService = SocialConnect.Facebook
    var toggle: UISwitch = UISwitch();
    var label = UILabel();
    var active = false;
    var socialController = SocialConnector();
    override init(frame: CGRect) {
        super.init(frame: frame)
    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpSwitch(socialService: SocialConnect) {
        whichService = socialService;
        var text = ""
        switch(whichService) {
            case .Facebook:
                 text = "facebook"
            case .Reddit:
                 text = "reddit"
            case .Tumblr:
                 text = "tumblr"
            default:
                text = "twitter"
        }
        self.label.text = text;
        println(currentUser.data.social[text])
        println("current user social ^ " + text)
        if (currentUser.data.social[text]!) {
            active = true;
        }
        toggle.setOn(active, animated: false);
        socialController.cancelOauth = authenticationCancelled;

        self.toggle.addTarget(self, action: "toggleSwitch", forControlEvents: UIControlEvents.ValueChanged);

        addViews();
        setUpConstraints();

    }


    func addViews() {
        self.addSubview(self.toggle);
        self.addSubview(self.label);
    }

    func setUpConstraints() {
        self.toggle.snp_makeConstraints {
            (make) -> Void in
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
            make.width.equalTo(70)
        }

        self.label.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
            make.right.equalTo(self.toggle.snp_left).offset(-UIConstants.horizontalPadding)
        }

    }

    func authenticationCancelled(site: String) {
        uncheckToggle();
    }

    func toggleSwitch() {
        println("toggling")
        if (self.toggle.on) {
            checkToggle();
        } else {
            uncheckToggle();
        }
    }

    func uncheckToggle() {
        self.toggle.setOn(false, animated: true);
        if (currentUser.data.social[self.label.text!]!) {
            socialController.deleteService(self.label.text!)
        }

    }

    func checkToggle() {
        self.toggle.setOn(true, animated: true)
        socialController.setTypeAndAuthenticate(self.label.text!);


    }






}