//
// Created by Alex Hartwell on 6/26/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import uservoice_iphone_sdk


class CustomTabIcon: UIImageView {

    var imageName: String?
    var animation: RAMItemAnimation?

    func setImageAndAnimation(imageName: String, animation: RAMItemAnimation) {
        self.imageName = imageName;
        var createImg = UIImage(named: imageName);
        self.image = createImg;
        self.animation = animation;
    }

    func animate() {
        self.animation?.playAnimation(self, textLabel: UILabel())

    }


}


class TabBarController: UITabBarController, UITabBarControllerDelegate {

    var backgroundStrip = UIView();
    var circleView = UIView();
    var createGameView: CustomTabIcon = CustomTabIcon();
    var createcontroller = CreateLobbyController();
    override func viewDidLoad() {
        super.viewDidLoad();
        self.delegate = self;

        let allGames = UINavigationController(rootViewController: LobbyListController());
        myGamesController.view.layoutIfNeeded()
        let myGames = UINavigationController(rootViewController: myGamesController);
        let newGame = UINavigationController(rootViewController: self.createcontroller);
        let settings = UINavigationController(rootViewController: settingsController);

        var config: UVConfig = UVConfig(site: "swarmnyc.uservoice.com")
        config.forumId = 272754;
        config.showKnowledgeBase = false;
        config.showForum = false;
        UserVoice.initialize(config);

        var feedBack = UserVoice.getUserVoiceContactUsFormForModalDisplay();
        feedBack.navigationController?.navigationItem.leftBarButtonItem = nil
        

        let controllers = [allGames, myGames, newGame, settings, feedBack];

        self.viewControllers = controllers;


        allGames.tabBarItem = UITabBarItem(title: nil, image: UIImage(named: "allgames")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal), tag: 0)
//        allGames.tabBarItem = UITabBarItem(title: nil, image: UIImage(named: "allgames")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal), tag: 0);
        allGames.tabBarItem.setTitlePositionAdjustment(UIOffsetMake(0.0,400));
        allGames.tabBarItem.selectedImage = UIImage(named: "allgamesselected")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal);

        myGames.tabBarItem = UITabBarItem(title: nil, image: UIImage(named: "mygames")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal), tag: 1);
        myGames.tabBarItem.setTitlePositionAdjustment(UIOffsetMake(0.0,400));
        myGames.tabBarItem.selectedImage = UIImage(named: "mygamesselected")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal);

        newGame.tabBarItem = UITabBarItem(title: nil, image: nil, tag: 2);
        newGame.tabBarItem.setTitlePositionAdjustment(UIOffsetMake(0.0,400));

        feedBack.tabBarItem = UITabBarItem(title: nil, image: UIImage(named: "feedback")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal), tag: 3);
        feedBack.tabBarItem.setTitlePositionAdjustment(UIOffsetMake(0.0,400));
        feedBack.tabBarItem.selectedImage = UIImage(named: "feedbackselected")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal);

        settings.tabBarItem = UITabBarItem(title: nil, image: UIImage(named: "profile")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal), tag: 4);
        settings.tabBarItem.setTitlePositionAdjustment(UIOffsetMake(0.0,400));
        settings.tabBarItem.selectedImage = UIImage(named: "profileselected")!.imageWithRenderingMode(UIImageRenderingMode.AlwaysOriginal);



        self.tabBar.backgroundImage = UIImage();
        self.tabBar.shadowImage = UIImage();
        self.tabBar.barTintColor = UIColor.clearColor();
        self.tabBar.tintColor = UIColor.clearColor();
        println("did load")

        self.createGameView.setImageAndAnimation("creategame", animation: RAMBounceAnimation() as! RAMItemAnimation)
//        var createImg = UIImage(named: "creategame");
//        self.createGameView.image = createImg;

        self.backgroundStrip.backgroundColor = UIColor.whiteColor();
        self.circleView.backgroundColor = UIColor.whiteColor();
        self.tabBar.addSubview(self.backgroundStrip)
        self.backgroundStrip.addSubview(self.circleView)
        self.backgroundStrip.addSubview(self.createGameView);
        self.tabBar.sendSubviewToBack(self.createGameView);
        self.tabBar.sendSubviewToBack(self.backgroundStrip);

        self.createGameView.frame = CGRectMake( (self.tabBar.frame.size.width / 2) - (self.tabBar.frame.height * 1.15 / 2), -7, (self.tabBar.frame.height * 1.15), (self.tabBar.frame.height * 1.15) );

        self.backgroundStrip.layer.shadowRadius = 0;
        self.backgroundStrip.layer.shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor
        self.backgroundStrip.layer.shadowOpacity = 1;
        self.backgroundStrip.layer.shadowOffset = CGSizeMake(0.0, -2.0);
        self.backgroundStrip.layer.masksToBounds = false;

        self.backgroundStrip.frame = self.tabBar.frame;
        self.backgroundStrip.frame.origin = CGPointMake(0.0,-8.0);
        self.circleView.frame.origin = CGPointMake( (self.tabBar.frame.size.width / 2) - (self.tabBar.frame.size.width / 4 / 2) , -(self.tabBar.frame.size.width / 4 / 4.25));
        self.circleView.frame.size = CGSizeMake(self.tabBar.frame.size.width / 4, self.tabBar.frame.size.width / 4);
        self.backgroundStrip.frame.size = CGSizeMake(self.tabBar.frame.size.width,400);
        self.circleView.layer.cornerRadius = self.circleView.frame.size.width / 2.0;
        self.circleView.layer.masksToBounds = true
    }

    
    
    func tabBarController(tabBarController: UITabBarController, shouldSelectViewController viewController: UIViewController) -> Bool {
        
        if (viewController == tabBarController.viewControllers![1] as! UIViewController && !currentUser.loggedIn()) {
            self.selectedIndex = 0;
           
            globalRegister.showJoinScreen({
                println("horrah!");
                nav!.selectedIndex = 1;
                nav!.selectedViewController!.viewDidAppear(true)
            })
            
            return false;
        }
//        else if (viewController == tabBarController.viewControllers![1] as! UIViewController) {
//            ((tabBarController.viewControllers![1] as! UINavigationController).topViewController as! MyChatsController).getMoreLobbies()
//        }
//
        if (viewController == tabBarController.viewControllers![3] as! UIViewController && !currentUser.loggedIn()) {
            self.selectedIndex = 0;
            
            globalRegister.showJoinScreen({
                println("horrah!");
                nav!.selectedIndex = 3;
                nav!.selectedViewController!.viewDidAppear(true)
            })
            return false;
        }

    

        return true;
        
    }
    
    func tabBarController(tabBarController: UITabBarController,
        didSelectViewController viewController: UIViewController) {
            
            if (viewController == tabBarController.viewControllers![1] as! UIViewController) {
                ((nav!.viewControllers?[1] as! UINavigationController).topViewController as! MyChatsController).GetMyLobbies();
            }
    }

    




    override func tabBar(tabBar: UITabBar, didSelectItem item: UITabBarItem) {

        if (item == tabBar.items![4] as! UITabBarItem) {
//            println("is feedback")
//            var config: UVConfig = UVConfig(site: "swarmnyc.uservoice.com")
//            config.forumId = 272754;
//            config.showKnowledgeBase = true;
//            config.showForum = true;
//            UserVoice.initialize(config);
//            UserVoice.presentUserVoiceNewIdeaFormForParentViewController(self);
//
//
//            nav!.selectedIndex = 0;
//            nav!.selectedViewController!.viewDidAppear(true)
        }


        if (item == tabBar.items![2] as! UITabBarItem) {

            self.createGameView.animate()


        }
    }

}


