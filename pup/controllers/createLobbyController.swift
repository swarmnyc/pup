//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class CreateLobbyController: UIViewController, SimpleButtonDelegate,UISearchBarDelegate, SearcherDelegate, UIScrollViewDelegate, UITextViewDelegate {

    var createView: CreateLobbyView = CreateLobbyView()
    var searchController: SearchResultsController?;
    var data: Searcher = Searcher();
    var playStyle: HorizontalSelectController?
    var gamerSkill: HorizontalSelectController?
    var logInButton: JoinButton? = nil

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }

    override func loadView() {
        createView.setUpView(self);
       self.view = self.createView
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        currentUser.setPage("Create Lobby")

        searchController = SearchResultsController(parent: self, searchBar: createView.searchBar);
        playStyle = HorizontalSelectController(parent: self, options: ["NORMAL", "HARDCORE", "3L337", "PRO"], title: "PLAY STYLE")
        gamerSkill = HorizontalSelectController(parent: self, options: ["NOOB", "CASUAL", "3L337", "PRO"], title: "GAMER SKILL")
        //scrollView.panGestureRecognizer.requireGestureRecognizerToFail(mySwipe)

        createView.containerView.addSubview(playStyle!.view)
        createView.containerView.addSubview(gamerSkill!.view)
        playStyle?.setUpView(self.createView.containerView, bottomOffset:  300.0)
        gamerSkill?.setUpView(self.createView.containerView, bottomOffset: 200.0)
        view.addSubview(searchController!.view)
        data.delegate = self;

        if (currentUser.loggedIn() == false) {
            logInButton = JoinButton(parentController: self)
            logInButton?.setNewView(createView.containerView)
        }


        registerForKeyboardNotifications()
    }



    //Get text from the description editor
    func textViewShouldEndEditing(textView: UITextView) -> Bool {
        println(textView.text)


        textView.resignFirstResponder();
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
        var theButton = button as! Button
        closeEverything();

        createView.uncheckAllPlatforms()
        println(theButton)
       println(theButton.currentTitle!)
    }


    //touched down on a platform
    func touchDown(button: NSObject, type: String) {
        var theButton = button as! Button
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
            data.search(searchText);
        }


    }




}