//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import SwiftLoader

class CreateLobbyController: UIViewController, SimpleButtonDelegate,UISearchBarDelegate, SearchResultsDelegate, UIScrollViewDelegate, UITextViewDelegate {

    var createView: CreateLobbyView = CreateLobbyView()
    var searchController: SearchResultsController?;
    var data: SearchResultsModel = SearchResultsModel();

    var newLobbyModel: LobbyCreationModel = LobbyCreationModel();
    var playStyle: HorizontalSelectController?
    var gamerSkill: HorizontalSelectController?

    var logInButton: JoinPupButton?

    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .Gray)




var dateDisplay: DateDisplayView = DateDisplayView();

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }

    override func loadView() {
        self.navigationController?.navigationBar.translucent = false;

        createView.setUpView(self, dateDisplay: dateDisplay);
       self.view = self.createView
    }

    override func viewDidLoad() {
        self.navigationController?.navigationBar.translucent = false;

        super.viewDidLoad()
        currentUser.setPage("Create Lobby")

        //set up search results controller
        searchController = SearchResultsController(parent: self, searchBar: createView.searchBar);

        //set up scrolling selectors
        playStyle = HorizontalSelectController(parent: self, options: ["CASUAL", "SOCIAL", "HARDCORE"], title: "PLAY STYLE", defaultSelection: 1)
        gamerSkill = HorizontalSelectController(parent: self, options: ["NEWBIE", "EASY", "MEDIUM", "HARD", "NIGHTMARE"], title: "GAMER SKILL", defaultSelection: 2)

        createView.containerView.addSubview(playStyle!.view)
        createView.containerView.addSubview(gamerSkill!.view)
        setUpDateCallbacks(updateDate, newTime: updateTime)

        playStyle?.setUpView(self.createView.containerView, bottomOffset:  340.0)
        gamerSkill?.setUpView(self.createView.containerView, bottomOffset: 240.0)

        view.addSubview(searchController!.view)

        data.delegate = self;

        if (currentUser.loggedIn() == false) {
            if (logInButton == nil) {
                logInButton = JoinPupButton(aboveTabBar: true)
            }
            logInButton?.onSuccessJoin = createLobby;
            logInButton?.setUpView(self.createView.scrollView)
        }

        var barButton = UIBarButtonItem(customView: activityIndicator);
        self.navigationItem.leftBarButtonItem = barButton;

    }

    func scrollViewDidScroll(scrollView: UIScrollView!) {
        println(scrollView.contentOffset.y);
        var yOffset = scrollView.contentOffset.y;
        if (yOffset <= 0) {
            self.createView.offsetImage(yOffset);
        }
        //logInButton?.bounceView(scrollView.contentSize.height - (scrollView.contentOffset.y+scrollView.frame.height))
    }




    func updateDates(newDate: NSDate) {
        dateDisplay.currentDate = newDate;
    }


    func setUpDateCallbacks(newDate: (newDate: NSDate) -> NSDate, newTime: (newDate: NSDate) -> NSDate) {
        dateDisplay.successfulChange = newDate;
    }

    func updateDate(newDate: NSDate) -> NSDate {
        println(newDate)
        newLobbyModel.changeDateDay(newDate)
        //updateTimeText(newLobbyModel.startTime) time display was removed, no need to update it's text
        updateDates(newLobbyModel.startTime)
        return newLobbyModel.startTime
    }

    func updateTime(newTime: NSDate) -> NSDate {
        println(newTime)
        newLobbyModel.changeDateTime(newTime)
        updateDates(newLobbyModel.startTime)
        return newLobbyModel.startTime
    }


    func createLobby() {

        newLobbyModel.PlayStyle = playStyle?.getCurrentSelection();
        newLobbyModel.GamerSkill = gamerSkill?.getCurrentSelection();
        if (newLobbyModel.checkData()) {


            createView.pressIt();

            createView.closeKeyboard();

            newLobbyModel.createRequest(moveToLobby, failure: {
                var alert = SNYError(alertTitle: "Could Not Create Lobby", alertText: "Please try again", networkRequest: true)
            })

        } else {
            var alert = SNYError(alertTitle: "Could Not Create Lobby", alertText: "Make sure to fill out all of the fields")
            createView.releaseIt();


        }
        println("createLobby")
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        println("view will appear")
        if (currentUser.loggedIn() == false) {
            println("add to view")

                if (currentUser.loggedIn() == false) {
                    if (logInButton == nil) {
                        logInButton = JoinPupButton(aboveTabBar: true)
                    }
                    logInButton?.onSuccessJoin = createLobby;
                    logInButton?.setUpView(self.createView.scrollView)


            }
            //logInButton?.addToAppView()
        }
        self.createView.scrollView.setContentOffset(CGPointMake(0,0), animated: false);

        self.createView.checkViewHeightAndCorrect();

        registerForKeyboardNotifications();

        self.createView.setTextSize();

        self.title = "Create A Game"


        //clear create lobby data

    }

    func moveToLobby(newLobby: LobbyData) {
        self.createView.releaseIt()

        var lobbyController = SingleLobbyController(info: newLobby);


        nav!.selectedIndex = 1;
        //nav!.selectedViewController!.viewDidAppear(true)

       // SwiftLoader.hide()




        ((nav!.viewControllers![1] as! UINavigationController).topViewController as! MyChatsController).pushLobby(lobbyController)


//        var viewControllerArray = NSMutableArray();
//        viewControllerArray.setArray(self.navigationController?.viewControllers as! [AnyObject]!)
//
//        viewControllerArray.replaceObjectAtIndex(viewControllerArray.count - 1,withObject: lobbyView)
//        SwiftLoader.hide()
//        self.navigationController?.setViewControllers(viewControllerArray as [AnyObject], animated: true)
    }

    //Get text from the description editor
    func textViewShouldEndEditing(textView: UITextView) -> Bool {
        println(textView.text)


        textView.resignFirstResponder();
        newLobbyModel.description = textView.text;

        return true
    }

    func textView(textView: UITextView, shouldChangeTextInRange range: NSRange, replacementText text: String) -> Bool {
        if (text == "\n") {
            textView.resignFirstResponder()

            if (textView.text == "" || textView.text == "\n") {
                textView.text = UIConstants.descriptionPlaceholder;
            }

            return false
        }

        newLobbyModel.description = textView.text;


        return true

    }

    func keyboardWillShow(notification: NSNotification)
    {
       println("keyboard opened!")
        println(notification)
        createView.shortenView(notification)
        if (logInButton != nil && createView.descriptionEditor.firstResponderCheck()) {
            logInButton?.shortenView(notification)
        }

    }

    func keyboardWillBeHidden(notification: NSNotification)
    {
      createView.restoreView(0.5);

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

    override func viewWillDisappear(animated: Bool) {
        NSNotificationCenter.defaultCenter().removeObserver(self,
                name: UIKeyboardDidShowNotification,
                object: nil)

        NSNotificationCenter.defaultCenter().removeObserver(self,
                name: UIKeyboardWillHideNotification,
                object: nil)
        if (logInButton != nil) {
            //logInButton?.removeRegistrationView();
        }


    }


    func textViewDidBeginEditing(textView: UITextView) {

        println(textView.text)
        if (textView.text == UIConstants.descriptionPlaceholder) {
            textView.text = "";
        }
        textView.becomeFirstResponder();
    }



    //get game data from the game search drop down
    func retreiveData(data: gameData) {
        var imageURL: String = data.PictureUrl
        println(imageURL)
        println("got it!")
        createView.setImage(imageURL);
        //GAME SELECT PLATFORM FILTERING HERE

        newLobbyModel.possiblePlatforms = data.Platforms;

        createView.hideInactivePlatforms(data.Platforms)

        println(data.Platforms)
        println("platform formatting")
        newLobbyModel.gameId = data.id;

    }


    //give results to the drop down
    func handOffResults(data: Array<gameData>) {
        println("display results");

        searchController?.giveResults(data);
        self.activityIndicator.stopAnimating();

        searchController?.displayResults();
    }


    //touched a platform, here is the name and the button object
    func touchUp(button: NSObject, type: String) { //from button delegate
        println(type);
        var theButton = button as! PlatformButtonToggle
        closeEverything();

        createView.uncheckAllPlatforms()

        newLobbyModel.selectedPlatform = theButton.currentTitle!

        println(theButton)
       println(theButton.currentTitle!)
    }


    //touched down on a platform
    func touchDown(button: NSObject, type: String) {
        var theButton = button as! PlatformButtonToggle
        println(theButton.currentTitle!)
    }


    //close the keyboard and the search results
    func closeEverything() {
        createView.closeKeyboard();
        searchController?.resultsView?.closeResults()
    }


    func searchBarTextDidBeginEditing(searchBar: UISearchBar) {
        println(searchController?.resultsView!)
        println("HAAAAAAAA search bar did begin create")
        searchController?.resultsView?.maxHeight = 165;
        searchController?.resultsView?.maxHeightResults = 120;
        searchController?.resultsView?.setUpConstraints();

        println("beginEditing")
    }

    func searchBarTextDidEndEditing(searchBar: UISearchBar) {
        closeEverything();
        println("endEditing")
    }

    func searchBarCancelButtonClicked(searchBar: UISearchBar) {

        println("cancel clicked")
    }

    func searchBarSearchButtonClicked(searchBar: UISearchBar) {
        createView.closeKeyboard();

    }



    func handleDatePicker(sender: UIDatePicker) {
        println(sender.date)
    }

    func searchBar(searchBar: UISearchBar, textDidChange searchText: String) {
        println(searchText)
        if (searchText == "") {
            searchController?.hideResults();
        } else {
            self.activityIndicator.startAnimating();
            data.search(searchText, success: handOffResults, failure: {
                println("failure with search")
            });
        }


    }




}