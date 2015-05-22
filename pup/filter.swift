//
// Created by Alex Hartwell on 5/22/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class filterView: UIViewController, UICollectionViewDelegateFlowLayout, UICollectionViewDataSource {
    var isOpen = false;

    var filterBox = UIView()
    var subView = UIView()

    var search = UITextField()

    convenience init(parentview: UIView) {
        self.init()
        subView = parentview;

        search.text = "HELLO"
        search.textColor = UIColor.whiteColor()
        search.backgroundColor = UIColor.blackColor()





        subView.addSubview(filterBox)
        filterBox.addSubview(search)

        filterBox.backgroundColor = UIColor.whiteColor()

        filterBox.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.subView).offset(0)
            make.right.equalTo(self.subView).offset(0)
            make.top.equalTo(self.subView).offset(0)
            make.height.equalTo(0)

        }

        search.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.filterBox).offset(16)
            make.right.equalTo(self.filterBox).offset(-16)
            make.top.equalTo(self.filterBox).offset(0)
            make.height.equalTo(20)

        }

        println("init")

    }

    override func viewDidLoad() {
        super.viewDidLoad()

    }

    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 14
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier("Cell", forIndexPath: indexPath) as! UICollectionViewCell
//        cell.backgroundColor = UIColor.orangeColor()
        return cell
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

    }


    func openFilter() {
        UIView.animateWithDuration(Double(0.5)) {
            self.filterBox.snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.subView).offset(0)
                make.right.equalTo(self.subView).offset(0)
                make.top.equalTo(self.subView).offset(0)
                make.height.equalTo(300)

                self.search.snp_remakeConstraints { (make) -> Void in
                    make.left.equalTo(self.filterBox).offset(16)
                    make.right.equalTo(self.filterBox).offset(-16)
                    make.top.equalTo(self.filterBox).offset(75)
                    make.height.equalTo(20)

                }

            }

            self.subView.layoutIfNeeded()
        }



    }


    func closeFilter() {
        UIView.animateWithDuration(Double(0.5)) {
            self.filterBox.snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.subView).offset(0)
                make.right.equalTo(self.subView).offset(0)
                make.top.equalTo(self.subView).offset(0)
                make.height.equalTo(0)

            }

            self.search.snp_remakeConstraints { (make) -> Void in
                make.left.equalTo(self.filterBox).offset(16)
                make.right.equalTo(self.filterBox).offset(-16)
                make.top.equalTo(self.filterBox).offset(75)
                make.height.equalTo(0)

            }

            self.subView.layoutIfNeeded()
        }



    }

}
