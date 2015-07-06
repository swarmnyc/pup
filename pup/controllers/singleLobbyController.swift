//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore
import SwiftLoader

class SingleLobbyController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate, SimpleButtonDelegate {

    var lobbyView: SingleLobbyView?;
    var data: singleLobby = singleLobby();
    var submitting = false;
    var descCell = SingleLobbyTopCell()
    var imageCover: UIView = UIView()
    var joinPupButton: JoinPupButton? = nil
    var hasAnimatedDown = false;
    var membersList = MembersDrawerController();
    var navBarVisible = false;
    var firstTime = true;

    var socialController: SocialConnector?
    var socialView: SocialButtonsView?

    var canLoadMore = false;
    var shouldLoadMore = true;

    convenience init(info: LobbyData) {
        //to do add in right side members drawer nav button
        self.init();
        println(info);
        println(info.messages);


        data.passMessagesToController = recievedMessages;
        data.reloadData = reloadData;

        println("setting data")
        data.data = info;
        data.recievedMessages = info.recievedMessages;
        data.data.setMessagesRecieved = data.messagesReceived; //let the controller know it has recieved messages

        data.data.clearTheText = data.clearText;
        data.data.reloadData = self.reloadData;

        descCell.setUpCell(data)
        imageCover = descCell.getTopBox();
        println(imageCover.subviews)
        println("image cover")
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

        membersList.setNavigationBar(self);
        membersList.setUpSideBar();
        membersList.populateUserList(data.data.users, owner: data.data.owner)
       // self.lobbyView?.addDrawer(membersList.membersView);
        //makes it so that inside of a lobby their is no title on the back button
        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .Plain, target: nil, action: nil)


