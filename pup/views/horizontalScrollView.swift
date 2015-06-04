//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class HorizontalSelectView: UIView {

    var options: Array<HorizontalButtons> = []
    var optionWidth = UIScreen.mainScreen().bounds.size.width * 0.33333;
    var swipeDelegate: SwipeGestureDelegate?;
    var title: UILabel = UILabel();

    override init(frame: CGRect) {
        super.init(frame: frame)

    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func addOptions(options: Array<String>, title: String, delegate: UIViewController) {
        swipeDelegate = delegate as? SwipeGestureDelegate;

        var swipeRight = UISwipeGestureRecognizer(target: self, action: "swiped:")
        swipeRight.direction = UISwipeGestureRecognizerDirection.Right
        self.addGestureRecognizer(swipeRight)

        var swipeLeft = UISwipeGestureRecognizer(target: self, action: "swiped:")
        swipeLeft.direction = UISwipeGestureRecognizerDirection.Left
        self.addGestureRecognizer(swipeLeft)

        self.title.text = title;
        self.title.font = self.title.font.fontWithSize(10);
        self.title.textColor = UIColor(rgba: colors.mainGrey)
        self.title.textAlignment = NSTextAlignment.Center
        self.addSubview(self.title)

        for (var i = 0; i<options.count; i++) {
            self.options.append(HorizontalButtons())
            println(options[i]);
            self.options[i].setUpButton(options[i], buttonDelegate: delegate as! SimpleButtonDelegate);
            self.addSubview(self.options[i])
        }
        self.setSelected(0)


    }

    func slideLayout(currentSelect: Int) {
            println("layout slide")
//        UIView.animateWithDuration(Double(0.5)) {
//
//            var trans = CGAffineTransformMakeTranslation(CGFloat(-currentSelect) * (self.optionWidth), 0);
//            self.transform = trans;
//
//
//            self.setSelected(currentSelect)
//
//
//
//            self.layoutIfNeeded()
//        }

        UIView.animateWithDuration(Double(0.15)) {

            for (var i = 0; i<self.options.count; i++) {
                if (i == currentSelect) {
                    self.options[i].snp_remakeConstraints { (make) -> Void in
                        make.left.equalTo(self.optionWidth)
                        make.top.equalTo(self).offset(0)
                        make.width.equalTo(self.optionWidth)
                        make.bottom.equalTo(self)
                    }

                } else if (i<currentSelect) {
                    self.options[i].snp_remakeConstraints {
                        (make) -> Void in
                        make.right.equalTo(self.options[i + 1].snp_left).offset(-UIConstants.horizontalPadding)
                        make.top.equalTo(self).offset(0)
                        make.width.equalTo(self.optionWidth)
                        make.bottom.equalTo(self)
                    }
                } else if (i>currentSelect) {
                    self.options[i].snp_remakeConstraints {
                        (make) -> Void in
                        make.left.equalTo(self.options[i - 1].snp_right).offset(UIConstants.horizontalPadding)
                        make.top.equalTo(self).offset(0)
                        make.width.equalTo(self.optionWidth)
                        make.bottom.equalTo(self)
                    }
                }


            }


            self.setSelected(currentSelect)



            self.layoutIfNeeded()
        }


    }

    func swiped(gesture: UIGestureRecognizer) {

        if let swipeGesture = gesture as? UISwipeGestureRecognizer {
            switch swipeGesture.direction {
                case UISwipeGestureRecognizerDirection.Right:
                    println("right")
                    swipeDelegate?.swiped("right");
                case UISwipeGestureRecognizerDirection.Left:
                    println("left")
                    swipeDelegate?.swiped("left");

            default:
                    break;
            }

        }
    }

    func setUpView(parentView: UIView, bottomOffset: Double) {

        self.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(parentView).offset(0)
            make.bottom.equalTo(parentView).offset(-bottomOffset)
            make.width.equalTo(self.optionWidth * CGFloat(self.options.count))
            make.height.equalTo(90)
        }

        self.title.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.width.equalTo(UIScreen.mainScreen().bounds.size.width)
            make.height.equalTo(12)
        }

        self.options[0].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.optionWidth)
            make.top.equalTo(self).offset(0)
            make.width.equalTo(self.optionWidth)
            make.bottom.equalTo(self)
        }

        for (var i = 1; i<self.options.count; i++) {
            self.options[i].snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.options[i-1].snp_right).offset(UIConstants.horizontalPadding)
                make.top.equalTo(self).offset(0)
                make.width.equalTo(self.optionWidth)
                make.bottom.equalTo(self)
            }


        }


    }


    func setSelected(selected: Int) {

        for (var i = 0; i<self.options.count; i++) {
            if (i == selected) {
                self.options[i].setSelected();
                self.options[i].snp_updateConstraints {
                    (make) -> Void in
                    make.width.equalTo(self.optionWidth)
                }
            } else {
                self.options[i].unSelected();
                self.options[i].snp_updateConstraints {
                    (make) -> Void in
                    make.width.equalTo(self.optionWidth)
                }
            }


        }
    }





}




class HorizontalButtons: UILabel {


    var delegate: SimpleButtonDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)


    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setUpButton(text: String, buttonDelegate: SimpleButtonDelegate) {
            self.userInteractionEnabled = true;
            self.text = text;
            self.textAlignment = NSTextAlignment.Center
            self.delegate = buttonDelegate;
        self.textColor = UIColor(rgba: colors.midGray)
        self.font = self.font.fontWithSize(11);
    }


    override func touchesBegan( touches: Set<NSObject>, withEvent event: UIEvent) {
        println("wooh")
       // self.backgroundColor = self.backgroundColor?.darkerColor(0.3);

    }

    override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
       // self.backgroundColor = UIColor(rgba: colors.orange)
        delegate?.touchUp(self, type: self.text!)


    }

    func setSelected() {
            self.textColor = UIColor(rgba: colors.tealMain)
            self.font = self.font.fontWithSize(19);
           // delegate?.touchUp(self, type: self.text)

    }

    func unSelected() {
        self.textColor = UIColor(rgba: colors.midGray)
        self.font = self.font.fontWithSize(9);
    }


}
