//
// Created by Alex Hartwell on 6/15/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import Haneke



class MessageCell: UITableViewCell {


    var profilePicture: UIImageView = UIImageView();
    var userName: UILabel = UILabel();
    var message:UITextView = UITextView();
    var whiteBackground: UIView = UIView();
    var firstTime = true;
    var delay: Double = 0;
    var isSystemMessage = false;

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        // println(style);
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }


    func isCurrentUser() -> Bool {
        if let theUserName = self.userName.text {
            if (theUserName == currentUser.data.name) {
                return true;
        }
    }

        return false;

        }

    func setTheAnimationDelay(delay: Double) {
        self.delay = delay;
    }

    func setUpCell(item: Message) {


        var image = UIImage(named: "iconWithText");
        self.profilePicture.clipsToBounds = true;
        self.profilePicture.image = nil;
        self.profilePicture.layer.cornerRadius = 20;
        if (item.picture != "") {
            var url = NSURL(string: item.picture.getPUPUrl())
            self.profilePicture.frame = CGRectMake(0,0,50,50);
            self.profilePicture.hnk_setImageFromURL(url!)

        } else {
            self.profilePicture.image = image;

        }


        whiteBackground.backgroundColor = UIColor.whiteColor();


        message.text = item.message;
        message.font = UIFont(name: "AvenirNext-Regular", size: 13.0)
        message.userInteractionEnabled = false;
        message.textContainerInset = UIEdgeInsetsZero
        message.textContainer.lineFragmentPadding = 0;
        message.backgroundColor = UIColor.clearColor();
        message.textColor = UIColor(rgba: colors.midGray).darkerColor(0.3)
        self.message.textAlignment = NSTextAlignment.Left

        if (item.username == "system message") {
            isSystemMessage = true;
            self.profilePicture.layer.opacity = 0;
            self.userName.layer.opacity = 0;
            self.message.textColor = UIColor(rgba: colors.orange);
            self.message.textAlignment = NSTextAlignment.Center
        } else {
            isSystemMessage = false;
            self.profilePicture.layer.opacity = 1;
            self.userName.layer.opacity = 1;

            userName.text = item.username;
            userName.font = UIFont(name: "AvenirNext-Regular", size: 12.0)
            userName.backgroundColor = UIColor.clearColor();
            userName.textColor = UIColor(rgba: colors.mainGrey);
            if (item.username == currentUser.data.name) {
                self.backgroundColor = UIColor(rgba: colors.lightGray)
            }
        }

        if (message.superview == nil){

            addViews();

        }
        addConstraints();


        if (delay != 0) {
            if (firstTime) {
                var trans = CGAffineTransformMakeTranslation(UIScreen.mainScreen().bounds.width, 0);
                self.transform = trans;

                firstTime = false;

            }

            UIView.animateWithDuration(delay, animations: {
                var trans = CGAffineTransformMakeTranslation(0, 0);
                self.transform = trans;
            })
        } else {

        }


    }



    func addViews() {
        addSubview(whiteBackground)
        addSubview(profilePicture)
        addSubview(userName)
        addSubview(message)

    }

    func addConstraints() {
        profilePicture.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self).offset(UIConstants.horizontalPadding)
            make.height.equalTo(38)
            make.width.equalTo(38);

        }

        whiteBackground.snp_remakeConstraints {
            (make) -> Void in
            make.right.equalTo(self.snp_left).offset(0);
            make.top.equalTo(self);
            make.bottom.equalTo(self);
            make.width.equalTo(UIScreen.mainScreen().bounds.width);
        }

        if (isSystemMessage == false) {
            userName.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicture.snp_right).offset(UIConstants.horizontalPadding)
                make.top.equalTo(self).offset(UIConstants.horizontalPadding)
                make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 2);
                make.height.equalTo(20)
            }

            message.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicture.snp_right).offset(UIConstants.horizontalPadding)
                make.bottom.equalTo(self).offset(-UIConstants.horizontalPadding)
                make.right.equalTo(self).offset(-UIConstants.horizontalPadding);
                make.top.equalTo(self.userName.snp_bottom).offset(-UIConstants.halfVerticalPadding * 0.5)
            }
        } else {
            message.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self).offset(UIConstants.horizontalPadding * 2.0)
                make.top.equalTo(self).offset(0.5 * self.frame.height - 8);
                make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 2.0);
                make.bottom.equalTo(self).offset(0)
            }
        }


        self.layoutIfNeeded();

    }




    override var layoutMargins: UIEdgeInsets {  //make cell dividers extend full width
        get { return UIEdgeInsetsZero }
        set(newVal) {}
    }

    override func setSelected(selected: Bool, animated: Bool) {

        self.backgroundColor = UIColor.whiteColor()
        // Configure the view for the selected state
    }


}