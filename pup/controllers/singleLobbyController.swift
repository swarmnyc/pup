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


    var joinPupButton: JoinPupButton? = nil

    convenience init(info: LobbyData) {

        self.init();
        println(info);
        println(info.name);
        data.passMessagesToController = recievedMessages;
        data.data = info;
        data.setID()





    }




    override func loadView()
    {
        self.lobbyView = SingleLobbyView();
        lobbyView?.setUpDelegates(self)
        self.view = self.lobbyView;
    }


    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.whiteColor()
        self.title = data.data.name;
        println("view did load")
        currentUser.setPage("Single Lobby")

        self.data.getDetailedAndThenGetMessages({
            self.lobbyView?.setUpViews(self.data)

            if (currentUser.loggedIn() == false) {
                self.joinPupButton = JoinPupButton(parentController: self)
                self.joinPupButton?.onSuccessJoin = self.joinLobby
            }


        }, failure: {

        })

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

        if (vcs.indexOfObject(self) == NSNotFound) {
            println("popped view")
            joinPupButton?.removeRegistrationView();
        }

    }






    func recievedMessages() {
        println("got em!")

        if (self.lobbyView!.hasMessages()) {
            self.lobbyView?.table?.reloadData();
        } else {
            self.lobbyView?.addTable(self);
        }
        self.lobbyView?.makeTableViewLonger(self.data.data.messages.count)

        println("about to submit a message")

    }








    override func viewDidLayoutSubviews() {  //set up layer properties that don't get set by constraints
            super.viewDidLayoutSubviews();
        println("view did layout subviews")

        println(self.lobbyView?.table?.frame)

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


    //UITEXTFIELDDELEGATE

    func textFieldShouldEndEditing(_ textField: UITextField) -> Bool {
        println(textField)

        return true;
    }


    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder();

        println(textField.text);
        self.data.quickBloxConnect?.sendMessage(textField.text);

        return true;
    }


    //table view functions

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

        return self.data.data.messages.count;
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        //var cell:UITableViewCell = self.tableV.dequeueReusableCellWithIdentifier("cell") as UITableViewCell
        //cell.textLabel.text = self.items[indexPath.row]
        let cell = tableView.dequeueReusableCellWithIdentifier("message", forIndexPath: indexPath) as! MessageCell
        cell.setUpCell(self.data.data.messages[indexPath.row])
        return cell;


    }

    func tableView(tlableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 50;


    }


    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")

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


