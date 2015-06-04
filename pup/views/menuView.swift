//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore

class SideMenuView: UIView {

    var menuItems: Array<MenuItems> = []
    var parentView: UIView?
    var divider: UIView = UIView();
    var logo: UIImageView = UIImageView();
    var logoimg: UIImage? = UIImage(named: "iconWithText");
    var menuWidth: CGFloat = UIScreen.mainScreen().bounds.size.width * 0.9;
    var panDetector = UIPanGestureRecognizer()
    var swipeDelegate: PanGestureDelegate?
    var parent: SideMenuController?
    var overlayDelegate: OverlayDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor=UIColor.whiteColor()

        clipsToBounds = true;





    }





    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setTranslation(sender: UIPanGestureRecognizer, opened: Bool) -> Bool {
        var translation = sender.translationInView(self);

        if (opened == false) {
            if (translation.x > 120) {
                self.openMenu();
                return true;
            } else if (translation.x>0) {
                var trans = CGAffineTransformMakeTranslation(translation.x, 0);
                self.transform = trans;
                if (sender.state == .Ended) {
                    self.closeMenu();
                }
            }
        }

        if (opened) {
            if (translation.x < -120) {
                self.closeMenu();
                return false;
            } else if (translation.x<0) {
                var trans = CGAffineTransformMakeTranslation(self.menuWidth + translation.x, 0);
                self.transform = trans;
                if (sender.state == .Ended) {
                    self.openMenu();
                }
            }
        }

        return opened;
    }




    func setUpView(parentView: UIView, parent: SideMenuController) {
        parentView.bringSubviewToFront(self);
        self.parent = parent;
        self.backgroundColor = UIColor(rgba: "#F5F6F6");

        self.panDetector.addTarget(self, action: "swiped:");

        self.addGestureRecognizer(panDetector);
        self.userInteractionEnabled = true;


        self.layer.shadowOffset = CGSizeMake(5,5)
        self.layer.shadowRadius = 8
        self.layer.shadowOpacity = 0
        self.layer.masksToBounds = false;

        self.parentView = parentView



        logo.image = logoimg!;
        logo.contentMode = UIViewContentMode.ScaleAspectFill;
        logo.clipsToBounds = true;

        divider.backgroundColor = UIColor(rgba: colors.mainGrey);

        menuItems.append(MenuItems())
        menuItems[0].setPage("Find A Game");


        menuItems.append(MenuItems())
        menuItems[1].setPage("Feedback");

        menuItems.append(MenuItems())
        menuItems[2].setPage("Settings");



        addViews()
        setUpConstraints()
    }

    func addViews() {
        addSubview(divider)
        addSubview(logo)
        addSubview(menuItems[0])
        addSubview(menuItems[1])
        addSubview(menuItems[2])

        self.parentView?.addSubview(self)

    }


    func swiped(sender: UIPanGestureRecognizer) {
        println("haha!")
        self.swipeDelegate?.swiped(sender);

    }

    func setUpDelegates(delegate: MenuItemDelegate, overlayDelegate: OverlayDelegate) {
        menuItems[0].setDelegate(delegate);
        menuItems[1].setDelegate(delegate);
        menuItems[2].setDelegate(delegate);
        swipeDelegate = delegate as? PanGestureDelegate;
        self.overlayDelegate = overlayDelegate
    }



    func setUpConstraints() {

        println("setting up menu constraints")
        println(self)
        println(self.parentView)


        self.snp_remakeConstraints { (make) -> Void in
            make.right.equalTo(self.parentView!.snp_left).offset(0)
            make.width.equalTo(self.menuWidth)
            make.top.equalTo(self.parentView!).offset(0)
            make.bottom.equalTo(self.parentView!).offset(0)

        }



        self.logo.snp_remakeConstraints { (make) -> Void in
            make.width.equalTo(self.menuWidth / 3.0);
            make.centerX.equalTo(self)
            make.top.equalTo(self).offset(UIConstants.verticalPadding*7);
            make.height.equalTo(self.menuWidth / 3.0);
        }


        self.divider.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.logo.snp_bottom).offset(UIConstants.verticalPadding);
            make.left.equalTo(self).offset(UIConstants.horizontalPadding);
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding);
            make.height.equalTo(1);

        }

        menuItems[0].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self.divider.snp_bottom).offset(UIConstants.verticalPadding*2)
            make.height.equalTo(50)

        }
        menuItems[1].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self.menuItems[0].snp_bottom).offset(0)
            make.height.equalTo(50)

        }
        menuItems[2].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self.menuItems[1].snp_bottom).offset(0)
            make.height.equalTo(50)

        }

    }



    func closeMenu() {

        overlayDelegate?.hideOverlay();


        let shadowAnimation = CABasicAnimation(keyPath: "shadowOpacity")
        shadowAnimation.fromValue = self.layer.shadowOpacity
        shadowAnimation.toValue = 0
        shadowAnimation.duration = 0.5
        shadowAnimation.repeatCount = 1;
        shadowAnimation.beginTime = CACurrentMediaTime();
        self.layer.shadowOpacity = 0;

        //shadowAnimation.removedOnCompletion = true;
        self.layer.addAnimation(shadowAnimation, forKey: "shadowOpacity");
        UIView.animateWithDuration(Double(0.5)) {

//            self.snp_remakeConstraints { (make) -> Void in
//                make.right.equalTo(self.parentView!.snp_left).offset(0)
//                make.width.equalTo(self.menuWidth)
//                make.top.equalTo(self.parentView!).offset(0)
//                make.bottom.equalTo(self.parentView!).offset(0)
//
//            }
            var trans = CGAffineTransformMakeTranslation(0, 0);
            self.transform = trans;




            self.parentView?.layoutIfNeeded()
        }



    }

