//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore
import SwiftLoader

class SingleLobbyController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate {

    var lobbyView: SingleLobbyView?;
    var data: singleLobby = singleLobby();
    var submitting = false;
    var descCell = SingleLobbyTopCell()
    var joinPupButton: JoinPupButton? = nil
    var hasAnimatedDown = false;
    var membersList = MembersDrawerController();
    var navBarVisible = false;
    var firstTime = true;

    convenience init(info: LobbyData) {

        self.init();
        println(info);
        println(info.name);
        data.passMessagesToController = recievedMessages;
        data.data = info;
        data.setID()

        descCell.setUpCell(data)

        if (currentUser.loggedIn() == false) {
            self.joinPupButton = JoinPupButton(parentController: self)
            self.joinPupButton?.onSuccessJoin = self.joinLobby
            self.addChildViewController(self.joinPupButton!)
        }


    }




    override func loadView() {
        self.lobbyView = SingleLobbyView();
        lobbyView?.sendTheMessage = self.sendMessage;
        lobbyView?.setUpDelegates(self)

        self.view = self.lobbyView;

        self.lobbyView?.addTable(self);
        scrollViewDidScroll(self.lobbyView?.table!)

        membersList.setUpSideBar();
        membersList.populateUserList(data.data.users, owner: data.data.owner)
       // self.lobbyView?.addDrawer(membersList.membersView);
        //makes it so that inside of a lobby their is no title on the back button
        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .Plain, target: nil, action: nil)