        data.clearInputText = self.lobbyView?.clearText;


    }


    func showNavBar() {
        UIView.animateWithDuration(0.3, animations: {
            self.navigationController?.navigationBar.translucent = false
            self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
            self.navigationController?.navigationBar.shadowImage = nil
            UIApplication.sharedApplication().statusBarStyle = .Default

        })
    }

    func hideNavBar() {
        UIView.animateWithDuration(0.3, animations: {
            self.navigationController?.navigationBar.translucent = true
            self.navigationController?.navigationBar.setBackgroundImage(UIImage(), forBarMetrics: UIBarMetrics.Default)
            self.navigationController?.navigationBar.shadowImage = UIImage()
            UIApplication.sharedApplication().statusBarStyle = .LightContent

        })
    }

    //scrolling table view
    func scrollViewDidScroll(scrollView: UIScrollView!) {

        if (scrollView.contentOffset.y > CGFloat(UIConstants.lobbyImageHeight) + CGFloat(140.0) && self.data.data.isMember) {
            self.canLoadMore = true;
            if (scrollView.contentOffset.y < (CGFloat(UIConstants.lobbyImageHeight) + CGFloat(140.0)) + 20 && self.shouldLoadMore) {
                self.shouldLoadMore = false;
                self.data.loadMoreHistory({
                    (messages) -> Void in
                    println("loaded more");
                    println(messages);

                    var offsetAddition = self.data.insertMessagesToStartAndGetHeightOfMessages(messages);

                    if (messages.count > 0) {
                        self.reloadData();
                        self.shouldLoadMore = true;
                    }
                        self.lobbyView?.layoutIfNeeded();

                    var offsetAdd = self.lobbyView!.table!.contentOffset.y + offsetAddition;
                    self.lobbyView?.table?.setContentOffset(CGPointMake(0.0, CGFloat(offsetAdd)), animated: false);

                }, failure: {
                    println("loaded more failure");

                    self.shouldLoadMore = true;
                })

            }

        }


        if (scrollView.contentOffset.y > CGFloat(UIConstants.lobbyImageHeight * 0.75)) {
           // self.lobbyView?.setTitle();

            if (self.navigationController?.navigationBar.translucent == true && navBarVisible == true) {
                navBarVisible = false
                self.title = data.data.name;
                showNavBar();

            }

        } else {
            if (self.navigationController?.navigationBar.translucent == false && navBarVisible==false) {
                navBarVisible = true
                self.title = "";
                hideNavBar();
            }

            if (firstTime == false) {
                var amount = 1.0 - (1.0 * (CGFloat(scrollView.contentOffset.y) / CGFloat(UIConstants.lobbyImageHeight)))
                lobbyView?.scaleImage(amount)

                if ((scrollView.contentOffset.y > 0) && (scrollView.contentOffset.y < CGFloat(UIConstants.lobbyImageHeight))) {
                    lobbyView?.scrollImage(scrollView.contentOffset.y);
                }


            } else {
                firstTime = false;
            }
        }



    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.whiteColor()
        println("view did load single lobby")
        println(self.data.recievedMessages)

        self.lobbyView?.setUpViews(self.data)

       lobbyView?.setHeaderCover(self.imageCover);

        let settingsIcon = UIImage(named: "settingsIcon")
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: settingsIcon, style: UIBarButtonItemStyle.Plain, target: self, action: "toggleMembersDrawer")

        UIApplication.sharedApplication().statusBarStyle = .LightContent

        registerForKeyboardNotifications()


        println(data.data.messages);
        self.lobbyView?.reloadTable();


    }

    func toggleMembersDrawer() {
        println("toggle member's drawer")
        self.membersList.toggleDrawer();
    }


    //SOCIAL START

    //set up
    func setUpSharing(contentView: UIView) {
        println("sharing should happen")

        socialView = SocialButtonsView();
        contentView.addSubview(self.socialView!);
        socialView!.setUpButtons(self)

        socialController = SocialConnector();
        socialController?.cancelOauth = self.socialView!.cancelled;

    }

    func reloadData() {
        self.lobbyView?.reloadTable();
        self.data.data.unreadMessageCount = 0;
        if (data.lastMessageIsUser()) {
            self.lobbyView?.scrollToBottom(0.5);
        }
    }


    //touch down doesn't get used, but part of protocol
    func touchDown(button: NSObject, type: String) {
            println(type)
    }

    //clicked on social toggle
    func touchUp(button: NSObject, type: String) {

        if (type == "send") {
            println(type)
            socialController?.sendInvites(button as! Array<SocialToggle>, lobbyData: self.data.data)
        } else if (currentUser.loggedIn()) {
            var socialToggle = button as! SocialToggle;
            if (socialToggle.checked == false) {
                //if its false that means it is about to turn true, it turns to checked after this
                //function runs
                socialController?.setTypeAndAuthenticate(type);
            }
        } else {
                socialController?.service = type;
                socialController?.cancelOauth?(type);
                joinPupButton?.touchUp(button, type: type)

        }

    }


    //SOCIAL START


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



    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated);
        nav!.tabBar.alpha = 0;


        self.data.data.setAllRead()
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

        nav!.tabBar.alpha = 1;
        UIApplication.sharedApplication().statusBarStyle = .Default


        if (vcs.indexOfObject(self) == NSNotFound) {
            println("popped view")


            joinPupButton?.removeRegistrationView();
        }


    }






    func recievedMessages() {
        println("got em!")


            self.reloadData();
            //self.lobbyView?.clearText();

            if (socialController != nil) {
                socialView?.removeView();
            }







    }








    override func viewDidLayoutSubviews() {  //set up layer properties that don't get set by constraints
            super.viewDidLayoutSubviews();
        println("view did layout subviews")


        if ((data.data.isMember && (data.hasMessages() && hasAnimatedDown == false) || data.isEmpty()) && hasAnimatedDown == false) {
            self.lobbyView?.reloadTable();
            var rowAndSection = NSIndexPath(forRow: self.data.data.messages.count - 1, inSection: 1);
            self.lobbyView?.table?.scrollToRowAtIndexPath(rowAndSection, atScrollPosition: UITableViewScrollPosition.Bottom, animated: false);
            self.lobbyView?.moveCoverImage()
            hasAnimatedDown = true;
        }





    }


    func joinLobby() {
        println("joining this lobby")
        var config = SwiftLoader.Config()
        config.size = 150
        config.spinnerColor = UIColor(rgba: colors.orange)
        config.backgroundColor = UIColor.whiteColor()

        SwiftLoader.setConfig(config);
        SwiftLoader.show(title: "Loading...", animated: true)
        if (submitting==false) {
            submitting = true;
            data.joinLobby({
                self.submitting = false;
                println("successfully joined")
                self.data.addSelfToMembers();
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

    }


    func sendMessage(message: String) {
        if (message != "") {
            self.data.sendMessage(message);
//            self.data.quickBloxConnect?.sendMessage(message);
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

    func checkFirstResponder() -> Bool {
        if (self.lobbyView!.newMessage.isFirstResponder()) {
            return true;
        }
        return false;
    }


    //table view functions



    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (section==0) {
           return 1
        } else {
            if (self.data.hasMessages()) {
                if (self.data.showingShare()) {
                    return 1;
                } else {
                    return self.data.data.messages.count;
                }
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
            if (self.data.showingShare()) {
                println("no messages, let's share")
                let cell = UITableViewCell();
                setUpSharing(cell.contentView);

                return cell;

            } else if (self.data.hasMessages()) {
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
            return CGFloat(UIConstants.lobbyImageHeight) + CGFloat(140.0)
        }
        if (self.data.hasMessages()) {
            if (self.data.showingShare()) {
                var height = ((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165)) * 3;
                return ((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165)) * 3;
            } else {
                return CGFloat(UIConstants.messageBaseSize) + UIConstants.getMessageAddition(self.data.data.messages[indexPath.row].message);

            }
        }

        return UIScreen.mainScreen().bounds.size.height - (CGFloat(UIConstants.lobbyImageHeight) + CGFloat(150.0));


    }


    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
        var cell = tableView.cellForRowAtIndexPath(indexPath);
        cell?.backgroundColor = UIColor.clearColor()

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


