//
// Created by Alex Hartwell on 6/5/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class RegistrationView: UIView {

    var topLabel: UILabel = UILabel()
    var dogCameraUpload: ImageViewButton = ImageViewButton();
    var registrationDescription: UITextView = UITextView();
    var username: UITextField = UITextField();
    var email: UITextField = UITextField();
    var register: UIButton = UIButton();
    var cancel: UIButton = UIButton();
    var registrationDelegate: RegistrationDelegate?
    var parentView: UIView?
    override init(frame: CGRect) {
        super.init(frame: frame)


    }
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }



    func setUpView() {

        self.backgroundColor = UIColor.whiteColor();
        self.layer.shadowOffset = CGSizeMake(2,2)
        self.layer.shadowRadius = 10
        self.layer.shadowOpacity = 0.8
        self.layer.masksToBounds = false;

        topLabel.text = "Register for PUP"
        topLabel.backgroundColor = UIColor(rgba: colors.tealMain)
        topLabel.textColor = UIColor.whiteColor()
        topLabel.textAlignment = NSTextAlignment.Center

        dogCameraUpload.backgroundColor = UIColor.blackColor();
        dogCameraUpload.userInteractionEnabled = true

        registrationDescription.text = "You'll need to create a username, but you'll only have to do this once."
        registrationDescription.font = registrationDescription.font.fontWithSize(11.0)

        username.text = UIConstants.usernamePlaceholder
        username.font = username.font.fontWithSize(13.0)
        username.returnKeyType = .Next

        email.text = UIConstants.emailPlaceholder
        email.font = email.font.fontWithSize(13.0)
        email.returnKeyType = .Done

        register.setTitle("Register and Join", forState: .Normal)
        register.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        register.titleLabel?.font = register.titleLabel?.font.fontWithSize(13.0)
        register.addTarget(self, action: "registerButton", forControlEvents: UIControlEvents.TouchUpInside)

        cancel.setTitle("Cancel", forState: .Normal)
        cancel.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        cancel.titleLabel?.font = cancel.titleLabel?.font.fontWithSize(13.0)
        cancel.addTarget(self, action: "cancelButton", forControlEvents: UIControlEvents.TouchUpInside)


        addViews();
        setConstraints();


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

    func addParentConstraints(parentView: UIView, delegate: UITextFieldDelegate) {
        self.parentView = parentView
        username.delegate = delegate
        email.delegate = delegate
        dogCameraUpload.delegate = delegate as? ImageButtonDelegate
        registrationDelegate = delegate as? RegistrationDelegate

        println("adding them")
        UIApplication.sharedApplication().windows.first!.addSubview(self)

        setParentConstraints()

        userInteractionEnabled = true;


    }


    func setParentConstraints() {
        self.snp_remakeConstraints { (make) -> Void in
            make.centerX.equalTo(self.superview!)
            make.top.equalTo(self.superview!).offset(UIConstants.verticalPadding * 7.0)
            make.left.equalTo(self.superview!).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.superview!).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(220)
        }
    }

    func hide() {
       // self.layer.opacity = 0;
       // self.userInteractionEnabled = false;
       self.removeFromSuperview()
    }


    func getData() -> (image: UIImage?, username: String, email: String) {
//        println(username.text)
//        println(email.text)
//        println(dogCameraUpload.image)

        var data: (UIImage?, String, String) = (dogCameraUpload.image, username.text, email.text);
        return data;

    }



    func setConstraints() {

        topLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
        }


        dogCameraUpload.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.topLabel.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.width.equalTo(UIConstants.buttonHeight)
            make.height.equalTo(UIConstants.buttonHeight)
        }


        registrationDescription.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.topLabel.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self.dogCameraUpload.snp_right).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
        }

        username.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.registrationDescription.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self.dogCameraUpload.snp_right).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(UIConstants.buttonHeight / 2.0 / 2.0)
        }

        email.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.username.snp_bottom).offset(UIConstants.verticalPadding * 2)
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(UIConstants.buttonHeight / 2.0 / 2.0)
        }

        cancel.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self.snp_centerX).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)

        }
        register.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.left.equalTo(self.snp_centerX).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
        }



    }

    func addViews() {

        println("registration subviews added!")
        self.addSubview(topLabel)
        self.addSubview(dogCameraUpload)
        self.addSubview(registrationDescription)
        self.addSubview(username)
        self.addSubview(email)
        self.addSubview(register)
        self.addSubview(cancel)
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