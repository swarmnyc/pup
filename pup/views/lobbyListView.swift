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


//    var floatingActionButton: floatingAction?
//    var fabDelegate: FABDelegate?


    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor=UIColor(rgba: colors.lightGray)

        clipsToBounds = true;

        setUpTable();
        setUpTableConstraints();


    }





    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setDelegates(delegate: UITableViewDelegate, dataSource: UITableViewDataSource, fabDelegate: FABDelegate) {
        table.delegate = delegate;
        table.dataSource = dataSource;
        //floatingActionButton?.fabDelegate = fabDelegate
    }

    func setUpTable() {
        table.separatorInset = UIEdgeInsetsZero

        table.separatorColor = UIColor.clearColor();
        table.layer.masksToBounds = false;
        table.alwaysBounceVertical = true;
        table.clipsToBounds = false;

//        let singleTap = UITapGestureRecognizer(target: self, action: Selector("tapDetected"))
//        singleTap.numberOfTapsRequired = 1



//        let fabImage = UIImage(named: "FAB")
//        floatingActionButton = floatingAction()
//        floatingActionButton?.image = fabImage;
//        floatingActionButton?.contentMode = UIViewContentMode.ScaleAspectFill
//        floatingActionButton?.clipsToBounds = true;
//        floatingActionButton?.userInteractionEnabled = true


        addSubview(table);
//        addSubview(floatingActionButton!)

        //addSubview(swipeDetectionView)


    }


//    func pushFab() {
//
//        UIView.animateWithDuration(Double(0.3)) {
//            let trans = CGAffineTransformMakeScale(1.1, 1.1);
//            self.floatingActionButton?.transform = trans;
//            self.layoutIfNeeded()
//        }
//
//    }
//
//    func releaseFab() {
//        UIView.animateWithDuration(Double(0.2)) {
//            let trans = CGAffineTransformMakeScale(1.0, 1.0);
//            self.floatingActionButton?.transform = trans;
//            self.layoutIfNeeded()
//        }
//
//    }





    func setUpTableConstraints() {
        table.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(-20)
        }

//        floatingActionButton?.snp_makeConstraints{ (make) -> Void in
//            make.centerX.equalTo(self)
//            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
//            make.width.equalTo(50)
//            make.height.equalTo(50)
//        }

    }



}


class floatingAction: UIImageView {

    var fabDelegate: FABDelegate?


    override func touchesBegan( touches: Set<NSObject>, withEvent event: UIEvent) {
        fabDelegate?.fabTouchDown();

    }

    override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
        fabDelegate?.fabTouchUp();


    }


}