        println(navigationController);
        println("navigation controller")


    }

    //scrolling table view
    func scrollViewDidScroll(scrollView: UIScrollView!) {
        if (scrollView.contentOffset.y > CGFloat(UIConstants.lobbyImageHeight * 0.75)) {
           // self.lobbyView?.setTitle();

            if (self.navigationController?.navigationBar.translucent == true && navBarVisible == true) {
                navBarVisible = false
                self.title = data.data.name;
                UIView.animateWithDuration(0.3, animations: {
                    self.navigationController?.navigationBar.translucent = false
                    self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
                    self.navigationController?.navigationBar.shadowImage = nil
                })

            }

        } else {
            if (self.navigationController?.navigationBar.translucent == false && navBarVisible==false) {
                navBarVisible = true
                self.title = "";
                UIView.animateWithDuration(0.3, animations: {
                    self.navigationController?.navigationBar.translucent = true
                    self.navigationController?.navigationBar.setBackgroundImage(UIImage(), forBarMetrics: UIBarMetrics.Default)
                    self.navigationController?.navigationBar.shadowImage = UIImage()

                })
            }

            if (firstTime == false) {
                var amount = 1.0 - (1.0 * (CGFloat(scrollView.contentOffset.y) / CGFloat(UIConstants.lobbyImageHeight)))
                lobbyView?.scaleImage(amount)

            } else {
                firstTime = false;
            }
        }



    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.whiteColor()
        println("view did load")
        currentUser.setPage("Single Lobby")
        println(self.data.data.isMember)
        self.lobbyView?.setUpViews(self.data)

        self.data.setRoomIDAndLogIn()





        registerForKeyboardNotifications()



    }



    func keyboardWillShow(notification: NSNotification)
    {
        println("keyboard opened!")
        println(notification)
        lobbyView?.shortenView(notification)

    }

    func keyboardWillBeHidden(notification: NSNotification)
    {
        lobbyView?.restoreView();

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


    //View is leaving the building
    override func viewWillDisappear(animated: Bool) {
        NSNotificationCenter.defaultCenter().removeObserver(self,
                name: UIKeyboardDidShowNotification,
                object: nil)

        NSNotificationCenter.defaultCenter().removeObserver(self,
                name: UIKeyboardWillHideNotification,
                object: nil)
        var vcs = navigationController?.viewControllers as! NSArray;

        membersList.removeViews();

        if (vcs.indexOfObject(self) == NSNotFound) {
            println("popped view")


            joinPupButton?.removeRegistrationView();
        }

    }






    func recievedMessages() {
        println("got em!")

        self.lobbyView?.table?.reloadData();
        self.lobbyView?.clearText();
        self.lobbyView?.makeTableViewLonger(self.data.data.messages.count)

        println("about to submit a message")

    }








    override func viewDidLayoutSubviews() {  //set up layer properties that don't get set by constraints
            super.viewDidLayoutSubviews();
        println("view did layout subviews")


        if (data.data.isMember && (data.hasMessages() || data.isEmpty()) && hasAnimatedDown == false) {
            var rowAndSection = NSIndexPath(forRow: self.data.data.messages.count - 1, inSection: 1);
            self.lobbyView?.table?.scrollToRowAtIndexPath(rowAndSection, atScrollPosition: UITableViewScrollPosition.Bottom, animated: true);
            hasAnimatedDown = true;
        }

//        if let tableFrame = self.lobbyView?.table {
//            var offset = tableFrame.frame.
//        }

        // gradient.frame = gradientBox.frame;



    }


    func joinLobby() {
        println("joining this lobby")
        var config = SwiftLoader.Config()
        config.size = 150
        config.spinnerColor = UIColor(rgba: colors.orange)
        config.backgroundColor = UIColor(rgba: colors.mainGrey)

        SwiftLoader.setConfig(config);
        SwiftLoader.show(title: "Loading...", animated: true)
        if (submitting==false) {
            submitting = true;
            data.joinLobby({
                self.submitting = false;
                println("successfully joined")
                self.lobbyView?.joinLobbyButton.removeFromSuperview();
                SwiftLoader.hide()
                
            }, failure: {
                println("failed")
                SwiftLoader.hide()
                self.submitting = false;
            })

        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
        println("logging out")
        self.data.logoutOfQuickBlox()

    }


    func sendMessage(message: String) {
        if (message != "") {
            self.data.quickBloxConnect?.sendMessage(message);
        }

    }

    //UITEXTFIELDDELEGATE

    func textFieldShouldEndEditing(_ textField: UITextField) -> Bool {
        println(textField)

        return true;
    }


    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder();

        println(textField.text);
        sendMessage(textField.text)
        return true;
    }


    //table view functions



    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (section==0) {
           return 1
        } else {
            if (self.data.hasMessages()) {
                return self.data.data.messages.count;
            } else {
                return 1
            }
        }
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        //var cell:UITableViewCell = self.tableV.dequeueReusableCellWithIdentifier("cell") as UITableViewCell
        //cell.textLabel.text = self.items[indexPath.row]
        if (indexPath.row==0 && indexPath.section==0) {

            return descCell
        } else {
            if (self.data.hasMessages()) {
                let cell = tableView.dequeueReusableCellWithIdentifier("message", forIndexPath: indexPath) as! MessageCell
                cell.setUpCell(self.data.data.messages[indexPath.row])
                return cell;
            } else {
                let cell = UITableViewCell();
                cell.contentView.backgroundColor = UIColor.whiteColor();
                cell.textLabel?.text = "Loading...";
                cell.textLabel?.textAlignment = NSTextAlignment.Center
                return cell
            }
        }


    }

    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 2;
    }



    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        if (indexPath.row == 0 && indexPath.section == 0) {
            return CGFloat(UIConstants.lobbyImageHeight) + CGFloat(100.0)
        }
        if (self.data.hasMessages()) {
            return CGFloat(50.0) + CGFloat(Int(count(self.data.data.messages[indexPath.row].message)/26) * 12);

        } else {
            return tableView.frame.height - (CGFloat(UIConstants.lobbyImageHeight) + CGFloat(150.0));
        }


    }


    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
        var cell = tableView.cellForRowAtIndexPath(indexPath);
        cell?.backgroundColor = UIColor.whiteColor()

        lobbyView?.newMessage.resignFirstResponder()

    }




}





class NoOnesHere {

    var topText: UIView = UIView();
    var facebookBlocks: Array<UITextView> = []
    var inviteButton: UIButton = UIButton()
    var parent: SingleLobbyController?
    init(parentController: SingleLobbyController) {
        parent = parentController;

        setUpViews()
    }

    func setUpViews() {


    }

    func addViews() {

    }

    func setUpConstraints() {


    }


}


