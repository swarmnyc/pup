//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore
import SwiftLoader

class SingleLobbyController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextViewDelegate, SimpleButtonDelegate {

    var lobbyView: SingleLobbyView?;
    var data: singleLobby = singleLobby();
    var submitting = false;
    var descCell = SingleLobbyTopCell()
    var imageCover: UIView = UIView()
    var joinPupButton: JoinPupButton? = nil
    var hasAnimatedDown: Bool = false;
    var membersList: MembersDrawerController = MembersDrawerController();
    var navBarVisible: Bool = false;

    var socialController: SocialConnector?
    var socialView: SocialButtonsView?

    var canLoadMore: Bool = false;
    var shouldLoadMore: Bool = true;


    var messageCount: Int = 0;
    var scrollCount: Int = 0;
    var justStarted: Bool = true;

    var viewWasPopped: Bool = false;

    var flagPopUp = SNYError(alertTitle: "Flag This Comment", alertText: "Why is it so offensive?", alertStyle: UIAlertViewStyle.PlainTextInput);

    var animationDelegate: MainScreenAnimationDelegate?

    convenience init(info: LobbyData) {
        //to do add in right side members drawer nav button
        self.init();


        //lets set those modal styles so that the animation can be smooth (to get rid of the drop shadow on the view)
        self.modalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;
        self.hidesBottomBarWhenPushed = true;
        
        self.flagPopUp.lobbyID = info.id;

        //connect the delegate methods from quickblox
        data.passMessagesToController = recievedMessages;
        data.reloadData = reloadData;


        //set the data and the messages loaded indicator
        data.data = info;
        data.data.reloadData = self.reloadData;
        data.recievedMessages = info.recievedMessages;
        data.data.setMessagesRecieved = data.messagesReceived; //let the controller know it has recieved messages

        //connect the methods for clearing text
        data.data.clearTheText = data.clearText;

        //set up the description cell
        descCell.setUpCell(data)


        //if the user isn't logged in, we have to bug them to register
        if (currentUser.loggedIn() == false) {
            self.joinPupButton = JoinPupButton(aboveTabBar: false)
            self.joinPupButton?.onSuccessJoin = self.joinLobby
            self.addChildViewController(self.joinPupButton!)

        }

    }


    func startFlagging(userName: String, messageString: String) {
        flagPopUp.showIt(messageString, user: userName);
    }
    

    func setAnimationDelegate(delegate: MainScreenAnimationDelegate) {
        self.animationDelegate = delegate;
    }

    override func loadView() {
        self.lobbyView = SingleLobbyView();

        //set up the view and pass in delegate
        self.view = self.lobbyView;
        self.lobbyView?.createTable();
        self.lobbyView?.addTable(self);

        //set up sideBar of members
        membersList.setNavigationBar(self);


        //set this just incase, it gets overridden to 1 in both cases if the navigation bar gets hidden (if we don't hide the images when the
        //navigation bar is down when typing in the new message box there is a flicker for some reason)
        self.lobbyView?.lobbyImgBack.alpha = 0;
        self.lobbyView?.lobbyImg.alpha = 0;

        //transform everything off screen and bring it back
        var transform = CGAffineTransformMakeTranslation(UIScreen.mainScreen().bounds.width, 0);
        self.lobbyView?.transform = transform;



        //send the command back to the previvous screen to start the animation on that end and get rid of those cells
        var finalDelay = self.animationDelegate?.animateLobbyCellsAway(nil, animateNavBar: true);
        self.animateAwayTabBar(finalDelay!);



    }


    func animateAwayTabBar(speed: Double) {
        UIView.animateWithDuration(0.5, animations: {
            var transform = CGAffineTransformMakeTranslation(0,0);
            self.lobbyView?.transform = transform;
            transform = CGAffineTransformMakeTranslation(-UIScreen.mainScreen().bounds.width,0);

            nav!.tabBar.transform = transform;

        })
    }

    func showNavBar() {
        //(if we don't hide the images when the
        //navigation bar is down when typing in the new message box there is a flicker for some reason)
        self.lobbyView?.lobbyImgBack.alpha = 0;
        self.lobbyView?.lobbyImg.alpha = 0;

        UIView.animateWithDuration(0.3, animations: {
            self.navigationController?.navigationBar.translucent = false
            self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
            self.navigationController?.navigationBar.shadowImage = nil
            UIApplication.sharedApplication().statusBarStyle = .Default

        })
    }