func openMenu() {

    overlayDelegate?.darkenOverlay();

    let shadowAnimation = CABasicAnimation(keyPath: "shadowOpacity")
    shadowAnimation.fromValue = self.layer.shadowOpacity
    shadowAnimation.toValue = 1
    shadowAnimation.repeatCount = 1;
    shadowAnimation.duration = 0.5
    shadowAnimation.beginTime = CACurrentMediaTime();
    self.layer.shadowOpacity = 1;
    //shadowAnimation.removedOnCompletion = true;

    self.layer.addAnimation(shadowAnimation, forKey: "shadowOpacity");

    UIView.animateWithDuration(Double(0.5)) {

//        self.snp_remakeConstraints {
//            (make) -> Void in
//            make.left.equalTo(self.parentView!).offset(0)
//            make.width.equalTo(UIScreen.mainScreen().bounds.size.width * 0.8)
//            make.top.equalTo(self.parentView!).offset(0)
//            make.bottom.equalTo(self.parentView!).offset(0)
//
//        }

        var trans = CGAffineTransformMakeTranslation(self.menuWidth, 0);
        self.transform = trans;

        self.layer.shadowOpacity = 0.7
        self.parentView?.layoutIfNeeded()
}




}


}


class MenuItems: UIButton {

    var name: String = "page";
    var delegate: MenuItemDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)


    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setPage(page: String) {
        name = page;
        self.setTitle(page, forState: UIControlState.Normal)
        self.setTitleColor(UIColor(rgba: colors.mainGrey), forState: UIControlState.Normal)
        self.backgroundColor = UIColor.clearColor();


        self.addTarget(self, action: "touchesEndedWithAction:", forControlEvents: UIControlEvents.TouchUpInside);
        self.addTarget(self, action: "touchesBegan:", forControlEvents: UIControlEvents.TouchDown);
        self.addTarget(self, action: "touchesEndedNoAction:", forControlEvents: UIControlEvents.TouchCancel);
    }

    func setDelegate(delegate: MenuItemDelegate) {
        self.delegate = delegate;


    }

    func touchesBegan( sender: UIButton!) {
        println("wooh")
        self.backgroundColor = UIColor(rgba: colors.orange)
        self.setTitleColor(UIColor.whiteColor(), forState: UIControlState.Normal)
    }

    func touchesEndedWithAction(sender: UIButton!) {
        self.backgroundColor = UIColor.clearColor()
        self.setTitleColor(UIColor(rgba: colors.mainGrey), forState: UIControlState.Normal)
        delegate?.touchUpInside(self)


    }

    func touchesEndedNoAction(sender: UIButton!) {
        self.backgroundColor = UIColor.clearColor()
        self.setTitleColor(UIColor(rgba: colors.mainGrey), forState: UIControlState.Normal)


    }


}