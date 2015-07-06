//
// Created by Alex Hartwell on 6/9/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class SettingsView: UIView {
    
    var logout: UIButton = UIButton();
    var containerView: UIView = UIView();
    var scrollView: UIScrollView = UIScrollView()

    var profileView: UIView = UIView();
    var profilePicImg: UIImageView = UIImageView();
    var cameraIcon: UIImageView = UIImageView();
    var usernameView: UITextView = UITextView();
    var integrationDisclaimer: UITextView = UITextView();

    var socialButtons: Array<SocialSharingSwitch> = [];

    override init(frame: CGRect) {
        super.init(frame: frame)


        initView();

    }


    func initView() {



        backgroundColor=UIColor.whiteColor()

        clipsToBounds = true;
        if (currentUser.loggedIn()) {
            var url = NSURL(string: currentUser.data.picture.getPUPUrl())

            profilePicImg.clipsToBounds = true;
            profilePicImg.contentMode = UIViewContentMode.ScaleAspectFill;
            profilePicImg.frame.size = CGSizeMake(93, 93);
            profilePicImg.userInteractionEnabled = true;

            profilePicImg.backgroundColor = UIColor(rgba: colors.orange)
            profilePicImg.alpha = 0;
            self.profilePicImg.hnk_setImageFromURL(url!, placeholder: nil, format: nil, failure: nil, success: {
                (image) -> Void in
                self.profilePicImg.image = image;
                UIView.animateWithDuration(0.3, animations: {
                    () -> Void in
                    self.profilePicImg.alpha = 1;
                });

            })

            cameraIcon.image = UIImage(named: "camera");
            cameraIcon.userInteractionEnabled = true;

            usernameView.text = currentUser.data.name;
            usernameView.userInteractionEnabled = false;

            integrationDisclaimer.text = "We'll be adding integrations for additional systems..."
            integrationDisclaimer.userInteractionEnabled = false;

            socialButtons.append(SocialSharingSwitch())
            socialButtons.append(SocialSharingSwitch())
            socialButtons.append(SocialSharingSwitch())
            socialButtons.append(SocialSharingSwitch())
        }

        setUpView();
        setUpConstraints();
        if (currentUser.loggedIn()) {
            socialButtons[0].setUpSwitch(SocialConnect.Facebook)
            socialButtons[1].setUpSwitch(SocialConnect.Tumblr)
            socialButtons[2].setUpSwitch(SocialConnect.Twitter)
            socialButtons[3].setUpSwitch(SocialConnect.Reddit)
        }

    }


    func updateProfilePicture() {
        self.profilePicImg.alpha = 0;
        var url = NSURL(string: currentUser.data.picture.getPUPUrl())
        println(url)
        self.profilePicImg.hnk_setImageFromURL(url!, placeholder: nil, format: nil, failure: nil, success: {
            (image) -> Void in
            self.profilePicImg.image = image;
            UIView.animateWithDuration(0.3, animations: {
                () -> Void in
                self.profilePicImg.alpha = 1;
            });

        })
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

        var imageTapper = UITapGestureRecognizer(target: delegate as! SettingsController, action: "imageTapped");
        self.profilePicImg.addGestureRecognizer(imageTapper);

        var imageTapper2 = UITapGestureRecognizer(target: delegate as! SettingsController, action: "imageTapped");
        self.cameraIcon.addGestureRecognizer(imageTapper2);
        //logout.titleLabel!.font = titleLabel!.font.fontWithSize(11)
    }

    func setUpView() {
        if (currentUser.loggedIn()) {
            self.profileView.addSubview(profilePicImg)
            self.profileView.addSubview(cameraIcon)
            self.profileView.addSubview(usernameView)
            self.profileView.addSubview(integrationDisclaimer)
            self.containerView.addSubview(profileView)

            self.containerView.addSubview(socialButtons[0])
            self.containerView.addSubview(socialButtons[1])
            self.containerView.addSubview(socialButtons[2])
            self.containerView.addSubview(socialButtons[3])
        }
        self.containerView.addSubview(logout)
        self.scrollView.addSubview(containerView)
        self.addSubview(self.scrollView)
    }


    func removeAllViewsAndRemake() {

        var allViews = self.subviews;

        for (var i = 0; i<allViews.count; i++) {
            allViews[i].removeFromSuperview();
        }

        initView();

    }

    func setUpConstraints() {

        scrollView.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self)
            make.right.equalTo(self)
            make.bottom.equalTo(self)
            make.top.equalTo(self)
        }

        containerView.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self)
            make.right.equalTo(self)
            make.top.equalTo(self)
            make.bottom.equalTo(self)
            make.height.equalTo(1000)
        }

        if (currentUser.loggedIn()) {
            logout.snp_makeConstraints {
                (make) -> Void in
                make.bottom.equalTo(self.containerView).offset(-nav!.tabBar.frame.height)
                make.left.equalTo(self.containerView).offset(0)
                make.right.equalTo(self.containerView).offset(0)
                make.height.equalTo(100)
            }


            socialButtons[0].snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self.profileView.snp_bottom).offset(UIConstants.verticalPadding)
                make.left.equalTo(self.containerView)
                make.right.equalTo(self.containerView)
                make.height.equalTo(35)
            }
            socialButtons[1].snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self.socialButtons[0].snp_bottom).offset(UIConstants.verticalPadding)
                make.left.equalTo(self.containerView)
                make.right.equalTo(self.containerView)
                make.height.equalTo(35)
            }
            socialButtons[2].snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self.socialButtons[1].snp_bottom).offset(UIConstants.verticalPadding)
                make.left.equalTo(self.containerView)
                make.right.equalTo(self.containerView)
                make.height.equalTo(35)
            }
            socialButtons[3].snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self.socialButtons[2].snp_bottom).offset(UIConstants.verticalPadding)
                make.left.equalTo(self.containerView)
                make.right.equalTo(self.containerView)
                make.height.equalTo(35)
            }

            profileView.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.containerView).offset(0)
                make.top.equalTo(self.containerView).offset(90)
                make.right.equalTo(self.containerView).offset(0)
                make.height.equalTo(145)
            }

            profilePicImg.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profileView).offset(UIConstants.horizontalPadding)
                make.top.equalTo(self.profileView).offset(UIConstants.verticalPadding)
                make.width.equalTo(UIScreen.mainScreen().bounds.size.width / 4)
                make.height.equalTo(UIScreen.mainScreen().bounds.size.width / 4);
            }

            cameraIcon.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicImg.snp_left).offset(2)
                make.bottom.equalTo(self.profilePicImg.snp_bottom).offset(-2)
                make.width.equalTo(UIScreen.mainScreen().bounds.size.width / 12)
                make.height.equalTo(UIScreen.mainScreen().bounds.size.width / 12)
            }

            usernameView.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicImg.snp_right).offset(UIConstants.horizontalPadding)
                make.right.equalTo(self.profileView).offset(0)
                make.top.equalTo(self.profilePicImg.snp_top).offset(0)
                make.height.equalTo(20)
            }

            integrationDisclaimer.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicImg.snp_right).offset(UIConstants.horizontalPadding)
                make.right.equalTo(self.profileView).offset(0)
                make.top.equalTo(self.usernameView.snp_bottom).offset(UIConstants.verticalPadding)
                make.bottom.equalTo(self.profilePicImg.snp_bottom);
            }

        }
    }



}