    func hideNavBar() {

        //the images will be in view so lets make them visible
        self.lobbyView?.lobbyImgBack.alpha = 1;
        self.lobbyView?.lobbyImg.alpha = 1;

        UIView.animateWithDuration(0.3, animations: {
            self.navigationController?.navigationBar.translucent = true
            self.navigationController?.navigationBar.setBackgroundImage(UIImage(), forBarMetrics: UIBarMetrics.Default)
            self.navigationController?.navigationBar.shadowImage = UIImage()
            UIApplication.sharedApplication().statusBarStyle = .LightContent

        })
    }




    //scrolling table view
    func scrollViewDidScroll(scrollView: UIScrollView!) {

        //if scrolling is right past the description
        if (scrollView.contentOffset.y > CGFloat(UIConstants.lobbyImageHeight) + CGFloat(140.0) && self.data.data.isMember) {
            self.canLoadMore = true; //lets give it the ability to load more

            //if scrolling is a bit more than past the description (you are scrolling up and want to see more messages)
            if (scrollView.contentOffset.y < (CGFloat(UIConstants.lobbyImageHeight) + CGFloat(140.0)) + 20 && self.shouldLoadMore) {
                self.shouldLoadMore = false; //take away permission to load more
                println("loading more!");


                self.data.loadMoreHistory({
                    (messages) -> Void in
                    println("loaded more");
                    println(messages);

                    var offsetAddition = self.data.insertMessagesToStartAndGetHeightOfMessages(messages);
                    dispatch_async(dispatch_get_main_queue()) {
                        if (messages.count > 0) {
                            self.reloadDataWithoutScrollDown()
                            self.shouldLoadMore = true;
                        }
                        self.lobbyView?.layoutIfNeeded();

                        var offsetAdd = self.lobbyView!.table!.contentOffset.y + offsetAddition;
                        self.lobbyView?.table?.setContentOffset(CGPointMake(0.0, CGFloat(offsetAdd)), animated: false);
                    }
                }, failure: {
                    println("loaded more failure");

                    self.shouldLoadMore = true;
                }, quickBloxNotConnectedYet: {
                    println("quickblox not connected")
                    self.shouldLoadMore = true;

                })

            }

        }




        //if you have scrolled past the lobby image
        if (scrollView.contentOffset.y > CGFloat(UIConstants.lobbyImageHeight * 0.678)) {
           
            //if the nav bar is invisible let's show it
            if (self.navigationController?.navigationBar.translucent == true && navBarVisible == true) {
                navBarVisible = false
                self.title = data.data.name;
                showNavBar();

            }

            
        //you have scrolled back into the lobby image
        } else {
            
            //if the nav bar is visible we want to get rid of it now
            if (self.navigationController?.navigationBar.translucent == false && navBarVisible==false) {
                //&& (!self.lobbyView!.isTyping())
                navBarVisible = true
                self.title = "";
                hideNavBar();
            }

            
                //lets scale the image if you are scrolling up above the lobby
                var amount = 1.0 - (1.0 * (CGFloat(scrollView.contentOffset.y) / CGFloat(UIConstants.lobbyImageHeight)))
                lobbyView?.scaleImage(amount)

                if ((scrollView.contentOffset.y > 0) && (scrollView.contentOffset.y < CGFloat(UIConstants.lobbyImageHeight))) {
                   lobbyView?.scrollImageAndFadeText(scrollView.contentOffset.y);

                }


        }



    }

    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated);
        if (self.lobbyView?.table?.contentOffset.y < CGFloat(UIConstants.lobbyImageHeight * 0.693)) {
            navBarVisible = true
            self.title = "";
            hideNavBar();

        } else {
            navBarVisible = false
            self.title = data.data.name;
            showNavBar();
        }
        if (self.data.data.isMember) {
            self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .Plain, target: nil, action: nil)
        }
        data.clearInputText = self.lobbyView?.clearText;

        membersList.setUpSideBar();
        membersList.populateUserList(data.data.users, owner: data.data.owner)
        lobbyView?.sendTheMessage = self.sendMessage;
        lobbyView?.setUpDelegates(self)
        self.data.data.setAllRead()



        let settingsIcon = UIImage(named: "settingsIcon")
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: settingsIcon, style: UIBarButtonItemStyle.Plain, target: self, action: "toggleMembersDrawer")


        //"〈"
        //CUSTOM BACK BUTTON UNCOMMENT TO APPLY ANIMATION ON BACK
//        var backButton = UIBarButtonItem(title: "<", style: UIBarButtonItemStyle.Bordered, target: self, action: "backButton");
        var backButton = UIBarButtonItem(title: " ", style: UIBarButtonItemStyle.Bordered, target: self, action: "backButton");
