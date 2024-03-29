//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class HorizontalSelectController: UIViewController, SimpleButtonDelegate, SwipeGestureDelegate {
    var parentController: UIViewController?
    var horizontalView: HorizontalSelectView = HorizontalSelectView();
    var data = HorizontalData()
    var onChange: ((String) -> Void)?;

    var options: Array<String> = []
    convenience init(parent: UIViewController, options: Array<String>, title: String, defaultSelection: Int) {
        self.init();
        println("hello im a horizontal selector!")
        parentController = parent;
        self.data.options = options;
        self.data.title = title;
        self.data.currentSelection = defaultSelection;

    }

    override func loadView() {
        println("horizontal selector loading")
        self.view = horizontalView;

    }

    override func viewDidLoad() {
        super.viewDidLoad();

        horizontalView.addOptions(data.options, title: data.title, delegate: self)
        println(self.data.currentSelection)

        self.onChange?(self.getCurrentSelection());


    }


    func swiped(direction: String) {
        switch (direction) {
            case "left":
                self.data.currentSelection++;
            case "right":
                self.data.currentSelection--;
            default:
                break;
        }

        self.horizontalView.slideLayout(self.data.currentSelection);
        println(self.data.currentSelection);

        self.onChange?(self.getCurrentSelection());

    }

    func getCurrentSelection() -> String {
        return self.horizontalView.getSelected(self.data.currentSelection)
    }

    func setUpView(parentView: UIView, bottomOffset: Double) {
        horizontalView.setUpView(parentView, bottomOffset: bottomOffset)
        horizontalView.slideLayout(self.data.currentSelection);
    }

    func touchDown(button: NSObject, type: String) {

    }
    func touchUp(button: NSObject, type: String) {
        self.data.currentSelection = self.data.getIndexFromString(type);
        self.horizontalView.slideLayout(self.data.currentSelection);

    }
}