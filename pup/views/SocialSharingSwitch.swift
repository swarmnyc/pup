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
    var socialController: SocialConnector?;
    override init(frame: CGRect) {
        super.init(frame: frame)
    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpController(viewController: UIViewController) {
        socialController = SocialConnector(viewController: viewController);
    }

    func setUpSwitch(socialService: SocialConnect) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), {
            self.whichService = socialService;
            var text = ""
            switch(self.whichService) {
            case .Facebook:
                text = "facebook"
            case .Reddit:
                text = "reddit"
            case .Tumblr:
                text = "tumblr"
            default:
                text = "twitter"
            }
            self.label.text = text.capitalizeIt;

            if (currentUser.data.social[text]!) {
                self.active = true;
            }
            self.toggle.setOn(self.active, animated: false);
            self.socialController?.cancelOauth = self.authenticationCancelled;

            self.toggle.addTarget(self, action: "toggleSwitch", forControlEvents: UIControlEvents.ValueChanged);
            dispatch_async(dispatch_get_main_queue(), {
                () -> Void in
                self.addViews();
                self.setUpConstraints();
            });
        })

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
        if (self.toggle.on) {
            checkToggle();
        } else {
            uncheckToggle();
        }
    }

    func uncheckToggle() {
        self.toggle.setOn(false, animated: true);
        if (currentUser.data.social[self.label.text!.lowercaseString]!) {
            socialController?.deleteService(self.label.text!)
        }

    }

    func checkToggle() {
        self.toggle.setOn(true, animated: true)
        socialController?.setTypeAndAuthenticate(self.label.text!);


    }






}