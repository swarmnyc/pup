//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class HorizontalSelectController: UIViewController, SimpleButtonDelegate {
    var parentController: UIViewController?
    var horizontalView: HorizontalSelectView = HorizontalSelectView();
    var options: Array<String> = []
    convenience init(parent: UIViewController, options: Array<String>) {
        self.init();
        println("hello im a horizontal selector!")
        parentController = parent;
        self.options = options;

    }

    override func loadView() {
        println("horizontal selector loading")
        self.view = horizontalView;

    }

    override func viewDidLoad() {
        super.viewDidLoad();

        horizontalView.addOptions(options, buttonDelegate: self)



    }

    func setUpView(parentView: UIView, topOffset: Double) {
        horizontalView.setUpView(parentView, topOffset: topOffset)

    }

    func touchDown(button: NSObject, type: String) {

    }
    func touchUp(button: NSObject, type: String) {

    }
}