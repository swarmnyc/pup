//
// Created by Alex Hartwell on 6/1/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import uservoice_iphone_sdk

class SearchResultsController: UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, SimpleButtonDelegate {

    var resultsView: SearchResultsView?
    var parentController: UIViewController?
    var searchBar: UISearchBar?
    var results: NSDictionary?
    var data: Array<gameData>?
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



    func touchUp(button: NSObject, type: String) {
        println("touched up request game")
        var config: UVConfig = UVConfig(site: "swarmnyc.uservoice.com")
        config.forumId = 272754;
        config.showKnowledgeBase = true;
        config.showForum = true;
        UserVoice.initialize(config);
        UserVoice.presentUserVoiceNewIdeaFormForParentViewController(self);
    }

    func touchDown(button: NSObject, type: String) {
        println("touched down request game")
    }

    func giveResults(data: Array<gameData>) {

            println(data.count)
            self.data = data;

        dispatch_async(dispatch_get_main_queue()) {
                            self.resultsView?.results?.reloadData();
//            self.resultsView?.results?.star
//                    var indexPath = NSIndexPath(indexes: [self.data!.count], length: 1);
//                    self.resultsView?.results?.insertItemsAtIndexPaths(indexPath)

//                            self.resultsView?.results?.beginUpdate();
//                            for
//            self.resultsView?.results?.reloadSections(NSIndexSet(indexesInRange: NSMakeRange(0, self.resultsView!.results!.numberOfSections())));
//            self.resultsView?.results?.endEditing(true)
            //                self.resultsView?.results?.reloadData();
            return

        }



    }

    func displayResults() {
        resultsView?.openResults()
    }

    func hideResults() {
        resultsView?.closeResults()

    }


    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if data != nil {
            return data!.count
        } else {
            return 0;
        }
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier("searchResult", forIndexPath: indexPath) as! SearchResultsViewCell
        cell.frame.size = CGSize(width: self.resultsView!.frame.size.width, height: 45)
        cell.frame.origin.x = 0;
        println(self.data![indexPath.row])
        println(indexPath.row);
        cell.setUpCell(self.data![indexPath.row])
        cell.layer.shouldRasterize = true;
        cell.layer.rasterizationScale = UIScreen.mainScreen().scale;

        //cell.frame = CGRect(x: 0, y: 0, width: self.view.frame.size.width, height: 45);
        return cell
    }
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        var selectedCell = collectionView.cellForItemAtIndexPath(indexPath) as? SearchResultsViewCell;
        println(selectedCell?.gameName.text!)
        self.searchBar?.text = selectedCell?.gameName.text!

        self.hideResults();


        var searcherDelegate = parentController! as? SearchResultsDelegate
        searcherDelegate!.retreiveData(data![indexPath.item])
        println(data![indexPath.row])

    }




}
