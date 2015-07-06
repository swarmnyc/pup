//
// Created by Alex Hartwell on 6/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import uservoice_iphone_sdk

class TabBarController: UITabBarController, UITabBarControllerDelegate {

    override func viewDidLoad() {
        super.viewDidLoad();
        self.delegate = self;
    }




    override func tabBar(tabBar: UITabBar, didSelectItem item: UITabBarItem) {

        if (item == tabBar.items![3] as! UITabBarItem) {
            println("is feedback")
            var config: UVConfig = UVConfig(site: "swarmnyc.uservoice.com")
            config.forumId = 272754;
            config.showKnowledgeBase = true;
            config.showForum = true;
            UserVoice.initialize(config);
            UserVoice.presentUserVoiceNewIdeaFormForParentViewController(self);


            nav!.selectedIndex = 0;
            nav!.selectedViewController!.viewDidAppear(true)
        }
    }

}