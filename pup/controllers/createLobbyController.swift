//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class CreateLobbyController: UIViewController, SimpleButtonDelegate,UISearchBarDelegate, SearcherDelegate, UIScrollViewDelegate, UITextFieldDelegate {

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

    }

    func retreiveData(data: gameData) {
        var imageURL: String = data.PictureUrl
        println(imageURL)
        println("got it!")
        createView.setImage(imageURL);


    }

    func handOffResults(data: Array<gameData>) {
        println("display results");
        searchController?.giveResults(data);
        searchController?.displayResults();
    }

    func touchUp(button: NSObject, type: String) { //from button delegate
        println(type);
        var theButton = button as! Button
        closeEverything();

        createView.uncheckAllPlatforms()
        println(theButton)
       println(theButton.currentTitle!)
    }

    func touchDown(button: NSObject, type: String) {
        var theButton = button as! Button
        println(theButton.currentTitle!)
    }

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