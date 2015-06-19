//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


var menuLabels = ["My Chats", "Find A Game", "Feedback", "Settings"]

class MenuNavigationController: ENSideMenuNavigationController, ENSideMenuDelegate {

    override func viewDidLoad() {
        super.viewDidLoad()
        println(sideMenu)
        sideMenu = ENSideMenu(sourceView: self.view, menuTableViewController: MenuTableController(), menuPosition:.Left)
        //sideMenu?.delegate = self //optional
        sideMenu?.menuWidth = UIScreen.mainScreen().bounds.size.width * 0.9; // optional, default is 160
        println(sideMenu?.menuWidth)
        sideMenu?.bouncingEnabled = false
        println(sideMenu)


        // make navigation bar showing over side menu
        view.bringSubviewToFront(navigationBar)


    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - ENSideMenu Delegate
    func sideMenuWillOpen() {
        println("sideMenuWillOpen")
    }

    func sideMenuWillClose() {
        println("sideMenuWillClose")
    }

    /*
    // MARK: - Navigation
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue!, sender: AnyObject!) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}


class MenuTableController: UITableViewController {
    var selectedMenuItem : Int = 1
    override func viewDidLoad() {
        super.viewDidLoad()

        // Customize apperance of table view
        tableView.contentInset = UIEdgeInsetsMake(64.0, 0, 0, 0) //
        tableView.separatorStyle = .None
        tableView.backgroundColor = UIColor.clearColor()
        tableView.scrollsToTop = false

        // Preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = false

        if (currentUser.loggedIn()) {
            selectedMenuItem = 1;
        } else {
            selectedMenuItem = 0;
        }

        tableView.selectRowAtIndexPath(NSIndexPath(forRow: selectedMenuItem, inSection: 0), animated: false, scrollPosition: .Middle)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // Return the number of sections.
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // Return the number of rows in the section.
        if (currentUser.loggedIn()) {
        return 4
        } else {
            return 3;
        }
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {

        var cell = tableView.dequeueReusableCellWithIdentifier("CELL") as? UITableViewCell

        if (cell == nil) {
            cell = UITableViewCell(style: UITableViewCellStyle.Default, reuseIdentifier: "CELL")
            cell!.backgroundColor = UIColor.clearColor()
            cell!.textLabel?.textColor = UIColor.darkGrayColor()
            let selectedBackgroundView = UIView(frame: CGRectMake(0, 0, cell!.frame.size.width, cell!.frame.size.height))
            selectedBackgroundView.backgroundColor = UIColor.grayColor().colorWithAlphaComponent(0.2)
            cell!.selectedBackgroundView = selectedBackgroundView
        }

        var currentLabelNumber = indexPath.row;
        if (!currentUser.loggedIn()) {
            currentLabelNumber++;
        }
        var labelText = menuLabels[currentLabelNumber];

        cell!.textLabel?.text = labelText
        cell!.textLabel?.textAlignment = NSTextAlignment.Center
        return cell!
    }

    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 50.0
    }

    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {

        println("did select row: \(indexPath.row)")

        if (indexPath.row == selectedMenuItem) {
            var menu = sideMenuController() as! ENSideMenuNavigationController
            menu.toggleSideMenu()
            return
        }
        selectedMenuItem = indexPath.row
        if (!currentUser.loggedIn()) {
            selectedMenuItem++;
        }

        //Present new view controller
        var destViewController : UIViewController
        switch (selectedMenuItem) {
        case 0:
            destViewController = MyChatsController() as! UIViewController
            break
        case 1:
            destViewController = LobbyListController() as! UIViewController
            break
        case 2:
            destViewController = UIViewController() as! UIViewController
            break
        default:
            destViewController = SettingsController() as! UIViewController
            break
        }
        println(sideMenuController())
        sideMenuController()?.setContentViewController(destViewController)
    }


    /*
    // MARK: - Navigation
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue!, sender: AnyObject!) {
        // Get the new view controller using [segue destinationViewController].
        // Pass the selected object to the new view controller.
    }
    */

}