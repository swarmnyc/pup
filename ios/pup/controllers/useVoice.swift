//
// Created by Alex Hartwell on 6/25/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import uservoice_iphone_sdk

class FeedBackController: UIViewController {



    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }



    override func viewDidLoad() {
        super.viewDidLoad()

        goToAllGames();

    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated);
        goToAllGames();

    }

    func goToAllGames() {
        nav!.selectedIndex = 0;
        nav!.selectedViewController!.viewDidAppear(true)
    }
}