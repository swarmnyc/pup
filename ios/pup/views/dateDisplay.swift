//
// Created by Alex Hartwell on 6/4/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class DateDisplayView: UIView, UITextFieldDelegate {

    var title: UILabel = UILabel();
    var timeTextField: UIButton = UIButton();

    var successfulChange: ((newDate: NSDate) -> (NSDate))?

    var currentDate: NSDate = NSDate();

    var dateSelector: SNYDateAndTimePicker = SNYDateAndTimePicker();

    override init(frame: CGRect) {
        super.init(frame: frame)

    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpView() {




        self.title.text = "WHEN?"
        self.title.textAlignment = NSTextAlignment.Center
        self.title.font = UIConstants.titleFont;
        self.title.textColor = UIColor(rgba: colors.mainGrey)




        self.timeTextField.setTitle("TODAY" ,forState: .Normal);
        self.timeTextField.titleLabel?.textAlignment = NSTextAlignment.Center
        self.timeTextField.setTitleColor(UIColor(rgba: colors.tealMain), forState: .Normal);
        self.timeTextField.titleLabel?.font =  UIFont(name: "AvenirNext-Regular", size: 19.0)

        self.timeTextField.addTarget(self, action: "clickDateAndSeeDateSelector", forControlEvents: UIControlEvents.TouchUpInside)



        dateSelector.setUp(.DateAndTime, titleText: "WHEN?", maxDate: NSDate().dateByAddingDays(14), minimumDate: NSDate().dateByAddingMinutes(20), onComplete: {
            (date) -> Void in
            var newestTime = self.successfulChange!(newDate: date)
            self.currentDate = newestTime;
            self.timeTextField.setNeedsDisplay()
            self.timeTextField.setTitle(date.toString(dateStyle: .ShortStyle, timeStyle: .ShortStyle, doesRelativeDateFormatting: true).uppercaseString ,forState: .Normal);
//            self.timeTextField.text = date.toString(dateStyle: .ShortStyle, timeStyle: .NoStyle, doesRelativeDateFormatting: true).uppercaseString;
        })
        //self.timeTextField.text = NSDate().toString(dateStyle: .ShortStyle, timeStyle: .NoStyle, doesRelativeDateFormatting: true).uppercaseString;

    }








    func layoutView() {

        self.addSubview(title)
        self.addSubview(timeTextField)

        title.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.height.equalTo(20)
        }

        timeTextField.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(-20)
            make.height.equalTo(40)
        }


    }


    func clickDateAndSeeDateSelector() {
        self.dateSelector.show();
    }


    func textFieldShouldBeginEditing(textField: UITextField) -> Bool{
        println("woooooooooh yeeeeeeeaaahhhhhhh")

        self.dateSelector.show();


        return false

    }

    func setText() {



    }



}

//
//class TimeDisplayView: DateDisplayView {
//
//
//    override func setUpView() {
//
//        self.title.text = "WHAT TIME?"
//        self.title.textAlignment = NSTextAlignment.Center
//        self.title.font = UIFont(name: "AvenirNext-Regular", size: 10.0)
//        self.title.textColor = UIColor(rgba: colors.mainGrey)
//
//        self.timeTextField.text = "in 20 Minutes"
//        self.timeTextField.textAlignment = NSTextAlignment.Center
//        self.timeTextField.textColor = UIColor(rgba: colors.tealMain)
//        self.timeTextField.font = UIFont(name: "AvenirNext-Regular", size: 19.0)
//
//
//        dateSelector.setUp(.Time, titleText: "WHAT TIME?", maxDate: NSDate().dateByAddingDays(14), minimumDate: NSDate().dateByAddingMinutes(20), onComplete: {
//            (date) -> Void in
//            var newestTime = self.successfulChange!(newDate: date)
//            self.currentDate = newestTime;
//            self.setNewText(newestTime)
//        })
//
//    }
//
//
//    func setNewText(newestTime: NSDate) {
//        if (newestTime.minutesAfterDate(NSDate()) <= 20) {
//            self.timeTextField.text = "in 20 Minutes"
//        } else {
//            self.timeTextField.text = "\(newestTime.toString(dateStyle: .NoStyle, timeStyle: .ShortStyle, doesRelativeDateFormatting: true))"
//        }
//    }
//
//
//    override func textFieldShouldBeginEditing(textField: UITextField) -> Bool{
//        println("woooooooooh yeeeeeeeaaahhhhhhh")
//
//
//        self.dateSelector.show();
//
//        return false
//
//    }
//}