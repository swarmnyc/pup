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
        self.view = settingsView
        settingsView?.setDelegates(self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

       // currentUser.setPage("Find A Game");
        self.title = "Settings";


    }

    func buttonAction(sender: PlatformButtonToggle!) {
       println("pressed")
        currentUser.logout();

        JLToast.makeText("You have been logged out").show()
        nav!.selectedIndex = 0;
        nav!.selectedViewController!.viewDidAppear(true)

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

        picker.allowsEditing = true //2
        picker.sourceType = .Camera //3

        self.view.window?.rootViewController?.presentViewController(picker, animated: true, completion: nil)//4

    }
    func bringOutPhotoLibrary() {
        println("bring out photo")

        let picker = UIImagePickerController()
        picker.delegate = self

        picker.allowsEditing = true //2
        picker.sourceType = .PhotoLibrary //3


        self.view.window?.rootViewController?.presentViewController(picker, animated: true, completion: nil)//4

    }

    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [NSObject : AnyObject]) {
        println(picker)
        println(info)
        var chosenImage = info[UIImagePickerControllerEditedImage] as! UIImage
        currentUser.updatePortrait(chosenImage, success: {
            println("success")
            self.settingsView?.updateProfilePicture();
        }, failure: {
            println("failure")
        });

        picker.dismissViewControllerAnimated(true, completion: nil) //5
    }

    func imagePickerControllerDidCancel(picker: UIImagePickerController) {
        println(picker)
        picker.dismissViewControllerAnimated(true, completion: nil)



    }






}