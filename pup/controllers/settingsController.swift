//
// Created by Alex Hartwell on 6/9/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SettingsController: UIViewController {

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


        let menuImage = UIImage(named: "hamburgerMenu")
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: menuImage, style: UIBarButtonItemStyle.Plain, target: navigationController, action: "toggleSideMenu")







    }

    func buttonAction(sender: PlatformButtonToggle!) {
       println("pressed")
        currentUser.logout();
        sideMenuController()?.setContentViewController(LobbyListController())
        sideMenuController()?.sideMenu?.reloadData();

    }






}