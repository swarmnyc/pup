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


    convenience init(parentController: JoinPupButton) {
        self.init()
            self.view = registrationView;
            self.parentController = parentController

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



    }

    func setUpView() {
        registrationView.setUpView();
        registrationView.getData()

    }

    func addParentConstraints(parentView: UIView) {
            registrationView.addParentConstraints(parentView, delegate: self)

    }



    func registerClicked() {
        println("register")
        var config = SwiftLoader.Config()
        config.size = 150
        config.spinnerColor = UIColor(rgba: colors.orange)
        config.backgroundColor = UIColor.whiteColor()

        SwiftLoader.setConfig(config);
        SwiftLoader.show(title: "Loading...", animated: true)
        if (submitting==false) {
            self.submitting=true;
            registerUser();
        }
    }

    func closeClicked() {
        println("close")
        registrationView.hide()
//        var parent: JoinButton? = parentController as? JoinButton
        println(parentController)
       // parent.removeRegistrationView()

    }

    func closeAndContinue() {
        println(parentController)
        sideMenuController()?.sideMenu?.reloadData();

        registrationView.hide()
        self.view.removeFromSuperview()
        parentController?.removeRegistrationView()


        self.submitting=false;
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
            parentController?.presentViewController(alert, animated: true, completion: nil)//4
        } else {

            bringOutPhotoLibrary()
        }




    }

    func bringOutCamera() {
        println("bring out camera")
        let picker = UIImagePickerController()
        picker.delegate = self

        picker.allowsEditing = true //2
        picker.sourceType = .Camera //3
        self.registrationView.layer.opacity = 0;
        self.parentController?.joinButtonView.layer.opacity = 0;
        self.view.window?.rootViewController?.presentViewController(picker, animated: true, completion: nil)//4

    }
    func bringOutPhotoLibrary() {
        println("bring out photo")

        let picker = UIImagePickerController()
        picker.delegate = self

        picker.allowsEditing = true //2
        picker.sourceType = .PhotoLibrary //3
        self.registrationView.layer.opacity = 0;
        self.parentController?.joinButtonView.layer.opacity = 0;

        self.view.window?.rootViewController?.presentViewController(picker, animated: true, completion: nil)//4

    }

    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [NSObject : AnyObject]) {
        println(picker)
        println(info)
        var chosenImage = info[UIImagePickerControllerOriginalImage] as! UIImage

        registrationView.setImage(chosenImage)
        self.registrationView.layer.opacity = 1;
        self.parentController?.joinButtonView.layer.opacity = 1;
        picker.dismissViewControllerAnimated(true, completion: nil) //5
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
            currentUser.register(registrationData, success: closeAndContinue);
        } else {
            SwiftLoader.hide()
            submitting = false;


        }


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