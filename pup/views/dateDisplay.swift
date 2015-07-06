//
// Created by Alex Hartwell on 6/4/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class DateDisplayView: UIView, UITextFieldDelegate {

    var title: UILabel = UILabel();
    var text: UITextField = UITextField();

    var successfulChange: ((newDate: NSDate) -> (NSDate))?

    var currentDate: NSDate = NSDate();

    var dateSelector: DateAndTimePicker = DateAndTimePicker();

    override init(frame: CGRect) {
        super.init(frame: frame)

    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpView() {
        self.text.delegate = self;




        self.title.text = "WHEN?"
        self.title.textAlignment = NSTextAlignment.Center
        self.title.font = self.title.font.fontWithSize(10);
        self.title.textColor = UIColor(rgba: colors.mainGrey)

        self.text.text = "Today"
        self.text.textAlignment = NSTextAlignment.Center
        self.text.textColor = UIColor(rgba: colors.tealMain)
        self.text.font = self.text.font.fontWithSize(19);


        dateSelector.setUp(.DateAndTime, titleText: "WHEN?", maxDate: NSDate().dateByAddingDays(14), minimumDate: NSDate().dateByAddingMinutes(20), onComplete: {
            (date) -> Void in
            var newestTime = self.successfulChange!(newDate: date)
            self.currentDate = newestTime;
            self.text.text = "\(date.toString(dateStyle: .ShortStyle, timeStyle: .NoStyle, doesRelativeDateFormatting: true))"
        })

    }

    func layoutView() {

        self.addSubview(title)
        self.addSubview(text)

        title.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.height.equalTo(20)
        }

        text.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(-20)
            make.height.equalTo(30)
        }


    }



    func textFieldShouldBeginEditing(textField: UITextField) -> Bool{
        println("woooooooooh yeeeeeeeaaahhhhhhh")

        self.dateSelector.show();


        return false

    }



}


class TimeDisplayView: DateDisplayView {


    override func setUpView() {
        self.text.delegate = self;

        self.title.text = "WHAT TIME?"
        self.title.textAlignment = NSTextAlignment.Center
        self.title.font = self.title.font.fontWithSize(10);
        self.title.textColor = UIColor(rgba: colors.mainGrey)

        self.text.text = "in 20 Minutes"
        self.text.textAlignment = NSTextAlignment.Center
        self.text.textColor = UIColor(rgba: colors.tealMain)
        self.text.font = self.text.font.fontWithSize(19);


        dateSelector.setUp(.Time, titleText: "WHAT TIME?", maxDate: NSDate().dateByAddingDays(14), minimumDate: NSDate().dateByAddingMinutes(20), onComplete: {
            (date) -> Void in
            var newestTime = self.successfulChange!(newDate: date)
            self.currentDate = newestTime;
            self.setNewText(newestTime)
        })

    }


    func setNewText(newestTime: NSDate) {
        if (newestTime.minutesAfterDate(NSDate()) <= 20) {
            self.text.text = "in 20 Minutes"
        } else {
            self.text.text = "\(newestTime.toString(dateStyle: .NoStyle, timeStyle: .ShortStyle, doesRelativeDateFormatting: true))"
        }
    }


    override func textFieldShouldBeginEditing(textField: UITextField) -> Bool{
        println("woooooooooh yeeeeeeeaaahhhhhhh")


        self.dateSelector.show();

        return false

    }
}