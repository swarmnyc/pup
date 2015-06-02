//
// Created by Alex Hartwell on 6/1/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SearchResultsController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate {

    var resultsView: SearchResultsView?
    var parentController: UIViewController?
    var searchBar: UISearchBar?

    convenience init(parent: UIViewController, searchBar: UISearchBar) {
        self.init();
        parentController = parent;
        self.searchBar = searchBar;
        println("searchResultsController")
    }

    override func loadView() {
            println("loadView = search results")
        resultsView = SearchResultsView();
        self.view = resultsView;

    }

    override func viewDidLoad() {
        super.viewDidLoad();
        println("view did load - search results")
        println("searchResultsViewDidLoad");
        resultsView?.setUpView(self, parentView: parentController!.view, searchBar: searchBar!);



    }

    func displayResults(searchBar: UISearchBar) {
        resultsView?.openResults(searchBar)
    }

    func hideResults() {
        resultsView?.closeResults()

    }


    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 14
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier("searchResult", forIndexPath: indexPath) as! UICollectionViewCell
        cell.backgroundColor = UIColor.orangeColor()
        cell.frame.size = CGSize(width: self.view.frame.size.width, height: 45)
        cell.frame.origin.x = 0;
        //cell.frame = CGRect(x: 0, y: 0, width: self.view.frame.size.width, height: 45);
        return cell
    }




}
