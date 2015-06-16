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
        self.backgroundColor = UIColor(rgba: colors.orange)
        delegate?.touchUp(self, type: "join")


    }

    func setUpViews(parentView: UIView, joinDelegate: SimpleButtonDelegate) {
        delegate = joinDelegate
        self.backgroundColor = UIColor(rgba: colors.orange)

        topLabel.textColor = UIColor.whiteColor();
        topLabel.text = "Join"
        topLabel.font = topLabel.font.fontWithSize(16)
        topLabel.textAlignment = NSTextAlignment.Center


        bottomLabel.textColor = UIColor.whiteColor();
        bottomLabel.text = "You'll be able to chat and stuff"
        bottomLabel.font = bottomLabel.font.fontWithSize(12)
        bottomLabel.textAlignment = .Center

        addViews();
        setUpConstraints();
    }


    func addViews() {

        addSubview(topLabel)
        addSubview(bottomLabel)
//      parentView.addSubview(self)
//      parentView.bringSubviewToFront(self)
        UIApplication.sharedApplication().windows.first!.addSubview(self)
    }

    func removeViews() {
        self.removeFromSuperview()
    }

    func setUpConstraints() {

        self.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.superview!).offset(0)
            make.right.equalTo(self.superview!).offset(0)
            make.bottom.equalTo(self.superview!).offset(0)
            make.height.equalTo(58)

        }



        bottomLabel.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.height.equalTo(12)
        }

        topLabel.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self.bottomLabel.snp_top).offset(-UIConstants.halfVerticalPadding)
            make.height.equalTo(17)
        }


    }


}
