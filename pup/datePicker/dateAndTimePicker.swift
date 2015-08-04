//
// Created by Alex Hartwell on 6/12/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

enum datePickerType {
    case Date, Time, DateAndTime;
}

class DateAndTimePicker: UIView {

    var datePicker: UIDatePicker = UIDatePicker();
    var cancelButton: UIButton = UIButton();
    var okButton: UIButton = UIButton();
    var title: UITextView = UITextView();
    var onComplete: ((NSDate) -> (Void))?


    override init(frame: CGRect) {
        super.init(frame: frame)


    }
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUp(pickerType: datePickerType, titleText: String, maxDate: NSDate, minimumDate: NSDate, onComplete: (NSDate) -> Void) {

        self.onComplete = onComplete;

        title.text = titleText;

        switch pickerType {
            case .Date:
                datePicker.datePickerMode = .Date
            case .DateAndTime:
                datePicker.datePickerMode = .DateAndTime
            default:
                datePicker.datePickerMode = .Time
        }

        datePicker.maximumDate = maxDate;
        datePicker.minimumDate = minimumDate;

        setUpViews();
        addViews();
        //setUpConstraints()


    }

    func show() {
        self.alpha = 1;
        UIApplication.sharedApplication().windows.first!.addSubview(self)
        setUpConstraints();
    }

    func hide() {
        self.removeFromSuperview()
    }

    func setUpViews() {
        self.alpha = 0;
        self.layer.cornerRadius = 15;
        self.layer.masksToBounds = true;

        self.layer.shadowOffset = CGSizeMake(1.0,1.0);
        self.layer.shadowColor = UIColor.blackColor().CGColor;
        self.layer.shadowRadius = 8.0;
        self.layer.shadowOpacity = 0.8;

        self.clipsToBounds = false;
        self.backgroundColor = UIColor(red: 1, green: 1, blue: 1, alpha: 0.99);





        cancelButton.setTitle("Cancel", forState: .Normal)
        cancelButton.titleLabel!.font = cancelButton.titleLabel!.font.fontWithSize(11.0)
        cancelButton.setTitleColor(UIColor.blackColor(), forState: .Normal)
        cancelButton.titleLabel!.textAlignment = NSTextAlignment.Center
        cancelButton.addTarget(self, action: "cancelButtonPressed", forControlEvents: UIControlEvents.TouchUpInside);

        okButton.setTitle("OK", forState: .Normal)
        okButton.titleLabel!.font = okButton.titleLabel!.font.fontWithSize(11.0)
        okButton.setTitleColor(UIColor.blackColor(), forState: .Normal)
        okButton.titleLabel!.textAlignment = NSTextAlignment.Center
        okButton.addTarget(self, action: "okButtonPressed", forControlEvents: UIControlEvents.TouchUpInside);

        title.backgroundColor = UIColor.clearColor()
        title.textAlignment = NSTextAlignment.Center
        title.font = title.font.fontWithSize(13);

        datePicker.addTarget(self, action: "dateChanged", forControlEvents: UIControlEvents.ValueChanged)

    }




    func cancelButtonPressed() {
        self.hide()
    }

    func okButtonPressed() {
        println(datePicker.date)
        cancelButtonPressed();
        onComplete!(self.datePicker.date)
    }

    func dateChanged() {

        println("date picker");
        println(self.datePicker);
        println(self.datePicker.date);

    }




    func addViews() {

        UIApplication.sharedApplication().windows.first!.addSubview(self)
        self.addSubview(datePicker)
        self.addSubview(cancelButton)
        self.addSubview(okButton)
        self.addSubview(title)

    }

    func setUpConstraints() {

        self.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.superview!).offset(UIConstants.verticalPadding * 8)
            make.left.equalTo(self.superview!).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.superview!).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(300)
        }


        title.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self).offset(UIConstants.halfVerticalPadding)
            make.left.equalTo(self)
            make.right.equalTo(self)
            make.height.equalTo(20)
        }

        datePicker.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self).offset(UIConstants.verticalPadding + UIConstants.halfVerticalPadding)
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(216)
        }

        cancelButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.datePicker.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.snp_centerX).offset(0)
            make.bottom.equalTo(self).offset(-UIConstants.horizontalPadding)
        }

        okButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.datePicker.snp_bottom).offset(UIConstants.verticalPadding)
            make.left.equalTo(self.snp_centerX).offset(0)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self).offset(-UIConstants.horizontalPadding)
        }

    }


}