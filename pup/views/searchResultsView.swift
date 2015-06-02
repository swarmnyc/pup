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

    func setUpView(parent: UIViewController, parentView: UIView, searchBar: UISearchBar) {
        self.parentView = parentView;
        //parentView?.addSubview(self);
        self.searchBar = searchBar
       // self.backgroundColor = UIColor.blackColor();

        let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsetsZero;
        layout.itemSize = CGSize(width: 250, height: 45);
        results = UICollectionView(frame: self.frame, collectionViewLayout: layout)
        results!.dataSource = parent as? UICollectionViewDataSource;
        results!.delegate = parent as? UICollectionViewDelegate
        results!.registerClass(UICollectionViewCell.self, forCellWithReuseIdentifier: "searchResult")
        results!.backgroundColor = UIColor(rgba: colors.mainGrey)

        self.addViews();
        self.setUpConstraints();


    }



    func addViews() {
        self.addSubview(results!);
        self.parentView?.addSubview(self);
    }


    func setUpConstraints() {

        self.snp_makeConstraints{(make) -> Void in
            make.left.equalTo(self.searchBar!.snp_left).offset(0)
            make.top.equalTo(self.searchBar!.snp_bottom).offset(0)
            make.right.equalTo(self.searchBar!.snp_right).offset(0)
            make.height.equalTo(0)
        }

        results!.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)


        }

    }


    func openResults(searchBar: UISearchBar) {

        UIView.animateWithDuration(Double(0.5)) {
            self.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self.searchBar!.snp_left).offset(0)
                make.top.equalTo(self.searchBar!.snp_bottom).offset(0)
                make.right.equalTo(self.searchBar!.snp_right).offset(0)
                make.height.equalTo(180)
            }
            self.parentView?.layoutIfNeeded()

        }
    }

    func closeResults() {
        UIView.animateWithDuration(Double(0.5)) {
            self.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self.searchBar!.snp_left).offset(0)
                make.top.equalTo(self.searchBar!.snp_bottom).offset(0)
                make.right.equalTo(self.searchBar!.snp_right).offset(0)
                make.height.equalTo(0)
            }
            self.parentView?.layoutIfNeeded()

        }
    }



}