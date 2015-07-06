//
// Created by Alex Hartwell on 6/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class TabSwiper: NSObject {

    init(view: UIView) {
        super.init();
        var rightSwipe = UISwipeGestureRecognizer(target: self, action: "swipedRight");
        rightSwipe.direction = UISwipeGestureRecognizerDirection.Right;
        view.addGestureRecognizer(rightSwipe);

        var leftSwipe = UISwipeGestureRecognizer(target: self, action: "swipedLeft");
        leftSwipe.direction = UISwipeGestureRecognizerDirection.Left;
        view.addGestureRecognizer(leftSwipe);
    }

    func swipedRight() {
        var currentPageIndex = nav!.selectedIndex;
        println("currentIndex " + String(currentPageIndex))
        currentPageIndex--;
        if (currentPageIndex<0) {
            currentPageIndex = 0;
        } else {
            if (currentPageIndex == 3) {
                currentPageIndex = 2;
            }
            nav!.selectedIndex = currentPageIndex;
            nav!.selectedViewController!.viewDidAppear(true)
        }
    }

    func swipedLeft() {
        var currentPageIndex = nav!.selectedIndex;

        currentPageIndex++;
        if (currentPageIndex>nav!.tabBar.items?.count) {
            currentPageIndex = nav!.tabBar.items!.count;
        } else {
            if (currentPageIndex == 3) {
                currentPageIndex = 4;
            }
            nav!.selectedIndex = currentPageIndex;
            nav!.selectedViewController!.viewDidAppear(true)
        }

    }


}