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
    var profilePicImg: LoaderImageView = LoaderImageView();
    var cameraIcon: UIImageView = UIImageView();
    var usernameView: UITextView = UITextView();
    var integrationDisclaimer: UITextView = UITextView();

    var socialButtons: Array<SocialSharingSwitch> = [];

    var parentViewController: UIViewController?

    var TOSLink: UIButton = UIButton();
    var openTOS: (() -> Void)?;
    var bounceImage: Bool = true;

    override init(frame: CGRect) {
        super.init(frame: frame)




    }


    func initView(parentController: UIViewController) {
        println("init view");

        self.parentViewController = parentController;

        backgroundColor=UIColor.whiteColor()

        clipsToBounds = true;
            
        
        
            profilePicImg.clipsToBounds = true;
            profilePicImg.contentMode = UIViewContentMode.ScaleAspectFill;
            profilePicImg.frame.size = CGSizeMake(93, 93);
            profilePicImg.userInteractionEnabled = true;

            profilePicImg.layer.cornerRadius = (UIScreen.mainScreen().bounds.size.width / 4) / 2;
            profilePicImg.layer.masksToBounds = true;

            profilePicImg.backgroundColor = UIColor(rgba: colors.lightGray)
            profilePicImg.alpha = 0;
            println("url!")

            var url: NSURL? = nil;
            url = NSURL(string: currentUser.data.picture.getPUPUrl())
            println(url);

            if (url != nil) {
                self.profilePicImg.sd_setImageWithURL(url!, placeholderImage: nil, options: SDWebImageOptions.RefreshCached, completed: {
                    (image, error, cacheType, imageUrl) -> Void in
                    println(image);
                    println("^^^ the image")
                    if (image != nil) {
                        self.bounceImage = false;
                        self.profilePicImg.image = image;
                    } else {
                        self.profilePicImg.image = UIImage(named: "profilePicDefault");
                    }
                    UIView.animateWithDuration(0.3, animations: {
                        () -> Void in
                        self.profilePicImg.alpha = 1;
                    });
                });
            } else {
                self.profilePicImg.image = UIImage(named: "profilePicDefault")
            }
//                UIView.animateWithDuration(0.3, animations: {
//                    () -> Void in
//                    self.profilePicImg.alpha = 1;
//                });

            cameraIcon.image = UIImage(named: "camera");
            cameraIcon.userInteractionEnabled = true;

            usernameView.text = currentUser.data.name;
            usernameView.font = UIConstants.titleFont;
            usernameView.userInteractionEnabled = false;

            integrationDisclaimer.text = "We'll be adding integrations for additional systems..."
            integrationDisclaimer.font = UIConstants.paragraphType;
            integrationDisclaimer.userInteractionEnabled = false;
            integrationDisclaimer.editable = false;

            self.socialButtons.append(SocialSharingSwitch())
            self.socialButtons.append(SocialSharingSwitch())
            self.socialButtons.append(SocialSharingSwitch())
            self.socialButtons.append(SocialSharingSwitch())

            self.setUpView();
            self.setUpConstraints();

            self.socialButtons[0].setUpController(parentController)
            self.socialButtons[0].setUpSwitch(SocialConnect.Facebook)

            self.socialButtons[1].setUpController(parentController)
            self.socialButtons[1].setUpSwitch(SocialConnect.Tumblr)

            self.socialButtons[2].setUpController(parentController)
            self.socialButtons[2].setUpSwitch(SocialConnect.Twitter)

            self.socialButtons[3].setUpController(parentController)
            self.socialButtons[3].setUpSwitch(SocialConnect.Reddit)

            TOSLink.setTitle("TOS Document", forState: .Normal);
            TOSLink.setTitleColor(UIColor.blackColor(), forState: .Normal);
            TOSLink.addTarget(self, action: "viewTOS", forControlEvents: UIControlEvents.TouchUpInside);
            TOSLink.titleLabel!.font = UIConstants.paragraphType;
            TOSLink.titleLabel!.textAlignment = .Left;
            TOSLink.contentHorizontalAlignment = UIControlContentHorizontalAlignment.Left;

    }

    func viewTOS() {
        println("open TOS");
        self.openTOS?();
    }


    func updateProfilePicture() {
        self.profilePicImg.alpha = 0;
        var url = NSURL(string: currentUser.data.picture.getPUPUrl())
        println(url)
        println("   !!!!!! it should be working")
        self.profilePicImg.image = UIImage(data: NSData(contentsOfURL: url!)!);
        self.profilePicImg.alpha = 1;

    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setDelegates(delegate: UIViewController) {
//        logout.addTarget(delegate as! SettingsController, action: "buttonAction:", forControlEvents: UIControlEvents.TouchUpInside)
//        logout.setTitle("Logout", forState: .Normal);
//        logout.setTitleColor(UIColor.whiteColor(), forState: .Normal)
//        logout.setTitleColor(UIColor.whiteColor(), forState: .Selected)
//        logout.backgroundColor = UIColor(rgba: colors.tealMain);
//        logout.titleLabel?.font = UIFont(name: "AvenirNext-Medium", size: 11.0);
        var imageTapper = UITapGestureRecognizer(target: delegate as! SettingsController, action: "imageTapped");
        self.profilePicImg.addGestureRecognizer(imageTapper);

        var imageTapper2 = UITapGestureRecognizer(target: delegate as! SettingsController, action: "imageTapped");
        self.cameraIcon.addGestureRecognizer(imageTapper2);
        //logout.titleLabel!.font = titleLabel!.font.fontWithSize(11)
    }

    func removeAllViews() {
        for (var i = self.subviews.count - 1; i > 0; i--) {
            var viewToRemove = self.subviews[i] as! UIView;
            viewToRemove.removeFromSuperview();
        }

        //self.setUpView();
    }


    func setAlphas(opac: CGFloat) {
        profilePicImg.alpha = opac
        cameraIcon.alpha = opac
        usernameView.alpha = opac
        integrationDisclaimer.alpha = opac
        profileView.alpha = opac
        containerView.alpha = opac

        if (self.bounceImage) {
            let bounceAnimation = CAKeyframeAnimation(keyPath: "transform.scale")
            bounceAnimation.values = [1.0, 1.25, 0.94, 1.15, 0.95, 1.02, 1.0]
            bounceAnimation.duration = NSTimeInterval(0.5)
            bounceAnimation.calculationMode = kCAAnimationCubic

            profilePicImg.layer.addAnimation(bounceAnimation, forKey: "bounceAnimation")

        }
    }

    func setUpView() {

            self.profileView.addSubview(profilePicImg)
            self.profileView.addSubview(cameraIcon)
            self.profileView.addSubview(usernameView)
            self.profileView.addSubview(integrationDisclaimer)
            self.containerView.addSubview(profileView)

            self.containerView.addSubview(socialButtons[0])
            self.containerView.addSubview(socialButtons[1])
            self.containerView.addSubview(socialButtons[2])
            self.containerView.addSubview(socialButtons[3])
            self.containerView.addSubview(TOSLink)

        self.scrollView.addSubview(containerView)
        self.addSubview(self.scrollView)
    }


    func removeAllViewsAndRemake() {

        var allViews = self.subviews;

        for (var i = 0; i<allViews.count; i++) {
            allViews[i].removeFromSuperview();
        }

        initView(self.parentViewController!);

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
            make.top.equalTo(self).offset(0);
            make.bottom.equalTo(self)
        }


        

            socialButtons[0].snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self.profileView.snp_bottom).offset(-UIConstants.verticalPadding * 1.5)
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

            TOSLink.snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self.socialButtons[3].snp_bottom).offset(UIConstants.verticalPadding * 2)
                make.left.equalTo(self.containerView).offset(UIConstants.horizontalPadding);
                make.width.greaterThanOrEqualTo(130)
                make.height.equalTo(60)
            }

            profileView.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.containerView).offset(0)
                make.top.equalTo(self.containerView).offset(UIConstants.verticalPadding)
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
                make.height.equalTo(25)
            }

            integrationDisclaimer.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicImg.snp_right).offset(UIConstants.horizontalPadding)
                make.right.equalTo(self.profileView).offset(0)
                make.top.equalTo(self.usernameView.snp_bottom).offset(0)
                make.bottom.equalTo(self.profilePicImg.snp_bottom);
            }


    }



}