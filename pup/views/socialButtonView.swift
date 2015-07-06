//
// Created by Alex Hartwell on 6/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class SocialToggle: PlatformButtonToggle {

    var site: SocialConnect = .Facebook

    func setUpButton(service: SocialConnect, delegate: SimpleButtonDelegate) {
        println("button set up!!!!!")
        site = service;
        println(self.site)
        self.returnType = getServiceString();
        buttonDelegate = delegate

        self.setImage(UIImage(named: self.returnType), forState: .Normal);

        self.checked = false;

        addTarget(self, action: "buttonAction:", forControlEvents: UIControlEvents.TouchUpInside)
        //addTarget(self, action: "buttonPressed:", forControlEvents: UIControlEvents.TouchDown)
    }

    func getServiceString() -> String {
        switch(self.site) {
            case .Facebook:
                return "facebook"
            case .Reddit:
                return "reddit"
            case .Tumblr:
                return "tumblr"
            default:
                return "twitter"
        }
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
    var textView: UITextView = UITextView();

    override init(frame: CGRect) {
        super.init(frame: frame)
    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpButtons(delegate: SimpleButtonDelegate) {
        self.alpha = 0;

        buttons.append(SocialToggle())
        buttons[0].setUpButton(.Facebook, delegate: delegate)

        buttons.append(SocialToggle())
        buttons[1].setUpButton(.Twitter, delegate: delegate)

        buttons.append(SocialToggle())
        buttons[2].setUpButton(.Tumblr, delegate: delegate)

        buttons.append(SocialToggle())
        buttons[3].setUpButton(.Reddit, delegate: delegate)

        submitButton.setTitle("Share", forState: .Normal);
        submitButton.setTitleColor(UIColor.whiteColor(), forState: .Normal);
        submitButton.backgroundColor = UIColor(rgba: colors.orange);
        submitButton.addTarget(self, action: "sendButton", forControlEvents: UIControlEvents.TouchUpInside);

        textView.textAlignment = NSTextAlignment.Center
        textView.scrollEnabled = false;
        textView.userInteractionEnabled = false;
        textView.font = UIFont.systemFontOfSize(11)
        textView.text = "No one's here yet.\n" +
                "Why not invite some friends?"



        addViews();
        addConstraints()

        UIView.animateWithDuration(0.6, animations: {
            self.alpha = 1;
        })

    }

    func removeView() {
        self.removeFromSuperview();
    }

    func sendButton() {
        self.buttons[0].buttonDelegate?.touchUp(buttons, type: "send")
    }

    func addViews() {

        for (var i = 0; i<buttons.count; i++) {
            self.addSubview(buttons[i]);
        }

        self.addSubview(self.submitButton)
        self.addSubview(self.textView)
    }

    func addConstraints() {

        self.snp_makeConstraints {
            (make) -> Void in
            make.bottom.equalTo(self.superview!).offset(0)
            make.left.equalTo(self.superview!).offset(0)
            make.right.equalTo(self.superview!).offset(0)
            make.height.equalTo(((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165)) * 3)
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
            make.top.equalTo(self).offset((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
            make.height.equalTo((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
            make.width.equalTo(UIScreen.mainScreen().bounds.size.width / 4)
        }

        for (var i = 1; i<buttons.count; i++) {
            self.buttons[i].snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.buttons[i-1].snp_right).offset(0)
                make.top.equalTo(self).offset((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
                make.height.equalTo((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
                make.width.equalTo(UIScreen.mainScreen().bounds.size.width / 4)
            }
        }

        self.textView.snp_makeConstraints {
            (make) -> Void in
            make.bottom.equalTo(self.self.buttons[0].snp_top).offset(-UIConstants.halfVerticalPadding)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165))
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