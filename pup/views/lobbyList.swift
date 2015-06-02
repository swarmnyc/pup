//
//  lobbyList.swift
//  pup
//
//  Created by Alex Hartwell on 5/18/15.
//  Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit



class LobbyListView: UIView {

    var table: UITableView = UITableView()
    var swipeDetectionView = UIView()
    var panDetector = UIPanGestureRecognizer()
    var swipeDelegate: PanGestureDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor=UIColor.blackColor()

        clipsToBounds = true;

        setUpTable();
        setUpTableConstraints();


    }





    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setDelegates(delegate: UITableViewDelegate, dataSource: UITableViewDataSource) {
        table.delegate = delegate;
        table.dataSource = dataSource;
    }

    func setUpTable() {
        table.separatorInset = UIEdgeInsetsZero
        self.panDetector.addTarget(self, action: "swiped:");

        self.swipeDetectionView.addGestureRecognizer(panDetector);
        self.swipeDetectionView.userInteractionEnabled = true;


        addSubview(table);
        addSubview(swipeDetectionView)
    }

    func swiped(sender: UIPanGestureRecognizer) {
        println("haha!")
        self.swipeDelegate?.swiped(sender);

    }


    func setUpTableConstraints() {
        table.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
        }
        swipeDetectionView.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0);
            make.top.equalTo(self).offset(0);
            make.bottom.equalTo(self).offset(0);
            make.width.equalTo(20);
        }
    }



}









