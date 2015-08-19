//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class JoinPupButtonView: PlatformButtonToggle {

    var topLabel: UILabel = UILabel();
    var bottomLabel: UILabel = UILabel()
    var delegate: SimpleButtonDelegate?
    var aboveTabBar: Bool = false;

    override init(frame: CGRect) {
        super.init(frame: frame)


    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }



    override func touchesBegan( touches: Set<NSObject>, withEvent event: UIEvent) {
        println("wooh")
        self.backgroundColor = self.backgroundColor?.darkerColor(0.3);

    }

    override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
        UIView.animateWithDuration(0.35, animations: {
            self.backgroundColor = UIColor(rgba: colors.orange)
        })
        delegate?.touchUp(self, type: "join")


    }



    func bounce(amount: CGFloat) {
            var transAmount = amount
            if (transAmount>0) {
                transAmount = 0;
            }
            var trans = CGAffineTransformMakeTranslation(0, transAmount)
            //  self.lobbyImg.layer.anchorPoint = CGPointMake(self.lobbyImg.bounds.size.width / 2.0, self.lobbyImg.bounds.size.height / 2.0)
            self.transform = trans;


    }

    func setUpView(joinDelegate: SimpleButtonDelegate, aboveTabBar: Bool) {
        delegate = joinDelegate
        self.backgroundColor = UIColor(rgba: colors.orange)

        topLabel.textColor = UIColor.whiteColor();
        topLabel.text = "Join"
        topLabel.font = UIConstants.buttonType;
        topLabel.textAlignment = NSTextAlignment.Center


        bottomLabel.textColor = UIColor.whiteColor();
        bottomLabel.text = "You'll be able to chat and stuff"
        bottomLabel.font = UIConstants.paragraphType;
        bottomLabel.textAlignment = .Center
        self.aboveTabBar = aboveTabBar;


    }


    func addAndConstrain(parentView: UIView) {
        if (self.superview == nil) {
            addViews(parentView);
            setUpConstraints(aboveTabBar)
        }

    }

    func addViews(parentView: UIView) {
        println(self.superview);

        addSubview(topLabel)
        addSubview(bottomLabel)
//      parentView.addSubview(self)
//      parentView.bringSubviewToFront(self)
//        nav!.tabBar.superview?.insertSubview(self, belowSubview: nav!.tabBar)
       parentView.addSubview(self)
//        UIApplication.sharedApplication().windows.first!.addSubview(self)

    }

    func addToAppView() {
        if (self.superview == nil) {
            println("inserted app view")
            mainWindow!.insertSubview(self, belowSubview: nav!.tabBar)
        }
        //UIApplication.sharedApplication().windows.first!.addSubview(self)
    }

    func removeViews() {
        self.removeFromSuperview()
    }

    func setUpConstraints(aboveTabBar: Bool) {

        var bottomOffset: CGFloat = 0;
        var height = 62;
        if (aboveTabBar) {
            //bottomOffset = nav!.tabBar.frame.height;
            //might come back in a little bit
            height = 85;
        }
        
        

        self.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.superview!).offset(0)
            make.right.equalTo(self.superview!).offset(0)
            make.bottom.equalTo(self.superview!).offset(-bottomOffset)
            make.height.equalTo(height)

        }




        topLabel.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.top.equalTo(self).offset(UIConstants.verticalPadding)
            make.height.equalTo(17)
        }


        bottomLabel.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.top.equalTo(self.topLabel.snp_bottom).offset(UIConstants.halfVerticalPadding)
            make.height.equalTo(12)
        }


    }





}