//        backButton.setTitleTextAttributes([NSFontAttributeName: UIFont.systemFontOfSize(17)], forState: .Normal);
        backButton.setTitleTextAttributes([NSFontAttributeName: UIFont(name: "backbutton", size: 20.0)!], forState: UIControlState.Normal);
        self.navigationItem.leftBarButtonItem=backButton;
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        nav!.tabBar.translucent = true;

        println("view did load single lobby")

        self.lobbyView?.setUpViews(self.data)



        UIApplication.sharedApplication().statusBarStyle = .LightContent

        registerForKeyboardNotifications()


        println(data.data.messages);

        if (currentUser.loggedIn() == false) {
            self.joinPupButton?.setUpView(self.lobbyView!)

        }
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

        socialController = SocialConnector(viewController: self);
        socialController?.cancelOauth = self.socialView!.cancelled;

    }



    func reloadDataWithoutScrollDown() {
        self.lobbyView?.reloadTable();
        self.data.data.unreadMessageCount = 0;
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
            socialController?.sendInvites(button as! Array<SocialToggle>, lobbyData: self.data.data, success: {
                self.socialView?.setSubmitButtonText();
            });

        } else if (currentUser.loggedIn()) {
            var socialToggle = button as! SocialToggle;
            if (socialToggle.checked == false) {
                //if its false that means it is about to turn true, it turns to checked after this
                //function runs
                socialController?.setTypeAndAuthenticate(type);
                socialView?.makeSubmitBlue();
            } else {
                socialView?.makeSubmitOrange();

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
       // nav!.tabBar.alpha = 0;

            if (data.data.isMember == false) {
                self.lobbyView?.lobbyImgBack.alpha = 1;
                self.lobbyView?.lobbyImg.alpha = 1;
            }


        self.lobbyView?.reloadTable();

        self.navigationController?.navigationBar.translucent = true
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), forBarMetrics: UIBarMetrics.Default)
        self.navigationController?.navigationBar.shadowImage = UIImage()
        UIApplication.sharedApplication().statusBarStyle = .LightContent


    }
    
    func viewDidApeear() {
        
    }





    override func viewDidDisappear(animated: Bool) {
        super.viewDidDisappear(animated);

        if (self.viewWasPopped) {
            joinPupButton?.removeRegistrationView();
            self.viewWasPopped = true;
        }


    }

        //View is leaving the building
    override func viewWillDisappear(animated: Bool) {
        nav!.tabBar.translucent = false;

        NSNotificationCenter.defaultCenter().removeObserver(self,
                name: UIKeyboardDidShowNotification,
                object: nil)

        NSNotificationCenter.defaultCenter().removeObserver(self,
                name: UIKeyboardWillHideNotification,
                object: nil)

        membersList.removeViews();

        nav!.tabBar.alpha = 1;
        UIApplication.sharedApplication().statusBarStyle = .Default


        var vcs = navigationController?.viewControllers as! NSArray;
        if (vcs.indexOfObject(self) == NSNotFound) {
            println("popped view");

           self.viewWasPopped = true;

        }


    }

    func backButton() {
        animateCellsRight({})
        self.viewWasPopped = true;
        popLobby();

    }



    func popLobby() {
//       self.navigationController?.popViewControllerAnimated(true);
    UIView.animateWithDuration(0.38, animations: {
        var transform = CGAffineTransformMakeTranslation(UIScreen.mainScreen().bounds.width, 0);
        self.lobbyView?.transform = transform;
        transform = CGAffineTransformMakeTranslation(0,0);
        nav!.tabBar.transform = transform;
        self.lobbyView?.backgroundColor = UIColor.clearColor();
    }, completion: {
        finished in
        self.navigationController?.dismissViewControllerAnimated(false, completion: nil);
    })

        var presentingController: LobbyListController? = (self.navigationController!.presentingViewController as? UINavigationController)!.topViewController as? LobbyListController;

        if (presentingController != nil) {
            presentingController?.bringLobbiesBack();
        } else {
            var presentController: MyChatsController? = (self.navigationController!.presentingViewController as! UINavigationController).topViewController as? MyChatsController;
            presentController?.bringLobbiesBack();

        }




    }


    func animateCellsRight(success: (() -> Void)) {
        var cells = self.lobbyView?.table?.visibleCells();

        var speed  = 1.3;
        for (var i = 0; i<cells!.count; i++) {
            println(speed);
            if ((cells![i] as? MessageCell) != nil) {
                var theCell = cells![i] as! MessageCell;

                if (i == cells!.count-1) {
                    theCell.moveRight(speed, success: {
                        success();
                        //self.navigationController?.pushViewController(lobbyView, animated: true);
                    });
                } else {
                    theCell.moveRight(speed, success: nil);
                }
                speed -= 0.2;
                println(theCell);
            }
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
            println("scroll down!!");
            self.lobbyView?.table?.scrollToRowAtIndexPath(rowAndSection, atScrollPosition: UITableViewScrollPosition.Bottom, animated: false);
            hasAnimatedDown = true;
        }





    }


    func joinLobby() {
        println("joining this lobby")
               self.lobbyView?.changeJoinButtonBackgroundColor();

        if (submitting==false) {
            submitting = true;
            self.lobbyView?.joinLobbyButton.addIndicator();
            data.joinLobby({
                self.submitting = false;
                println("successfully joined")
                self.data.addSelfToMembers();
                self.membersList.populateUserList(self.data.data.users, owner: self.data.data.owner);


                self.lobbyView?.hideAndRemoveJoin();
                
            }, failure: {
                println("failed")
                self.lobbyView?.resetJoinButtonBackgroundColor();
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

    func textViewShouldEndEditing(_ textView: UITextView) -> Bool {
        println(textView)

        return true;
    }

    func textViewDidChange(_ textView: UITextView) {

        if (self.lobbyView!.isMessageBiggerThanTextBar()) {
           self.lobbyView?.increaseMessageInputSize();
       }
    }

    func textViewShouldReturn(_ textView: UITextView) -> Bool {
        textView.resignFirstResponder();

        println(textView.text);
        sendMessage(textView.text)
        return true;
    }

    func checkFirstResponder() -> Bool {
        if (self.lobbyView!.newMessage.isFirstResponder()) {
            return true;
        }
        return false;
    }


    //table view functions


    func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        
        cell.separatorInset = UIEdgeInsetsZero;
        cell.layoutMargins = UIEdgeInsetsZero;

        var visibleRowsIndexPath = tableView.indexPathsForVisibleRows()!;
        var lastIndex = visibleRowsIndexPath[visibleRowsIndexPath.count-1] as! NSIndexPath;

        if (indexPath.row == lastIndex.row) {
            self.justStarted = false;
        }
    }
    
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

                cell.selectionStyle = UITableViewCellSelectionStyle.None;
                cell.backgroundColor = UIColor.whiteColor();
                return cell;

            } else if (self.data.hasMessages()) {
                let cell = tableView.dequeueReusableCellWithIdentifier("message", forIndexPath: indexPath) as! MessageCell

                if (self.justStarted) {
                    println(0.7 + (Double(self.messageCount) * Double(0.1)))
                    cell.setTheAnimationDelay(0.35 + (Double(self.messageCount) * Double(0.075)));
                    self.messageCount++;
                } else {
                    cell.setTheAnimationDelay(0.0)
                }

                cell.setUpCell(self.data.data.messages[indexPath.row], flagCallBack: self.startFlagging)
                cell.selectionStyle = UITableViewCellSelectionStyle.None;
                return cell;
            } else {
                let cell = UITableViewCell();
                cell.contentView.backgroundColor = UIColor.whiteColor();
                cell.textLabel?.text = "Loading...";
                cell.textLabel?.textAlignment = NSTextAlignment.Center
                cell.selectionStyle = UITableViewCellSelectionStyle.None;
                return cell
            }

        }


    }




    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 2;
    }

