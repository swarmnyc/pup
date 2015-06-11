//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class CreateLobbyView: UIView {

    var headerImage: UIImageView = UIImageView();
    var searchBar: UISearchBar = UISearchBar();
    var pickSystemText: UILabel = UILabel();
    var platforms: Array<PlatformButtonToggle> = [];

    var scrollView: UIScrollView = UIScrollView()
    var containerView: UIView = UIView()
    var dateDisplay: DateDisplayView?
    var timeDisplay: TimeDisplayView?
    var descriptionEditor: DescriptionEditor = DescriptionEditor();
    var createLobbyButton: UIButton = UIButton();

    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor=UIColor.whiteColor()


    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func hideInactivePlatforms(possiblePlatforms: Array<String>) {

        for (var i = 0; i<platforms.count; i++) {
            platforms[i].hideIfNotNeeded(possiblePlatforms);
        }

    }




    func shortenView(notification: NSNotification) {
        println("shortening view")
        if (descriptionEditor.firstResponderCheck()) {
            UIView.animateWithDuration(0.5) {
                var keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.CGRectValue()
                var keyboardHeight = keyboardSize!.height;
                var newSize = self.scrollView.frame.height - keyboardHeight;

                self.scrollView.snp_remakeConstraints {
                    (make) -> Void in
                    make.left.equalTo(self).offset(0)
                    make.top.equalTo(self).offset(0)
                    make.right.equalTo(self).offset(0)
                    make.height.equalTo(newSize)
                }



                self.layoutIfNeeded()
            }


            var bottomOffset = CGPointMake(0, self.scrollView.contentSize.height - self.scrollView.bounds.size.height);
            scrollView.setContentOffset(bottomOffset, animated: false)
        }
    }

    func restoreView() {
        UIView.animateWithDuration(0.5) {

            self.scrollView.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self).offset(0)
                make.top.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.bottom.equalTo(self).offset(0)

            }



            self.layoutIfNeeded()
        }
    }




    func setImage(imageUrl: String) {

        var url = NSURL(string: imageUrl)
        var request:NSURLRequest = NSURLRequest(URL: url!)
        headerImage.backgroundColor = UIColor.blackColor();
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            var img = UIImage(data: data) as UIImage!
            self.headerImage.image = img;
            self.headerImage.contentMode = UIViewContentMode.ScaleAspectFill;
            self.headerImage.clipsToBounds = true;
        })

    }




    func setUpView(parentController: CreateLobbyController, dateDisplay: DateDisplayView, timeDisplay: TimeDisplayView) {

        var buttonDelegate = parentController as SimpleButtonDelegate;
        var searchDelegate = parentController as UISearchBarDelegate;
        createLobbyButton.addTarget(parentController, action: "createLobby", forControlEvents: .TouchUpInside)

        createLobbyButton.setTitle("Join Lobby", forState: .Normal)
        createLobbyButton.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)

        descriptionEditor.setDelegate(parentController as UITextViewDelegate)
        descriptionEditor.setUpView()


        for i in 0...appData.platforms.count-1 {
            platforms.append(PlatformButtonToggle())
            platforms[i].setUpButton(appData.platforms[i], delegate: buttonDelegate)
        }

        scrollView.delegate = parentController;
        scrollView.scrollEnabled = true;

        containerView.userInteractionEnabled = true

        searchBar.delegate = searchDelegate
        searchBar.enablesReturnKeyAutomatically = false

        headerImage.backgroundColor = UIColor.blackColor();

        self.dateDisplay = dateDisplay
        self.timeDisplay = timeDisplay
        self.dateDisplay?.setUpView()
        self.timeDisplay?.setUpView()

        pickSystemText.text = "PICK A SYSTEM";
        pickSystemText.font = pickSystemText.font.fontWithSize(11.0);
        pickSystemText.textAlignment = NSTextAlignment.Center
        addViews()
        layoutViews()
        //self.scrollView.contentSize = CGSize(width:scrollView.bounds.size.width, height: 1900);
        self.scrollView.indicatorStyle = .Default

        println("contentSize")
        println(scrollView.contentSize)

    }


    func closeKeyboard() {
        searchBar.resignFirstResponder()

    }



    func addViews() {

        self.containerView.addSubview(headerImage);
        self.containerView.addSubview(searchBar);
        self.containerView.addSubview(pickSystemText)
        self.containerView.addSubview(dateDisplay!)
        self.containerView.addSubview(timeDisplay!)
        self.containerView.addSubview(descriptionEditor)
        self.containerView.addSubview(createLobbyButton)

        for i in 0...appData.platforms.count-1 {
            self.containerView.addSubview(platforms[i])
            println(platforms[i])
        }
        self.scrollView.addSubview(containerView)
        self.addSubview(scrollView)

    }

    func layoutViews() {
        var fullW: CGFloat = UIScreen.mainScreen().bounds.size.width
        var fullP: CGFloat = 0
        var thirdW: CGFloat = (fullW - fullP) / 3



        self.scrollView.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
        }

        self.containerView.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.scrollView).offset(0)
            make.right.equalTo(self.scrollView).offset(0)
            make.top.equalTo(self.scrollView).offset(0)
            make.bottom.equalTo(self.scrollView).offset(0)
            make.height.equalTo(920)
        }

        self.headerImage.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.top.equalTo(self.containerView).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(UIConstants.halfLobbyImage)
        }

        self.searchBar.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.containerView).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self.headerImage.snp_bottom).offset(-UIConstants.verticalPadding)
            make.height.equalTo(20)
        }

        self.pickSystemText.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
            make.top.equalTo(self.headerImage.snp_bottom).offset(0)
        }

        self.platforms[0].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.top.equalTo(self.pickSystemText.snp_bottom).offset(0)
            make.right.equalTo(self.platforms[1].snp_left).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)
        }
        self.platforms[1].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[0].snp_right).offset(0)
            make.top.equalTo(self.pickSystemText.snp_bottom).offset(0)
            make.right.equalTo(self.platforms[2].snp_left).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)

        }

        self.platforms[2].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[1].snp_right).offset(0)
            make.top.equalTo(self.pickSystemText.snp_bottom).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(UIConstants.buttonHeight)
            make.width.equalTo(thirdW)

        }
        self.platforms[3].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
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

        self.dateDisplay!.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.top.equalTo(self.platforms[4].snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(90)

        }

        self.timeDisplay!.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.top.equalTo(self.dateDisplay!.snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(90)

        }

        self.descriptionEditor.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.bottom.equalTo(self.containerView).offset(-120)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(90)

        }

        self.createLobbyButton.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.bottom.equalTo(self.containerView).offset(0)
            make.height.equalTo(58)

        }

        dateDisplay!.layoutView()
        timeDisplay!.layoutView()



    }



    func uncheckAllPlatforms() {
        for i in 0...appData.platforms.count-1 {
            platforms[i].uncheck()
        }
    }








}

