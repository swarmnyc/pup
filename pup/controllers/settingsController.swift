//
// Created by Alex Hartwell on 6/9/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import JLToast

class SettingsController: UIViewController, UIImagePickerControllerDelegate,UINavigationControllerDelegate {

    var settingsView: SettingsView?


    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }

    override func loadView() {
        println("loading lobby list view!")
        settingsView = SettingsView()
        self.navigationController?.navigationBar.translucent = false;
        self.view = settingsView
        self.settingsView?.initView(self);
        settingsView?.setDelegates(self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

       // currentUser.setPage("Find A Game");
        self.title = "Settings";
        self.settingsView?.initView(self);

    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
//        self.settingsView?.initView();
//         self.destroyView();

        if (currentUser.loggedIn()) {
            self.settingsView?.setAlphas(1)
        } else {
            self.settingsView?.setAlphas(0)

        }


//        self.settingsView?.initView(self);

    }

    func buttonAction(sender: PlatformButtonToggle!) {
       println("pressed")
        currentUser.logout();
        self.destroyView();
        JLToast.makeText("You have been logged out").show()
        nav!.selectedIndex = 0;
        nav!.selectedViewController!.viewDidAppear(true)

    }

    func destroyView() {
        self.settingsView?.removeAllViews();

        println("destroy view");

        self.settingsView?.initView(self);

        settingsView?.setDelegates(self)
    }


    func imageTapped() {
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
            self.view.window?.rootViewController?.presentViewController(alert, animated: true, completion: nil)//4
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
        self.view.window?.rootViewController?.presentViewController(picker, animated: true, completion: nil)//4

    }
    func bringOutPhotoLibrary() {
        println("bring out photo")

        let picker = UIImagePickerController()
        picker.delegate = self

        picker.allowsEditing = false //2
        picker.sourceType = .PhotoLibrary //3

        println(self.view.window?.rootViewController);
        self.view.window?.rootViewController?.presentViewController(picker, animated: true, completion: nil)//4

    }

    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [NSObject : AnyObject]) {



        var chosenImage = info[UIImagePickerControllerOriginalImage] as! UIImage

        println(chosenImage);

        var editor = DZNPhotoEditorViewController(image: chosenImage);
        editor.cropMode = DZNPhotoEditorViewControllerCropMode.Circular;
        editor.cropSize = CGSizeMake(400,400);
        var controller = UINavigationController(rootViewController: editor)
        editor.acceptBlock = {
            (editor, userInfo) -> Void in
            nav!.dismissViewControllerAnimated(true, completion: nil)
            var editedImage = userInfo[UIImagePickerControllerEditedImage] as! UIImage

            SwiftLoader.show(title: "Uploading Profile Picture", animated: false);
            currentUser.updatePortrait(editedImage, success: {
                println(self.settingsView)
                self.settingsView?.updateProfilePicture();
                SwiftLoader.hide();
            }, failure: {
                println("failure")
                SwiftLoader.hide();
                Error(alertTitle: "Couldn't Upload Your Photo", alertText: "Please try again...")
            });

        }

        editor.cancelBlock = {
            (editor) -> Void in
            nav!.dismissViewControllerAnimated(true, completion: nil)

//            self.popViewController(animated: true);
        }

        println(nav!);
        println(nav!.navigationController);
        picker.dismissViewControllerAnimated(true, completion: nil) //5
        nav!.presentViewController(controller, animated: true, completion: nil);


    }

    func imagePickerControllerDidCancel(picker: UIImagePickerController) {
        println(picker)
        picker.dismissViewControllerAnimated(true, completion: nil)



    }






}