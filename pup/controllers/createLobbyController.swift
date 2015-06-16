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


    var dateDisplay: DateDisplayView = DateDisplayView();
    var timeDisplay: TimeDisplayView = TimeDisplayView();

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }

    override func loadView() {
        createView.setUpView(self, dateDisplay: dateDisplay, timeDisplay: timeDisplay);
       self.view = self.createView
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        currentUser.setPage("Create Lobby")

        //set up search results controller
        searchController = SearchResultsController(parent: self, searchBar: createView.searchBar);

        //set up scrolling selectors
        playStyle = HorizontalSelectController(parent: self, options: ["NORMAL", "HARDCORE", "3L337", "PRO"], title: "PLAY STYLE")
        gamerSkill = HorizontalSelectController(parent: self, options: ["NOOB", "CASUAL", "3L337", "PRO"], title: "GAMER SKILL")

        createView.containerView.addSubview(playStyle!.view)
        createView.containerView.addSubview(gamerSkill!.view)
        setUpDateCallbacks(updateDate, newTime: updateTime)

        playStyle?.setUpView(self.createView.containerView, bottomOffset:  300.0)
        gamerSkill?.setUpView(self.createView.containerView, bottomOffset: 200.0)

        view.addSubview(searchController!.view)

        data.delegate = self;

        if (currentUser.loggedIn() == false) {
            logInButton = JoinPupButton(parentController: self)
            logInButton?.setNewView(createView.containerView)
            logInButton?.onSuccessJoin = createLobby;
        }


        registerForKeyboardNotifications()
    }


    func updateTimeText(newDate: NSDate) {
        timeDisplay.setNewText(newDate)
    }

    func updateDates(newDate: NSDate) {
        timeDisplay.currentDate = newDate;
        dateDisplay.currentDate = newDate;
    }


    func setUpDateCallbacks(newDate: (newDate: NSDate) -> NSDate, newTime: (newDate: NSDate) -> NSDate) {
        dateDisplay.successfulChange = newDate;
        timeDisplay.successfulChange = newTime;
    }

    func updateDate(newDate: NSDate) -> NSDate {
        println(newDate)
        newLobbyModel.changeDateDay(newDate)
        updateTimeText(newLobbyModel.startTime)
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

            var config = SwiftLoader.Config()
            config.size = 150
            config.spinnerColor = UIColor(rgba: colors.orange)
            config.backgroundColor = UIColor(rgba: colors.mainGrey)
            SwiftLoader.setConfig(config);

            createView.closeKeyboard();

            SwiftLoader.show(title: "Loading...", animated: true)
            newLobbyModel.createRequest(moveToLobby, failure: {
                SwiftLoader.hide()

            })

        } else {
            var alert = Error(alertTitle: "Could Not Create Lobby", alertText: "Make sure to fill out all of the fields")
            SwiftLoader.show(title: "Loading...", animated: true)
            SwiftLoader.hide()

        }
        println("createLobby")
    }


    func moveToLobby(newLobby: LobbyData) {
        let lobbyView = SingleLobbyController(info: newLobby)
       // self.navigationController?.popViewControllerAnimated(false)
        //autoreleasepool()
        var viewControllerArray = NSMutableArray();
        viewControllerArray.setArray(self.navigationController?.viewControllers as! [AnyObject]!)

        viewControllerArray.replaceObjectAtIndex(viewControllerArray.count - 1,withObject: lobbyView)
        SwiftLoader.hide()
        self.navigationController?.setViewControllers(viewControllerArray as [AnyObject], animated: true)
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

        return true

    }

    func keyboardWillShow(notification: NSNotification)
    {
       println("keyboard opened!")
        println(notification)
        createView.shortenView(notification)

    }

    func keyboardWillBeHidden(notification: NSNotification)
    {
      createView.restoreView();

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
            data.search(searchText, success: handOffResults, failure: {
                println("failure with search")
            });
        }


    }




}