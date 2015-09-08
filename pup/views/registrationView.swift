//
// Created by Alex Hartwell on 6/5/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class RegistrationView: UIView {

    var topLabel: UILabel = UILabel()
    var dogCameraUpload: ImageViewButton = ImageViewButton();
    var cameraIcon: UIImageView = UIImageView();

    var registrationDescription: UITextView = UITextView();
    var username: UITextField = UITextField();
    var email: UITextField = UITextField();
    var register: UIButton = UIButton();
    var cancel: UIButton = UIButton();
    var registrationDelegate: RegistrationDelegate?
    var blackBack: UIControl = UIControl();
    var whiteView: UIView = UIView();
    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .White)
    var hideIt: (() -> Void)?
    override init(frame: CGRect) {
        super.init(frame: frame)


    }
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }



    func setUpView() {

        self.backgroundColor = UIColor.clearColor();

        self.layer.shadowRadius = 0;
        self.layer.shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor
        self.layer.shadowOpacity = 1;
        self.layer.shadowOffset = CGSizeMake(2.0, 2.0);

        self.whiteView.backgroundColor = UIColor.whiteColor();

        self.blackBack.backgroundColor = UIColor.blackColor();
        self.blackBack.addTarget(self, action: "hide", forControlEvents: UIControlEvents.TouchUpInside);
        self.blackBack.alpha = 0;
        self.blackBack.userInteractionEnabled = true;

        self.layer.masksToBounds = false;

        cameraIcon.image = UIImage(named: "camera");
        
        topLabel.text = "Register for PUP"
        topLabel.backgroundColor = UIColor(rgba: colors.tealMain)
        topLabel.textColor = UIColor.whiteColor()
        topLabel.textAlignment = NSTextAlignment.Center
        topLabel.font = UIConstants.buttonType;

        dogCameraUpload.backgroundColor = UIColor(rgba: colors.lightGray);
        dogCameraUpload.userInteractionEnabled = true
        dogCameraUpload.image = UIImage(named: "profilePicDefault");
        dogCameraUpload.contentMode = UIViewContentMode.ScaleAspectFit;
        
        registrationDescription.text = "You'll need to create a username, but you'll only have to do this once."
        registrationDescription.font = UIConstants.tagType!.fontWithSize(11);
        registrationDescription.textContainer.lineFragmentPadding = 0;
        registrationDescription.editable = false;
        registrationDescription.userInteractionEnabled = false;

        username.placeholder = UIConstants.usernamePlaceholder;
        username.font = UIConstants.paragraphType
        username.returnKeyType = .Next
    
        email.placeholder = UIConstants.emailPlaceholder;
        email.font = UIConstants.paragraphType
        email.returnKeyType = .Done
        email.keyboardType = UIKeyboardType.EmailAddress;
        
        register.setTitle("Register and Join", forState: .Normal)
        register.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        register.titleLabel?.font = UIConstants.titleFont
        register.addTarget(self, action: "registerButton", forControlEvents: UIControlEvents.TouchUpInside)

        cancel.setTitle("Cancel", forState: .Normal)
        cancel.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        cancel.titleLabel?.font = UIConstants.titleFont
        cancel.addTarget(self, action: "cancelButton", forControlEvents: UIControlEvents.TouchUpInside)


        addViews();
        setConstraints();

        var trans = CGAffineTransformMakeTranslation(0,1000);
        self.transform = trans;

        trans = CGAffineTransformMakeTranslation(0,-1000);
        self.blackBack.transform = trans;

    }

    func checkIfUserNameIsResponder() -> Bool {
        if (username.isFirstResponder()) {
            return true
        }

        return false


    }

    func setEmailToResponder() {
        email.becomeFirstResponder()
    }

    func registerButton() {
        registrationDelegate?.registerClicked()
    }

    func cancelButton() {
        registrationDelegate?.closeClicked()
    }

    func addParentConstraints(delegate: UITextFieldDelegate) {
        username.delegate = delegate
        email.delegate = delegate
        dogCameraUpload.delegate = delegate as? ImageButtonDelegate
        registrationDelegate = delegate as? RegistrationDelegate

        println("adding them")
//        UIApplication.sharedApplication().windows.first!.addSubview(self)
//        (nav!.viewControllers![nav!.selectedIndex] as! UINavigationController).viewControllers!.last!.view!.addSubview(self);
        setParentConstraints()

        userInteractionEnabled = true;
        self.unselectSubmitText();
        UIView.animateWithDuration(0.5, delay: 0.0, options: UIViewAnimationOptions.CurveEaseIn, animations: {
            var trans = CGAffineTransformMakeTranslation(0,0);
            self.transform = trans;
            self.blackBack.transform = trans;
            self.blackBack.alpha = 0.3;

        }, completion: {
            complete in
            println("complete")

        })
    }


    func setParentConstraints() {
//        self.snp_remakeConstraints { (make) -> Void in
//            make.centerX.equalTo(self.superview!)
//            make.top.equalTo(self.superview!)
//            make.left.equalTo(self.superview!)
//            make.right.equalTo(self.superview!)
//            make.bottom.equalTo(self.superview!)
//            //make.height.equalTo(220)
//        }

        self.whiteView.snp_remakeConstraints {
            (make) -> Void in
            make.centerX.equalTo(self)
            make.top.equalTo(self).offset(UIConstants.verticalPadding * 7.5)
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(220)
        }
    }

    func hide() {
       // self.layer.opacity = 0;
       // self.userInteractionEnabled = false;
        
        UIView.animateWithDuration(0.3, delay: 0.0, options: UIViewAnimationOptions.CurveEaseIn, animations: {
            var trans = CGAffineTransformMakeTranslation(0,1000);
            self.transform = trans;
            self.blackBack.alpha = 0;
            trans = CGAffineTransformMakeTranslation(0,-1000);
            self.blackBack.transform = trans;
        }, completion: {
            (complete) -> Void in
            self.hideIt?();
            self.removeFromSuperview()
        })
    }


    func moveViewUp(notification: NSNotification) {
        println("moveUp");
        var keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.CGRectValue()
        var keyboardHeight = keyboardSize!.height;
        UIView.animateWithDuration(0.3, animations: {
        self.whiteView.snp_remakeConstraints {
            (make) -> Void in
            make.centerX.equalTo(self)
            make.top.lessThanOrEqualTo(self).offset(150)
            make.bottom.equalTo(self).offset(-keyboardHeight - CGFloat(UIConstants.verticalPadding));
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(220)
        }

        

        });
       

    }


    func bringViewDown() {
        UIView.animateWithDuration(0.3, animations: {

        self.whiteView.snp_remakeConstraints {
            (make) -> Void in
            make.centerX.equalTo(self)
            make.top.equalTo(self).offset(UIConstants.verticalPadding * 7.5)
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(220)
        }
        })
    }

    func getData() -> (image: UIImage?, username: String, email: String) {

        var profileImage: UIImage? = dogCameraUpload.image;
        if (profileImage == UIImage(named: "profilePicDefault")) {
            println("profileImageIsNil");
            profileImage = nil;
        }
        var data: (UIImage?, String, String) = (profileImage, username.text, email.text);
        return data;

    }



    func setConstraints() {

        self.blackBack.snp_makeConstraints {
            (make) -> Void in
            make.top.equalTo(self);
            make.left.equalTo(self);
            make.right.equalTo(self);
            make.bottom.equalTo(self);
        }

        topLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.whiteView).offset(0)
            make.left.equalTo(self.whiteView).offset(0)
            make.right.equalTo(self.whiteView).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
        }

        activityIndicator.snp_makeConstraints {
            (make) -> Void in
            make.top.equalTo(self.topLabel)
            make.bottom.equalTo(self.topLabel)
            make.left.equalTo(self.topLabel).offset(UIConstants.horizontalPadding);
        }


        dogCameraUpload.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.topLabel.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self.whiteView).offset(UIConstants.horizontalPadding)
            make.width.equalTo(UIConstants.buttonHeight)
            make.height.equalTo(UIConstants.buttonHeight)
        }
        
        dogCameraUpload.layer.cornerRadius = CGFloat(UIConstants.buttonHeight / 2);
        dogCameraUpload.layer.masksToBounds = true;

        cameraIcon.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self.dogCameraUpload.snp_left).offset(2)
            make.bottom.equalTo(self.dogCameraUpload.snp_bottom).offset(-2)
            make.width.equalTo(UIScreen.mainScreen().bounds.size.width / 12)
            make.height.equalTo(UIScreen.mainScreen().bounds.size.width / 12)
        }

        registrationDescription.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.topLabel.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self.dogCameraUpload.snp_right).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.whiteView).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(UIConstants.buttonHeight / 2)
        }

        username.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.registrationDescription.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self.dogCameraUpload.snp_right).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.whiteView).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(UIConstants.buttonHeight / 2.0 / 1.5)
        }

        email.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.username.snp_bottom).offset(UIConstants.verticalPadding * 1.25)
            make.left.equalTo(self.whiteView).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.whiteView).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(UIConstants.buttonHeight / 2.0 / 1.5)
        }

        cancel.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.whiteView).offset(-UIConstants.verticalPadding)
            make.left.equalTo(self.whiteView).offset(0)
            make.right.equalTo(self.whiteView.snp_centerX).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)

        }
        register.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.whiteView).offset(-UIConstants.verticalPadding)
            make.left.equalTo(self.snp_centerX).offset(0)
            make.right.equalTo(self.whiteView).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
        }





    }
    
    func darkenSubmitButton() {
        register.setTitleColor(self.register.titleLabel?.textColor.darkerColor(0.4), forState: .Normal);
    }

    func unselectSubmitText() {
        register.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        
    }
    
    func addViews() {

        println("registration subviews added!")
        self.addSubview(self.blackBack);
        self.addSubview(self.whiteView);
        self.addSubview(topLabel)
        self.addSubview(dogCameraUpload)
        self.addSubview(cameraIcon)
        self.addSubview(registrationDescription)
        self.addSubview(username)
        self.addSubview(email)
        self.addSubview(register)
        self.addSubview(cancel)
        self.addSubview(activityIndicator)
    }

    func setImage(chosenImage: UIImage) {
        dogCameraUpload.image = chosenImage;
    }


}


class ImageViewButton: UIImageView {
    var delegate: ImageButtonDelegate?




    override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
        delegate?.touched(self);



    }


}