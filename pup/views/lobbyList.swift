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

    override init(frame: CGRect) {
        // 1. setup any properties here

        // 2. call super.init(frame:)
        super.init(frame: frame)

        // 3. Setup view from .xib file
        println ("fun on a bun")
        backgroundColor=UIColor.blackColor()

        clipsToBounds = true;

        setUpTable();
        setUpTableConstraints();


    }





    required init(coder aDecoder: NSCoder) {
        // 1. setup any properties here

        // 2. call super.init(coder:)
        super.init(coder: aDecoder)


    }


    func setDelegates(delegate: UITableViewDelegate, dataSource: UITableViewDataSource) {
        table.delegate = delegate;
        table.dataSource = dataSource;
    }

    func setUpTable() {

        table.separatorInset = UIEdgeInsetsZero





        addSubview(table);


    }





    func setUpTableConstraints() {
        table.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
        }
    }


}