//    func tableView(tableView: UITableView, estimatedHeightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
//
//        return UITableViewAutomaticDimension;
//
//    }

//- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath
//{
//return UITableViewAutomaticDimension;
//}


    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        if (indexPath.row == 0 && indexPath.section == 0) {
            return CGFloat(UIConstants.lobbyImageHeight) + CGFloat(45) + UIConstants.getMessageHeightAdditionBasedOnTextLength(self.data.data.description) + 8;
        }
        if (self.data.hasMessages()) {
            if (self.data.showingShare()) {
                var height = ((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165)) * 3;
                return ((UIScreen.mainScreen().bounds.size.width / 4) / (312 / 165)) * 3;
            } else {

                var height = CGFloat(UIConstants.messageBaseSize) + UIConstants.getMessageHeightAdditionBasedOnTextLength(self.data.data.messages[indexPath.row].message);
                if (self.data.data.messages[indexPath.row].username == "system message") {
                    height = height * 0.7;
                }
                return height;

            }
        }

        return UIScreen.mainScreen().bounds.size.height - (CGFloat(UIConstants.lobbyImageHeight) + CGFloat(150.0));


    }

    

    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
        var cell = tableView.cellForRowAtIndexPath(indexPath);
        cell?.backgroundColor = UIColor.whiteColor()

        if ((cell! as? MessageCell)?.isCurrentUser() != nil) {
            if ((cell! as? MessageCell)!.isCurrentUser()) {
                cell?.backgroundColor = UIColor(rgba: colors.lightGray)
            }
        }

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


