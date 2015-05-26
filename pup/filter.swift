//
// Created by Alex Hartwell on 5/22/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class filterView: UIViewController, UISearchBarDelegate {
    var isOpen = false;

    var filterBox = UIView()
    var parentView = UIView()
    var searchActive : Bool = false
    var search = UISearchBar()
    var buttonHeight = 75;
    var platforms: Array<UIButton> = [];


    convenience init(parentview: UIView) {
        self.init()
        parentView = parentview;



        setUpViews()


        println("init")

    }

    override func viewDidLoad() {
        super.viewDidLoad()
        search.delegate = self;
    }



    func setUpViews() {

//        search.textColor = UIColor.whiteColor()
        search.delegate = self;

        filterBox.backgroundColor = UIColor.whiteColor()
        filterBox.clipsToBounds = true;

        for i in 0...appData.platforms.count-1 {
            platforms.append(UIButton())
            platforms[i].setTitle(appData.platforms[i], forState: .Normal);
            platforms[i].setTitleColor(UIColor.blueColor(), forState: .Normal)
            platforms[i].titleLabel!.font = platforms[i].titleLabel!.font.fontWithSize(11)
            platforms[i].addTarget(self, action: "buttonAction:", forControlEvents: UIControlEvents.TouchUpInside)
        }


        addViews();
        setUpConstraints()

    }


    func buttonAction(sender: UIButton!) {
        println("test")
    }

    func setUpConstraints() {

        var fullW: CGFloat = self.parentView.frame.width
        var fullP: CGFloat = 2 * CGFloat(UIConstants.horizontalPadding)
        var thirdW: CGFloat = (fullW - fullP) / 3

        filterBox.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.parentView).offset(0)
            make.right.equalTo(self.parentView).offset(0)
            make.top.equalTo(self.parentView).offset(0)
            make.height.equalTo(0)


        }

        search.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.filterBox).offset(16)
            make.right.equalTo(self.filterBox).offset(-16)
            make.top.equalTo(self.filterBox).offset(0)
            make.height.equalTo(self.buttonHeight / 2)

        }

        self.platforms[0].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.filterBox).offset(16)
            make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.platforms[1].snp_left).offset(0)
            make.height.equalTo(self.buttonHeight)
            make.width.equalTo(thirdW)
        }
        self.platforms[1].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[0].snp_right).offset(0)
            make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.platforms[2].snp_left).offset(0)
            make.height.equalTo(self.buttonHeight)
            make.width.equalTo(thirdW)

        }

        self.platforms[2].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[1].snp_right).offset(0)
            make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.filterBox).offset(0)
            make.height.equalTo(self.buttonHeight)
            make.width.equalTo(thirdW)

        }
        self.platforms[3].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.filterBox).offset(16)
            make.top.equalTo(self.platforms[1].snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.platforms[1].snp_left).offset(0)
            make.height.equalTo(self.buttonHeight)
            make.width.equalTo(thirdW)
        }
        self.platforms[4].snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.platforms[0].snp_right).offset(0)
            make.top.equalTo(self.platforms[1].snp_bottom).offset(UIConstants.verticalPadding)
            make.right.equalTo(self.platforms[2].snp_left).offset(0)
            make.height.equalTo(self.buttonHeight)
            make.width.equalTo(thirdW)

        }


    }

    func addViews() {
        parentView.addSubview(filterBox)
        filterBox.addSubview(search)

        for i in 0...appData.platforms.count-1 {

            filterBox.addSubview(platforms[i])
            println(platforms[i])
        }
    }

    func toggleState() {
        if isOpen {
            isOpen = false
            closeFilter()
        } else {
            isOpen = true
            openFilter()
        }
        println(isOpen)
        println(platforms[0])
    }


    func openFilter() {
        search.becomeFirstResponder()



        UIView.animateWithDuration(Double(0.5)) {

            var fullW: CGFloat = self.parentView.frame.width
            var fullP: CGFloat = 2 * CGFloat(UIConstants.horizontalPadding)
            var thirdW: CGFloat = (fullW - fullP) / 3

            self.filterBox.snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.parentView).offset(0)
                make.right.equalTo(self.parentView).offset(0)
                make.top.equalTo(self.parentView).offset(0)
                make.height.equalTo(300)

                self.search.snp_remakeConstraints { (make) -> Void in
                    make.left.equalTo(self.filterBox).offset(16)
                    make.right.equalTo(self.filterBox).offset(-16)
                    make.top.equalTo(self.filterBox).offset(75)
                    make.height.equalTo(self.buttonHeight / 2)

                }

                self.platforms[0].snp_remakeConstraints { (make) -> Void in
                    make.left.equalTo(self.filterBox).offset(16)
                    make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
                    make.right.equalTo(self.platforms[1].snp_left).offset(0)
                    make.height.equalTo(self.buttonHeight)
                    make.width.equalTo(thirdW)
                }
                self.platforms[1].snp_remakeConstraints { (make) -> Void in
                    make.left.equalTo(self.platforms[0].snp_right).offset(0)
                    make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
                    make.right.equalTo(self.platforms[2].snp_left).offset(0)
                    make.height.equalTo(self.buttonHeight)
                    make.width.equalTo(thirdW)

                }

                self.platforms[2].snp_remakeConstraints { (make) -> Void in
                    make.left.equalTo(self.platforms[1].snp_right).offset(0)
                    make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
                    make.right.equalTo(self.filterBox).offset(0)
                    make.height.equalTo(self.buttonHeight)
                    make.width.equalTo(thirdW)

                }
                self.platforms[3].snp_remakeConstraints { (make) -> Void in
                    make.left.equalTo(self.filterBox).offset(16)
                    make.top.equalTo(self.platforms[1].snp_bottom).offset(UIConstants.verticalPadding)
                    make.right.equalTo(self.platforms[1].snp_left).offset(0)
                    make.height.equalTo(self.buttonHeight)
                    make.width.equalTo(thirdW)
                }
                self.platforms[4].snp_remakeConstraints { (make) -> Void in
                    make.left.equalTo(self.platforms[0].snp_right).offset(0)
                    make.top.equalTo(self.platforms[1].snp_bottom).offset(UIConstants.verticalPadding)
                    make.right.equalTo(self.platforms[2].snp_left).offset(0)
                    make.height.equalTo(self.buttonHeight)
                    make.width.equalTo(thirdW)

                }

            }

            self.parentView.layoutIfNeeded()
        }



    }




    func closeFilter() {
        search.resignFirstResponder()

        UIView.animateWithDuration(Double(0.5)) {
            var fullW: CGFloat = self.parentView.frame.width
            var fullP: CGFloat = 2 * CGFloat(UIConstants.horizontalPadding)
            var thirdW: CGFloat = (fullW - fullP) / 3
            self.filterBox.snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.parentView).offset(0)
                make.right.equalTo(self.parentView).offset(0)
                make.top.equalTo(self.parentView).offset(0)
                make.height.equalTo(0)

            }

            self.search.snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.filterBox).offset(16)
                make.right.equalTo(self.filterBox).offset(-16)
                make.top.equalTo(self.filterBox).offset(75)
                make.height.equalTo(self.buttonHeight / 2)

            }
            self.platforms[0].snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.filterBox).offset(16)
                make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
                make.right.equalTo(self.platforms[1].snp_left).offset(0)
                make.height.equalTo(self.buttonHeight)
                make.width.equalTo(thirdW)
            }
            self.platforms[1].snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.platforms[0].snp_right).offset(0)
                make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
                make.right.equalTo(self.platforms[2].snp_left).offset(0)
                make.height.equalTo(self.buttonHeight)
                make.width.equalTo(thirdW)

            }

            self.platforms[2].snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.platforms[1].snp_right).offset(0)
                make.top.equalTo(self.search.snp_bottom).offset(UIConstants.verticalPadding)
                make.right.equalTo(self.filterBox).offset(0)
                make.height.equalTo(self.buttonHeight)
                make.width.equalTo(thirdW)

            }


            self.parentView.layoutIfNeeded()
        }



    }

    func searchBarTextDidBeginEditing(searchBar: UISearchBar) {
        searchActive = true;
        println("beginEditing")
    }

    func searchBarTextDidEndEditing(searchBar: UISearchBar) {
        searchActive = false;
        println("endEditing")
    }

    func searchBarCancelButtonClicked(searchBar: UISearchBar) {
        searchActive = false;
        println("cancel clicked")
    }

    func searchBarSearchButtonClicked(searchBar: UISearchBar) {
        searchActive = false;
        println("searchButtonClicked")
    }



    func searchBar(searchBar: UISearchBar, textDidChange searchText: String) {

        println(searchText)
    }



}