class DescriptionEditor: UIView {
    var title: UILabel = UILabel();
    var descriptionField: UITextView = UITextView();

    override init(frame: CGRect) {
        super.init(frame: frame)

    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }



    func setDelegate(delegate: UITextViewDelegate) {
        descriptionField.delegate = delegate;


    }


    func setUpView() {
        backgroundColor=UIColor.whiteColor()
        self.title.text = "Description";
        self.title.font = self.title.font.fontWithSize(10);
        self.title.textColor = UIColor(rgba: colors.mainGrey)
        self.title.textAlignment = NSTextAlignment.Center

        self.descriptionField.text = UIConstants.descriptionPlaceholder
        self.descriptionField.font = self.descriptionField.font.fontWithSize(11.0)
        self.descriptionField.textColor = UIColor(rgba: colors.lightGray).darkerColor(0.5)
        self.descriptionField.returnKeyType = .Done
        addViews()
        addConstraints()
    }

    func addViews() {
        self.addSubview(self.title)
        self.addSubview(self.descriptionField)
    }


    func addConstraints() {

        var titleHeight = 20;

        title.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.height.equalTo(titleHeight)

        }
        descriptionField.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.top.equalTo(self).offset(titleHeight)
            make.height.equalTo(70)

        }


    }


    func firstResponderCheck() -> Bool {
        if (descriptionField.isFirstResponder()) {
            return true
        } else {
            return false
        }
    }




}