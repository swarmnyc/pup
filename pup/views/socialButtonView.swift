//
// Created by Alex Hartwell on 6/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class SocialToggle: PlatformButtonToggle {



    override func setUpButton(service: String, delegate: SimpleButtonDelegate) {
        self.returnType = service;
        buttonDelegate = delegate

        self.setImage(UIImage(named: service), forState: .Normal);

        self.checked = false;

        addTarget(self, action: "buttonAction:", forControlEvents: UIControlEvents.TouchUpInside)
        //addTarget(self, action: "buttonPressed:", forControlEvents: UIControlEvents.TouchDown)
    }

    override func setSelectionStyle() {
        if (checked) {

            self.setImage(UIImage(named: self.returnType + "_select"), forState: .Normal);

        } else {

            self.setImage(UIImage(named: self.returnType), forState: .Normal);

        }


    }

    func cancelOauth() {
        self.checked = false;
        setSelectionStyle();
    }



}


class SocialButtonsView: UIView {

    var buttons: Array<SocialToggle> = []
    //var submitButton = UIButton()
    var submitButton: UIButton = UIButton();
    override init(frame: CGRect) {
        super.init(frame: frame)
    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpButtons(delegate: SimpleButtonDelegate) {
        buttons.append(SocialToggle())
        buttons[0].setUpButton("facebook", delegate: delegate)

        buttons.append(SocialToggle())
        buttons[1].setUpButton("twitter", delegate: delegate)

        buttons.append(SocialToggle())
        buttons[2].setUpButton("tumblr", delegate: delegate)

        buttons.append(SocialToggle())
        buttons[3].setUpButton("reddit", delegate: delegate)

        submitButton.setTitle("Share", forState: .Normal);
        submitButton.setTitleColor(UIColor.whiteColor(), forState: .Normal);
        submitButton.backgroundColor = UIColor(rgba: colors.orange);
        submitButton.addTarget(self, action: "sendButton", forControlEvents: UIControlEvents.TouchUpInside);

        addViews();
        addConstraints()

    }

    func sendButton() {
        self.buttons[0].buttonDelegate?.touchUp(buttons, type: "send")
    }

    func addViews() {

        for (var i = 0; i<buttons.count; i++) {
            self.addSubview(buttons[i]);
        }

        self.addSubview(self.submitButton)

    }

    func addConstraints() {

        self.snp_makeConstraints {
            (make) -> Void in
            make.bottom.equalTo(self.superview!).offset(-UIConstants.buttonHeight)
            make.left.equalTo(self.superview!).offset(0)
            make.right.equalTo(self.superview!).offset(0)
            make.height.equalTo(((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165)) * 2)
        }

        self.submitButton.snp_makeConstraints {
            (make) -> Void in
            make.bottom.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
        }

        self.buttons[0].snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.height.equalTo((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
            make.width.equalTo(UIScreen.mainScreen().bounds.size.width / 4)
        }

        for (var i = 1; i<buttons.count; i++) {
            self.buttons[i].snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.buttons[i-1].snp_right).offset(0)
                make.top.equalTo(self).offset(0)
                make.height.equalTo((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
                make.width.equalTo(UIScreen.mainScreen().bounds.size.width / 4)
            }
        }
    }


    func cancelled(service: String) {
        for (var i = 0; i<self.buttons.count; i++) {
            if (self.buttons[i].returnType == service) {
                    self.buttons[i].cancelOauth();
            }
        }
    }


}