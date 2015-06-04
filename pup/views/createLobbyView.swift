//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class CreateLobbyView: UIScrollView {

    var headerImage: UIImageView = UIImageView();
    var searchBar: UISearchBar = UISearchBar();
    var pickSystemText: UILabel = UILabel();
    var platforms: Array<Button> = [];



    override init(frame: CGRect) {
        super.init(frame: frame)

        backgroundColor=UIColor.whiteColor()




    }

    func setUpView(parentController: CreateLobbyController) {

        var buttonDelegate = parentController as SimpleButtonDelegate;
        var searchDelegate = parentController as UISearchBarDelegate;
        for i in 0...appData.platforms.count-1 {
            platforms.append(Button())
            platforms[i].setUpButton(appData.platforms[i], delegate: buttonDelegate)
        }

        searchBar.delegate = searchDelegate
        searchBar.enablesReturnKeyAutomatically = false

        headerImage.backgroundColor = UIColor.blackColor();


        pickSystemText.text = "PICK A SYSTEM";
        pickSystemText.font = pickSystemText.font.fontWithSize(11.0);
        pickSystemText.textAlignment = NSTextAlignment.Center
        addViews()
        layoutViews()

    }


    func closeKeyboard() {
        searchBar.resignFirstResponder()

    }



    func addViews() {
        self.addSubview(headerImage);
        self.addSubview(searchBar);
        self.addSubview(pickSystemText)

        for i in 0...appData.platforms.count-1 {
            self.addSubview(platforms[i])
            println(platforms[i])
        }


    }

    func layoutViews() {
        var fullW: CGFloat = UIScreen.mainScreen().bounds.size.width
        var fullP: CGFloat = 0
        var thirdW: CGFloat = (fullW - fullP) / 3


        self.headerImage.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.top.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.halfLobbyImage)
        }

        self.searchBar.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self.headerImage.snp_bottom).offset(-UIConstants.verticalPadding)
            make.height.equalTo(20)
        }

        self.pickSystemText.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.buttonHeight / 2.0)
            make.top.equalTo(self.headerImage.snp_bottom).offset(0)
        }

        self.platforms[0].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
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
            make.right.equalTo(self).offset(0)
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