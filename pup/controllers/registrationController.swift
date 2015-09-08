//
// Created by Alex Hartwell on 6/5/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import SwiftLoader
class RegistrationController: UIViewController, UITextFieldDelegate, ImageButtonDelegate, UIImagePickerControllerDelegate,UINavigationControllerDelegate, RegistrationDelegate {


    var parentController: JoinPupButton?
    var registrationView: RegistrationView = RegistrationView();
    var onSuccessJoin: (() -> (Void))?
    var submitting = false;
    var parentView: UIView?

    convenience init(parentController: JoinPupButton?) {
        self.init()
            self.view = registrationView;
        if (parentController != nil) {
            self.parentController = parentController
        }
        registrationView.hideIt = {
            self.navigationController?.dismissViewControllerAnimated(false, completion: nil);
        }
//        self.view = filterView
//        parent = parentController
//        filterView.parentView = parent!.view
//
//        filterView.setUpDelegates(self)
//        filterView.setUpViews();
//        //setUpViews()
//        searchController = SearchResultsController(parent: self, searchBar: filterView.search);
//        //self.searchController.setUpView(self as UIViewController);
//        view.addSubview(searchController!.view)
//
//        data.delegate = self;

        registerForKeyboardNotifications()

    }

    func setUpView() {
        registrationView.setUpView();
        registrationView.getData()

    }

    func showJoinScreen(onSuccess: (() -> Void)) {
        self.onSuccessJoin = onSuccess;
        self.addParentConstraints();

    }

