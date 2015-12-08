//
// Created by Alex Hartwell on 5/22/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class FilterViewController: UIViewController, UISearchBarDelegate, SimpleButtonDelegate, PanGestureDelegate, SearchResultsDelegate, OverlayDelegate {
    var isOpen: Bool = false;
    var filterView: FilterView = FilterView()
    var parent: LobbyListController?
    var searchActive : Bool = false
    var data: SearchResultsModel = SearchResultsModel();
    var numberOfSelections: Int = 0;
    var searchController: SearchResultsController?;
    var searchText: String = "";

    convenience init(parentController: LobbyListController) {
        self.init()

        self.view = filterView
        parent = parentController
        filterView.parentView = parent!.view

        filterView.setUpDelegates(self)
        filterView.setUpViews();
        filterView.clearFilters = self.clearResults;
        //setUpViews()
        searchController = SearchResultsController(parent: self, searchBar: filterView.search);
        //self.searchController.setUpView(self as UIViewController);
        view.addSubview(searchController!.view)

        data.delegate = self;



    }


    func hideOverlay() {
        closeFilter()
    }


    func darkenOverlay() {

    }

    func retreiveData(data: gameData) { //from search results
        //empty here
    }

    func handOffResults(newdata: Array<gameData>) {
        println("display results");
        searchController?.giveResults(newdata);
        parent?.activityIndicator.stopAnimating();

        searchController?.displayResults();

    }

    func touchUp(button: NSObject, type: String) { //from button delegate
        var theButton = button as! PlatformButtonToggle
        var added = setSelectionCount(theButton.checked)
        println(theButton.currentTitle!);
        if (!added) {
            data.removePlatformFromSearch(theButton.currentTitle!);
        } else {

            data.addPlatformToSearch(theButton.currentTitle!);
        }
        setDimOfOtherButtons()

    }

    func touchDown(button: NSObject, type: String) {

    }


    func setSelectionCount(isChecked: Bool) -> Bool {
        if (!isChecked) {
            numberOfSelections++
            return true
        } else {
            numberOfSelections--
            return false
        }

    }


    func swiped(sender: UIPanGestureRecognizer) {

        self.isOpen = self.filterView.setTranslation(sender, opened: isOpen);
        println(self.isOpen);

    }

    func setDimOfOtherButtons() {
        println("number of selections: \(numberOfSelections)")
        if (numberOfSelections>0) {
            filterView.dimUncheckedButtons()
        } else {
            filterView.darkenAllButtons()
        }


    }

    func clearResults() {
        self.data.clearData()
        self.numberOfSelections = 0;
        setDimOfOtherButtons();
    }






    func toggleState() {
        if isOpen {
            closeFilter();

        } else {
            isOpen = true
            filterView.openFilter()
        }
        println(isOpen)
        println(filterView.platforms[0])
    }

    func closeFilter() {
        isOpen = false
        filterView.closeFilter()
        if (!self.data.isSameSearchTerm(self.searchController!.getSearchText(), platforms: data.platforms)) {
            parent?.activityIndicator.startAnimating();
            parent?.loadNewLobbies(self.searchController!.getSearchText(), platforms: data.platforms);
        }

        searchController?.hideResults();
    }




    func searchBarTextDidBeginEditing(searchBar: UISearchBar) {
        searchActive = true;
        println("beginEditing")
    }

    func searchBarTextDidEndEditing(searchBar: UISearchBar) {
        searchActive = false;
        println("endEditing")
    }

    func searchBarCancelButtonClicked(searchBar: UISearchBar) {
        searchActive = false;
        println("cancel clicked")
    }

    func searchBarSearchButtonClicked(searchBar: UISearchBar) {
        searchActive = false;
        println("searchButtonClicked")
        data.setSearchTerm(searchBar.text)
        
        toggleState();
    }


    func pullToRefresh() {
        data.search(searchText, success: handOffResults, failure: {
            println("failure in search");
            self.parent?.activityIndicator.stopAnimating();
        })
    }


    func searchBar(searchBar: UISearchBar, textDidChange searchText: String) {
        println(searchText)
        self.searchText = searchText;

        if (searchText == "") {
            searchController?.hideResults();
        } else {
            parent?.activityIndicator.startAnimating();
            data.search(searchText, success: handOffResults, failure: {
                println("failure in search")
                self.parent?.activityIndicator.stopAnimating();

            });
        }

    }



}