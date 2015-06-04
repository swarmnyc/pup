//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit



class FilterView: UIView {

    var whiteBox = UIView()
    var parentView = UIView()
    var search = UISearchBar()
    var platforms: Array<Button> = [];
    var buttonDelegate: SimpleButtonDelegate? = nil
    var handle: UIImageView = UIImageView();
    var panDetector = UIPanGestureRecognizer()
    var swipeDelegate: PanGestureDelegate?
    var overlayDelegate: OverlayDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)


    }




    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }



    func dimInactiveButtons() {
        println("dimming")
        for i in 0...appData.platforms.count-1 {

            platforms[i].dimIfInactive()

        }
    }

    func darkenAllButtons() {
        for i in 0...appData.platforms.count-1 {

            platforms[i].makeItDarkerAgain()

        }
    }

    func setUpViews() {
        println("setUpViews")
//        search.textColor = UIColor.whiteColor()

        self.backgroundColor = UIColor.clearColor()
        self.clipsToBounds = false;

        let handleImage = UIImage(named: "pullBar")
        handle.image = handleImage
        handle.contentMode = UIViewContentMode.ScaleAspectFill;

        whiteBox.backgroundColor = UIColor.whiteColor();
        whiteBox.clipsToBounds = false;


        for i in 0...appData.platforms.count-1 {
            platforms.append(Button())
            platforms[i].setUpButton(appData.platforms[i], delegate: self.buttonDelegate!)
        }
        self.panDetector.addTarget(self, action: "swiped:");

        self.addGestureRecognizer(panDetector);
        self.userInteractionEnabled = true;

        addViews();
        setUpConstraints()

    }



    func setUpDelegates(searchdelegate: UISearchBarDelegate, buttondelegate: SimpleButtonDelegate, overlayDelegate: OverlayDelegate) {
        search.delegate = searchdelegate;
        self.buttonDelegate = buttondelegate
        self.swipeDelegate = searchdelegate as? PanGestureDelegate;
        self.overlayDelegate = overlayDelegate
    }

    func swiped(sender: UIPanGestureRecognizer) {

        println("swiping");
        self.swipeDelegate?.swiped(sender);
    }


    func setTranslation(sender: UIPanGestureRecognizer, opened: Bool) -> Bool {
        var translation = sender.translationInView(self);

        if (opened == false) {
            if (translation.y > 75) {
                self.openFilter();
                return true;
            } else if (translation.y>0) {
                var trans = CGAffineTransformMakeTranslation(0, translation.y);
                self.self.transform = trans;
                if (sender.state == .Ended) {
                    self.closeFilter();
                }
            }
        }

        if (opened) {
            if (translation.y < -75) {
                self.closeFilter();
                return false;
            } else if (translation.y<0) {
                var trans = CGAffineTransformMakeTranslation(0, 300 + translation.y);
                self.self.transform = trans;
                if (sender.state == .Ended) {
                    self.openFilter();
                }
            }
        }

        return opened;
    }

    func setUpConstraints() {

        var fullW: CGFloat = UIScreen.mainScreen().bounds.size.width


        var fullP: CGFloat = 0
        var thirdW: CGFloat = (fullW - fullP) / 3
        self.snp_remakeConstraints { (make) -> Void in
            make.width.equalTo(fullW)
            make.right.equalTo(self.parentView).offset(0)
            make.top.equalTo(self.parentView).offset(-300)
            make.height.equalTo(400)

        }

        self.whiteBox.snp_remakeConstraints { (make) -> Void in
            make.width.equalTo(fullW)
            make.right.equalTo(self.parentView).offset(0)
            make.top.equalTo(self.parentView).offset(-300)
            make.bottom.equalTo(self.platforms[4].snp_bottom).offset(0)

        }



        self.search.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.whiteBox).offset(16)
            make.right.equalTo(self.whiteBox).offset(-16)
            make.top.equalTo(self.whiteBox).offset(UIConstants.justBelowSearchBar)
            make.height.equalTo(UIConstants.buttonHeight / 2)

        }

        self.platforms[0].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.whiteBox).offset(0)
            make.top.equalTo(self.whiteBox).offset(UIConstants.justBelowSearchBar + (UIConstants.buttonHeight / 2.0) + UIConstants.verticalPadding)
            make.right.equalTo(self.platforms[1].snp_left).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)
        }
        self.platforms[1].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[0].snp_right).offset(0)
            make.top.equalTo(self.whiteBox).offset(UIConstants.justBelowSearchBar + (UIConstants.buttonHeight / 2.0) + UIConstants.verticalPadding)
            make.right.equalTo(self.platforms[2].snp_left).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)

        }

        self.platforms[2].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[1].snp_right).offset(0)
            make.top.equalTo(self.whiteBox).offset(UIConstants.justBelowSearchBar + (UIConstants.buttonHeight / 2.0) + UIConstants.verticalPadding)
            make.right.equalTo(self.whiteBox).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)

        }
        self.platforms[3].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.whiteBox).offset(0)
            make.top.equalTo(self.platforms[1].snp_bottom).offset(0)
            make.right.equalTo(self.platforms[1].snp_left).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)
        }
        self.platforms[4].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[0].snp_right).offset(0)
            make.top.equalTo(self.platforms[1].snp_bottom).offset(0)
            make.right.equalTo(self.platforms[2].snp_left).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)

        }
        self.handle.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.platforms[4].snp_bottom)
            make.left.equalTo(self.whiteBox)
            make.right.equalTo(self.whiteBox)
            make.height.greaterThanOrEqualTo(3)
        }


    }

    func addViews() {
        println(parentView)
        println("parentView")
        parentView.addSubview(self)
        self.addSubview(whiteBox)
        whiteBox.addSubview(search)
        whiteBox.addSubview(handle)
        for i in 0...appData.platforms.count-1 {

            whiteBox.addSubview(platforms[i])
            println(platforms[i])
        }
    }




    func openFilter() {
        search.becomeFirstResponder()

        overlayDelegate?.darkenOverlay()

        var fullW: CGFloat = UIScreen.mainScreen().bounds.size.width

        UIView.animateWithDuration(Double(0.5)) {
            var trans = CGAffineTransformMakeTranslation(0, 300);
            self.self.transform = trans;
            self.snp_remakeConstraints { (make) -> Void in
                make.width.equalTo(fullW)
                make.right.equalTo(self.parentView).offset(0)
                make.top.equalTo(self.parentView).offset(-300)
                make.height.equalTo(UIConstants.justBelowSearchBar + (UIConstants.buttonHeight / 2.0) + UIConstants.verticalPadding + (2 * UIConstants.buttonHeight))

            }


            self.parentView.layoutIfNeeded()
            }


        }








    func closeFilter() {
        search.resignFirstResponder()

        overlayDelegate?.hideOverlay()

        var fullW: CGFloat = UIScreen.mainScreen().bounds.size.width

        UIView.animateWithDuration(Double(0.5)) {
            var trans = CGAffineTransformMakeTranslation(0, 0);
            self.self.transform = trans;

            self.snp_remakeConstraints { (make) -> Void in
                make.width.equalTo(fullW)
                make.right.equalTo(self.parentView).offset(0)
                make.top.equalTo(self.parentView).offset(-300)
                make.height.equalTo(400)

            }
            self.parentView.layoutIfNeeded()
        }



    }

}
