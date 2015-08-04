//
// Created by Alex Hartwell on 6/1/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SearchResultsView: UIView {

    var parentView: UIView?
    var results: UICollectionView?
    var searchBar: UISearchBar?
    var gameMissing: GameMissingView = GameMissingView()

    var maxHeight = 225.0;
    var maxHeightResults = 180.0;


    func setUpView(parent: UIViewController, parentView: UIView, searchBar: UISearchBar) {
        self.parentView = parentView; //set values that are passed in
        self.searchBar = searchBar

        self.searchBar?.enablesReturnKeyAutomatically = false //enable the search button at all times




        gameMissing.setDelegate(parent as! SimpleButtonDelegate) //request game cell



        self.clipsToBounds = true;                                  //lets get this table going
        let layout: animatedFlowLayout = animatedFlowLayout()
        layout.sectionInset = UIEdgeInsetsZero;
        layout.itemSize = CGSize(width: 250, height: 45);
        layout.minimumInteritemSpacing = 0.0;
        layout.minimumLineSpacing = 0.0;
        results = UICollectionView(frame: self.frame, collectionViewLayout: layout)
        results!.dataSource = parent as? UICollectionViewDataSource;
        results!.delegate = parent as? UICollectionViewDelegate
        results!.registerClass(SearchResultsViewCell.self, forCellWithReuseIdentifier: "searchResult")
        results!.backgroundColor = UIColor(rgba: colors.lightGray)

        self.addViews();
        self.setUpConstraints();


    }





    func addViews() {
        self.addSubview(results!);
        self.parentView?.addSubview(self);

        self.addSubview(gameMissing);

        self.bringSubviewToFront(gameMissing);
    }


    func setUpConstraints() {
        println("results set up constraints!")
        println(self.maxHeightResults)
        println(self.maxHeight)
        self.snp_remakeConstraints{(make) -> Void in
            make.left.equalTo(self.searchBar!.snp_left).offset(0)
            make.top.equalTo(self.searchBar!.snp_bottom).offset(0)
            make.right.equalTo(self.searchBar!.snp_right).offset(0)
            make.height.equalTo(0)
        }

        results!.snp_remakeConstraints{ (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(self.maxHeightResults)


        }

        gameMissing.snp_remakeConstraints{ (make) -> Void in
            make.bottom.equalTo(results!.snp_bottom).offset(45)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(45)
        }



    }


    func openResults() {
        println("opening it!!! (the search results should be animating)")

        UIView.animateWithDuration(Double(0.5)) {
            self.snp_updateConstraints{(make) -> Void in

                make.height.equalTo(self.getHeight())
            }
            self.gameMissing.snp_remakeConstraints{ (make) -> Void in
                make.bottom.equalTo(self).offset(0)
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.height.equalTo(45)
            }
            self.parentView?.setNeedsLayout();
        }
    }

    func closeResults() {
        println("!!!!closing results!!!!!")
    UIView.animateWithDuration(Double(0.5)) {
        self.snp_updateConstraints{(make) -> Void in

            make.height.equalTo(0)
        }
           self.parentView?.setNeedsLayout();
        }




    }

    func getHeight() -> Float {
        let parentController = results!.delegate as! SearchResultsController;
        println(parentController.data)
        println(parentController.data?.count)
        let count: Int? = parentController.data?.count
        if (count != nil) {
            var height = (Float(count!)+1) * 45.0;
            if (height > Float(self.maxHeight)) {
                return Float(self.maxHeight);
            }

            return height;
        } else {


            return Float(self.maxHeight)
        }
    }



}



class GameMissingView: UIView {

    var simpleButtonDelegate: SimpleButtonDelegate?
    var plusSign: UIImageView = UIImageView()
    var requestText:UILabel = UILabel()

    override func touchesBegan( touches: Set<NSObject>, withEvent event: UIEvent) {
        simpleButtonDelegate?.touchDown(self, type: "missing");
        self.backgroundColor = self.backgroundColor!.darkerColor(0.2)

    }

    override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
        simpleButtonDelegate?.touchUp(self, type: "missing");
        self.backgroundColor = self.backgroundColor!.lighterColor(0.2)


    }

    func setDelegate(simpleButtonDelegate: SimpleButtonDelegate) {
        self.simpleButtonDelegate = simpleButtonDelegate;
        self.userInteractionEnabled = true;

        self.backgroundColor = UIColor(rgba: colors.lightGray)
        let plus = UIImage(named: "plus")
        plusSign.image = plus;
        plusSign.contentMode = UIViewContentMode.ScaleAspectFill

        requestText.text = "Not showing up? Request a Game."
        requestText.textColor = UIColor(rgba: colors.midGray);
        requestText.font = UIFont(name: "AvenirNext-Regular", size: 12.0)
        self.addSubview(plusSign)
        self.addSubview(requestText)

        plusSign.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding * 1.25)
            make.top.equalTo(self).offset(UIConstants.horizontalPadding * 1.25)
            make.bottom.equalTo(self).offset(-UIConstants.horizontalPadding * 1.25)
            make.right.equalTo(self.snp_left).offset(45 - UIConstants.horizontalPadding * 1.25)
        }

        requestText.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(plusSign.snp_right).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self).offset(UIConstants.horizontalPadding * 1.25)
            make.bottom.equalTo(self).offset(-UIConstants.horizontalPadding * 1.25)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding);
        }

    }




}