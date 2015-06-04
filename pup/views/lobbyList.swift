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
    var overlay: UIView = UIView()
    var FAB: floatingAction?
    var fabDelegate: FABDelegate?
    var overlayDelegate: OverlayDelegate?
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


    func setDelegates(delegate: UITableViewDelegate, dataSource: UITableViewDataSource, fabDelegate: FABDelegate, overlayDelegate: OverlayDelegate) {
        table.delegate = delegate;
        table.dataSource = dataSource;
        FAB?.fabDelegate = fabDelegate
        self.overlayDelegate = overlayDelegate
    }

    func setUpTable() {
        table.separatorInset = UIEdgeInsetsZero
        self.panDetector.addTarget(self, action: "swiped:");

        self.swipeDetectionView.addGestureRecognizer(panDetector);
        self.swipeDetectionView.userInteractionEnabled = true;


        let singleTap = UITapGestureRecognizer(target: self, action: Selector("tapDetected"))
        singleTap.numberOfTapsRequired = 1

        overlay.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.5)
        overlay.layer.opacity = 0;
        overlay.addGestureRecognizer(singleTap)

        let fabImage = UIImage(named: "FAB")
        FAB = floatingAction()
        FAB?.image = fabImage;
        FAB?.contentMode = UIViewContentMode.ScaleAspectFill
        FAB?.clipsToBounds = true;
        FAB?.userInteractionEnabled = true


        addSubview(table);
        addSubview(FAB!)

        addSubview(overlay)
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

    func tapDetected() {
        if darkened {
            println("clickedShadow");
            overlayDelegate?.hideEverything();
        }
    }

    func darkenOverlay() {
        animateOverlay(1)
        darkened = true;
    }

    func hideOverlay() {
        animateOverlay(0)
        darkened = false;
    }

    func toggleOoverlay() {
        if (darkened) {
            hideOverlay()
        } else {
            darkenOverlay()
        }

    }

    func animateOverlay(opac: Float) {

        UIView.animateWithDuration(Double(0.5)) {


            self.overlay.layer.opacity = opac
            self.layoutIfNeeded()
        }


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
        overlay.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
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
        fabDelegate?.touchDown();

    }

    override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
        fabDelegate?.touchUp();


    }


}








