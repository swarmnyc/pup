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
    var descriptionEditor: DescriptionEditor = DescriptionEditor();
    var createLobbyButton: UIButtonWithAcivityIndicator = UIButtonWithAcivityIndicator();
    var searchBarBackground = UIView();
    var scrollViewFrame = CGRectMake(0,0,0,0)
    var scrollViewBounds = CGRectMake(0,0,0,0)
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



    func offsetImage(yOffset: CGFloat) {
        var scaleAmount = -yOffset / 150 + 1;
        var trans = CGAffineTransformMakeTranslation(0,(yOffset * 1.2));
        var scale = CGAffineTransformMakeScale(scaleAmount, scaleAmount);
        self.headerImage.transform = CGAffineTransformConcat(scale, trans);

    }


    func checkViewHeightAndCorrect() {

        if (self.scrollView.frame.height < UIScreen.mainScreen().bounds.height) {
            self.restoreView(0);
        }

    }

    func shortenView(notification: NSNotification) {
            scrollViewFrame = self.scrollView.frame;
            scrollViewBounds = self.scrollView.bounds;


            UIView.animateWithDuration(0.5) {
                var keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.CGRectValue()
                var keyboardHeight = keyboardSize!.height;
                var newSize = UIScreen.mainScreen().bounds.height - (keyboardHeight - nav!.tabBar.frame.height);

                self.scrollView.snp_remakeConstraints {
                    (make) -> Void in
                    make.left.equalTo(self).offset(0)
                    make.top.equalTo(self).offset(0)
                    make.right.equalTo(self).offset(0)
                    make.bottom.equalTo(self).offset(-keyboardHeight + nav!.tabBar.frame.height)
                }



                self.layoutIfNeeded()
            }

        if (descriptionEditor.firstResponderCheck()) {
            var bottomOffset = CGPointMake(0, self.scrollView.contentSize.height - self.scrollView.bounds.size.height);
            scrollView.setContentOffset(bottomOffset, animated: false)
        }
    }

    func restoreView(time: Double) {
        UIView.animateWithDuration(time) {

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

        var url = NSURL(string: imageUrl.getPUPUrl())
        println(url)
        var request:NSURLRequest = NSURLRequest(URL: url!)
        headerImage.backgroundColor = UIColor.blackColor();

        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            if (data != nil) {
                var img = UIImage(data: data) as UIImage!
                self.headerImage.image = img;
                self.headerImage.contentMode = UIViewContentMode.ScaleAspectFill;
                self.headerImage.clipsToBounds = true;
            }
        })

    }




    func setUpView(parentController: CreateLobbyController, dateDisplay: DateDisplayView) {
        var buttonDelegate = parentController as SimpleButtonDelegate;
        var searchDelegate = parentController as UISearchBarDelegate;
        createLobbyButton.addTarget(parentController, action: "createLobby", forControlEvents: .TouchUpInside)

        createLobbyButton.setTitle("CREATE LOBBY", forState: .Normal)
        createLobbyButton.titleLabel?.font = UIConstants.buttonType;
        createLobbyButton.titleLabel?.textAlignment = NSTextAlignment.Center;
        createLobbyButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        createLobbyButton.backgroundColor = UIColor(rgba: colors.tealMain);




        descriptionEditor.setDelegate(parentController as UITextViewDelegate)
        descriptionEditor.setUpView()


        for i in 0...appData.platforms.count-1 {
            platforms.append(PlatformButtonToggle())
            platforms[i].setUpButton(appData.platforms[i], delegate: buttonDelegate)
        }

        scrollView.delegate = parentController;
        scrollView.scrollEnabled = true;

        containerView.userInteractionEnabled = true

        searchBarBackground.backgroundColor = UIColor.whiteColor();

        searchBar.delegate = searchDelegate
        searchBar.enablesReturnKeyAutomatically = false
        searchBar.searchBarStyle = UISearchBarStyle.Minimal;
        searchBar.placeholder = "Select a Game..."

        headerImage.backgroundColor = UIColor.blackColor();
        headerImage.image = UIImage(named: "createLobbyBackground");
        self.headerImage.contentMode = UIViewContentMode.ScaleAspectFill;
        self.headerImage.clipsToBounds = true;

        self.dateDisplay = dateDisplay
        self.dateDisplay?.setUpView()

        pickSystemText.text = "PICK A SYSTEM";
        pickSystemText.font = UIConstants.titleFont;
        pickSystemText.textAlignment = NSTextAlignment.Center
        pickSystemText.textColor = UIColor(rgba: colors.mainGrey)

        pickSystemText.backgroundColor = UIColor.whiteColor();
        addViews()
        layoutViews()
        //self.scrollView.contentSize = CGSize(width:scrollView.bounds.size.width, height: 1900);
        self.scrollView.indicatorStyle = .Default

        println("contentSize")
        println(scrollView.contentSize)

    }

    func pressIt() {
        self.createLobbyButton.addIndicator();

        self.createLobbyButton.activityIndicator.startAnimating();
        createLobbyButton.backgroundColor = UIColor(rgba: colors.tealMain).darkerColor(0.3);

    }

    func releaseIt() {
        self.createLobbyButton.activityIndicator.stopAnimating();
        createLobbyButton.backgroundColor = UIColor(rgba: colors.tealMain);

    }


    func setTextSize() {
        self.dateDisplay?.setText()
    }

    func closeKeyboard() {
        searchBar.resignFirstResponder()

    }



    func addViews() {

        self.containerView.addSubview(headerImage);
        self.containerView.addSubview(searchBarBackground);
        self.containerView.addSubview(searchBar);
        self.containerView.addSubview(pickSystemText)
        self.containerView.addSubview(dateDisplay!)
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
            make.height.equalTo(830 + (UIConstants.lobbyImageHeight * 0.25) + (UIConstants.verticalPadding*2) + 40)
        }

        self.headerImage.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.top.equalTo(self.containerView).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(UIConstants.lobbyImageHeight * 0.75)
        }

        self.searchBarBackground.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.bottom.equalTo(self.headerImage.snp_bottom).offset(-UIConstants.halfVerticalPadding+10)
            make.height.equalTo(35)
        }


        self.pickSystemText.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
            make.top.equalTo(self.headerImage.snp_bottom).offset(UIConstants.verticalPadding * 2 + 10)
        }
        self.searchBar.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.containerView).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self.pickSystemText.snp_top).offset(-UIConstants.verticalPadding)
            make.height.equalTo(30)
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
            make.top.equalTo(self.platforms[4].snp_bottom).offset(UIConstants.verticalPadding * 2)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(90)

        }



        self.descriptionEditor.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.bottom.equalTo(self.containerView).offset(-105)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(120)

        }

        self.createLobbyButton.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.right.equalTo(self.containerView).offset(0)
            make.bottom.equalTo(self.containerView).offset(0)
            make.height.equalTo(85)

        }

        dateDisplay!.layoutView()
        dateDisplay!.setText();


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
        self.title.text = "DESCRIPTION";
        self.title.font = UIConstants.titleFont;
        self.title.textColor = UIColor(rgba: colors.mainGrey)
        self.title.textAlignment = NSTextAlignment.Center

        self.descriptionField.text = UIConstants.descriptionPlaceholder
        self.descriptionField.layer.borderColor = UIColor(rgba: colors.lightGray).CGColor
        self.descriptionField.layer.borderWidth = 0.4;
        self.descriptionField.font = UIConstants.paragraphType;
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
            make.height.equalTo(90)

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