    func addParentConstraints() {


        var overlayNav = UINavigationController(rootViewController: self);
        overlayNav.modalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;
        overlayNav.navigationBar.hidden = true;
            nav!.presentViewController(overlayNav, animated: false, completion: nil);
        
        // (nav!.viewControllers![nav!.selectedIndex] as! UINavigationController).presentViewController(overlayNav, animated: true, completion: nil);
    
    }


    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        registrationView.addParentConstraints(self);

    }

    func registerClicked() {
        println("register")
//        var config = SwiftLoader.Config()
//        config.size = 150
//        config.spinnerColor = UIColor(rgba: colors.orange)
//        config.backgroundColor = UIColor.whiteColor()
//
//        SwiftLoader.setConfig(config);
//        SwiftLoader.show(title: "Loading...", animated: true)
        self.registrationView.email.resignFirstResponder()
        self.registrationView.username.resignFirstResponder()

        self.registrationView.activityIndicator.startAnimating();
        if (submitting==false) {
            self.submitting=true;
            self.registrationView.darkenSubmitButton();
            registerUser();
        }
    }

    func closeClicked() {
        println("close")
        registrationView.hide();
//        var parent: JoinButton? = parentController as? JoinButton
        println(parentController);
        self.registrationView.activityIndicator.stopAnimating();

        // parent.removeRegistrationView()

    }

    func closeAndContinue() {
        println(parentController)
        self.navigationController?.dismissViewControllerAnimated(false, completion: nil)
        sideMenuController()?.sideMenu?.reloadData();

        registrationView.hide();
        self.view.removeFromSuperview()
        parentController?.removeRegistrationView()


        self.submitting=false;
        self.registrationView.activityIndicator.stopAnimating();

        onSuccessJoin!();
    }


    //image upload click event
    func touched(image: ImageViewButton) {
        println(image)
        println("touched the image!")

        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera)){
            let alert = UIAlertController(title: "Add a Profile Picture", message: nil, preferredStyle: .ActionSheet)
            var camera = UIAlertAction(title: "Camera",
                    style: UIAlertActionStyle.Default,
                    handler: {(alert: UIAlertAction!) in
                        self.bringOutCamera()
                    })
            var roll = UIAlertAction(title: "Photos",
                    style: UIAlertActionStyle.Default,
                    handler: {(alert: UIAlertAction!) in
                        self.bringOutPhotoLibrary()
                    })

            var cancel = UIAlertAction(title: "Cancel",
                    style: UIAlertActionStyle.Cancel,
                    handler: nil)

            alert.addAction(camera)
            alert.addAction(roll)
            alert.addAction(cancel)
//            self.parentViewController?.presentViewController(alert, animated: true, completion: nil);
            println(mainWindow!.rootViewController)
           self.navigationController?.presentViewController(alert, animated: true, completion: nil)//4
        } else {

            bringOutPhotoLibrary()
        }




    }

    func bringOutCamera() {
        println("bring out camera")
        let picker = UIImagePickerController()
        picker.delegate = self

        picker.allowsEditing = false //2
        picker.sourceType = .Camera //3
//        self.registrationView.layer.opacity = 0;
//        self.parentController?.joinButtonView.layer.opacity = 0;
        self.navigationController?.presentViewController(picker, animated: true, completion: nil)//4

    }
    func bringOutPhotoLibrary() {
        println("bring out photo")

        let picker = UIImagePickerController()
        picker.delegate = self

        picker.allowsEditing = false //2
        picker.sourceType = .PhotoLibrary //3
       // self.registrationView.layer.opacity = 0;
        //self.parentController?.joinButtonView.layer.opacity = 0;

        self.navigationController?.presentViewController(picker, animated: true, completion: nil)//4

    }

    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [NSObject : AnyObject]) {
        println(picker)
        println(info)
        
        var chosenImage = info[UIImagePickerControllerOriginalImage] as! UIImage


        picker.dismissViewControllerAnimated(true, completion: nil) //5

        var editor = DZNPhotoEditorViewController(image: chosenImage);
        editor.cropMode = DZNPhotoEditorViewControllerCropMode.Circular;
        editor.cropSize = CGSizeMake(400,400);
         var controller = UINavigationController(rootViewController: editor)
        editor.acceptBlock = {
            (editor, userInfo) -> Void in
            self.navigationController?.dismissViewControllerAnimated(true, completion: nil)
            var editedImage = userInfo[UIImagePickerControllerEditedImage] as! UIImage
            
            self.registrationView.setImage(editedImage);
            self.registrationView.layer.opacity = 1;
            self.parentController?.joinButtonView.layer.opacity = 1;
           // self.addParentConstraints();
        }
        
        editor.cancelBlock = {
            (editor) -> Void in
            self.navigationController?.dismissViewControllerAnimated(true, completion: nil)
            self.registrationView.layer.opacity = 1;
            self.parentController?.joinButtonView.layer.opacity = 1;
           // self.addParentConstraints();
//            self.popViewController(animated: true);
        }
        
       
        
        self.navigationController?.presentViewController(controller, animated: true, completion: nil);


    }

    func imagePickerControllerDidCancel(picker: UIImagePickerController) {
        println(picker)
        self.registrationView.layer.opacity = 1;
        self.parentController?.joinButtonView.layer.opacity = 1;
        picker.dismissViewControllerAnimated(true, completion: nil)



    }
        func textFieldShouldBeginEditing(textField: UITextField) -> Bool {
        //textField.becomeFirstResponder()
        println("textView!!!")

        if (textField.text == UIConstants.usernamePlaceholder || textField.text == UIConstants.emailPlaceholder) {
            textField.text = ""
        }

        return true;
    }



    func registerUser() {
        var registrationData = registrationView.getData();
        println(registrationData)

        if (currentUser.validData(registrationData)) {
            currentUser.register(registrationData, success: closeAndContinue, failure: {
                self.submitting = false;
                self.registrationView.activityIndicator.stopAnimating();

            });
        } else {
            submitting = false;
            self.registrationView.activityIndicator.stopAnimating();
            self.registrationView.unselectSubmitText();
            SNYError(alertTitle: "Couldn't Register", alertText: "Please check your email and username");

        }


    }


    func keyboardWillShow(notification: NSNotification)
    {
        self.registrationView.moveViewUp(notification);

    }

    func keyboardWillBeHidden(notification: NSNotification)
    {
        self.registrationView.bringViewDown();
    }

    func registerForKeyboardNotifications() {
        NSNotificationCenter.defaultCenter().addObserver(
        self,
                selector: "keyboardWillShow:",
                name: UIKeyboardWillShowNotification,
                object: nil)

        NSNotificationCenter.defaultCenter().addObserver(
        self,
                selector: "keyboardWillBeHidden:",
                name: UIKeyboardWillHideNotification,
                object: nil)
    }

   func textFieldShouldReturn(textField: UITextField) -> Bool {
        if (registrationView.checkIfUserNameIsResponder()) {
            if (textField.text == "") {
                textField.text==UIConstants.usernamePlaceholder
            }
            registrationView.setEmailToResponder()
        } else {
            println("hit done!")
            if (textField.text == "") {
                textField.text==UIConstants.emailPlaceholder
            } else {
                textField.resignFirstResponder()
              registerClicked();
            }
        }

       return true
    }





}