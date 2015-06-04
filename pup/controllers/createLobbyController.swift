//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class CreateLobbyController: UIViewController, SimpleButtonDelegate,UISearchBarDelegate, SearcherDelegate {

    var createView: CreateLobbyView = CreateLobbyView()
    var searchController: SearchResultsController?;
    var data: Searcher = Searcher();
    var playStyle: HorizontalSelectController?


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

        searchController = SearchResultsController(parent: self, searchBar: createView.searchBar);
        playStyle = HorizontalSelectController(parent: self, options: ["NORMAL", "HARDCORE", "3L337", "PRO"])
        view.addSubview(playStyle!.view)
        playStyle?.setUpView(self.view, topOffset: 330.0)
        view.addSubview(searchController!.view)
        data.delegate = self;


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



    func searchBar(searchBar: UISearchBar, textDidChange searchText: String) {
        println(searchText)
        if (searchText == "") {
            searchController?.hideResults();
        } else {
            data.search(searchText);
        }


    }




}