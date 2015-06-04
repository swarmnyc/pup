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
    var FAB: floatingAction?
    var fabDelegate: FABDelegate?
    var darkened = false;


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


    func setDelegates(delegate: UITableViewDelegate, dataSource: UITableViewDataSource, fabDelegate: FABDelegate) {
        table.delegate = delegate;
        table.dataSource = dataSource;
        FAB?.fabDelegate = fabDelegate
    }

    func setUpTable() {
        table.separatorInset = UIEdgeInsetsZero
        self.panDetector.addTarget(self, action: "swiped:");

        self.swipeDetectionView.addGestureRecognizer(panDetector);
        self.swipeDetectionView.userInteractionEnabled = true;


        let singleTap = UITapGestureRecognizer(target: self, action: Selector("tapDetected"))
        singleTap.numberOfTapsRequired = 1



        let fabImage = UIImage(named: "FAB")
        FAB = floatingAction()
        FAB?.image = fabImage;
        FAB?.contentMode = UIViewContentMode.ScaleAspectFill
        FAB?.clipsToBounds = true;
        FAB?.userInteractionEnabled = true


        addSubview(table);
        addSubview(FAB!)

        addSubview(swipeDetectionView)
    }


    func pushFab() {

        UIView.animateWithDuration(Double(0.3)) {
            let trans = CGAffineTransformMakeScale(1.1, 1.1);
            self.FAB?.transform = trans;
            self.layoutIfNeeded()
        }

    }

    func releaseFab() {
        UIView.animateWithDuration(Double(0.2)) {
            let trans = CGAffineTransformMakeScale(1.0, 1.0);
            self.FAB?.transform = trans;
            self.layoutIfNeeded()
        }

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

        FAB?.snp_makeConstraints{ (make) -> Void in
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.width.equalTo(50)
            make.height.equalTo(50)
        }
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








