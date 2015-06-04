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
    var platforms: Array<Button> = [];

    var scrollView: UIScrollView = UIScrollView()
    var containerView: UIView = UIView()
    var dateDisplay: DateDisplayView = DateDisplayView();
    var timeDisplay: TimeDisplayView = TimeDisplayView();


    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor=UIColor.whiteColor()



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

    func setUpView(parentController: CreateLobbyController) {

        var buttonDelegate = parentController as SimpleButtonDelegate;
        var searchDelegate = parentController as UISearchBarDelegate;
        for i in 0...appData.platforms.count-1 {
            platforms.append(Button())
            platforms[i].setUpButton(appData.platforms[i], delegate: buttonDelegate)
        }

        scrollView.delegate = parentController;
        scrollView.scrollEnabled = true;

        containerView.userInteractionEnabled = true

        searchBar.delegate = searchDelegate
        searchBar.enablesReturnKeyAutomatically = false

        headerImage.backgroundColor = UIColor.blackColor();


        dateDisplay.setUpView()
        timeDisplay.setUpView()

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
        self.containerView.addSubview(dateDisplay)
        self.containerView.addSubview(timeDisplay)

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

        self.dateDisplay.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.top.equalTo(self.platforms[4].snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(90)

        }

        self.timeDisplay.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.containerView).offset(0)
            make.top.equalTo(self.dateDisplay.snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.containerView).offset(0)
            make.height.equalTo(90)

        }

        dateDisplay.layoutView()
        timeDisplay.layoutView()



    }

    func uncheckAllPlatforms() {
        for i in 0...appData.platforms.count-1 {
            platforms[i].uncheck()
        }
    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }






}