//
// Created by Alex Hartwell on 5/22/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class FilterViewController: UIViewController, UISearchBarDelegate, SimpleButtonDelegate, PanGestureDelegate {
    var isOpen = false;
    var filterView: FilterView = FilterView()
    var parent: UIViewController = UIViewController()
    var searchActive : Bool = false
    var data: Searcher = Searcher();
    var numberOfSelections: Int = 0;
    var searchController: SearchResultsController?;

    convenience init(parentController: UIViewController) {
        self.init()

        self.view = filterView
        parent = parentController
        filterView.parentView = parent.view

        filterView.setUpDelegates(self, buttondelegate: self)
        filterView.setUpViews();
        //setUpViews()
        searchController = SearchResultsController(parent: self, searchBar: filterView.search);
        //self.searchController.setUpView(self as UIViewController);
        view.addSubview(searchController!.view)




    }




    func touchUpInside(button: Button) { //from button delegate
        var added = setSelectionCount(button.checked)
        println(button.currentTitle!);
        if (added) {
            data.addPlatformToSearch(button.currentTitle!);
        } else {
            data.removePlatformFromSearch(button.currentTitle!);

        }
        setDimOfOtherButtons()

    }


    func setSelectionCount(isChecked: Bool) -> Bool {
        if (isChecked) {
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
        if (numberOfSelections>0) {
            filterView.dimInactiveButtons()
        } else {
            filterView.darkenAllButtons()
        }


    }


    func toggleState() {
        if isOpen {
            isOpen = false
            filterView.closeFilter()
            searchController?.hideResults();

        } else {
            isOpen = true
            filterView.openFilter()
        }
        println(isOpen)
        println(filterView.platforms[0])
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
        toggleState();
    }



    func searchBar(searchBar: UISearchBar, textDidChange searchText: String) {
        println(searchText)


        if (searchText == "") {
            searchController?.hideResults();
        } else {
            searchController?.displayResults(searchBar);
            data.search(searchText);
        }

    }



}
