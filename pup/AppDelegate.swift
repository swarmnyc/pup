//
//  AppDelegate.swift
//  pup
//
//  Created by Alex Hartwell on 5/18/15.
//  Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import UIKit
import SwiftLoader
import uservoice_iphone_sdk

var colors: appColors = appColors();

var urls: appURLS = appURLS();

var UIConstants: UIValues = UIValues();

var currentUser: User = User()

var appData: miscData = miscData()

var nav:UITabBarController?
let priority = DISPATCH_QUEUE_PRIORITY_DEFAULT
var myGamesController = MyChatsController();
var myChatsListener = MyChatsListener();


//let cache = Shared.JSONCache
//let cache = Haneke.sharedJSONCache

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {


    var window: UIWindow?




    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
//        getUserVoiceContactUsFormForModalDisplay

        self.window = UIWindow(frame: UIScreen.mainScreen().bounds)

        self.window!.backgroundColor = UIColor.whiteColor()
        self.window!.makeKeyAndVisible()


       // var menuController: MenuNavigationController? = MenuNavigationController( menuTableViewController: MenuTableController(), contentViewController: LobbyListController())
        nav = TabBarController();
//        nav?.delegate = TabBarController();
//        self.nav!.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName: UIColor(rgba: colors.tealMain)]
//         self.nav!.navigationBar.tintColor = UIColor(rgba: colors.tealMain)


        let allGames = UINavigationController(rootViewController: LobbyListController());
        let myGames = UINavigationController(rootViewController: myGamesController);
        let newGame = UINavigationController(rootViewController: CreateLobbyController());
        let feedBack = UINavigationController(rootViewController: FeedBackController());
        let settings = UINavigationController(rootViewController: SettingsController());

        let controllers = [allGames, myGames, newGame, feedBack, settings];

        nav!.viewControllers = controllers;
        allGames.tabBarItem = UITabBarItem(title: "All Games", image: nil, tag: 0);
        myGames.tabBarItem = UITabBarItem(title: "My Games", image: nil, tag: 1);
        newGame.tabBarItem = UITabBarItem(title: "Create Game", image: nil, tag: 2);
        feedBack.tabBarItem = UITabBarItem(title: "Feedback", image: nil, tag: 3);
        settings.tabBarItem = UITabBarItem(title: "Settings", image: nil, tag: 4);
        if (!currentUser.loggedIn()) {
           myGames.tabBarItem.enabled = false;
        }
        self.window!.rootViewController = nav
         //self.nav!.setNavigationBarHidden(false, animated: false)
        //self.nav!.title = "All Games"

        return FBSDKApplicationDelegate.sharedInstance().application(application, didFinishLaunchingWithOptions: launchOptions)
//        return true
    }

    func applicationWillResignActive(application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        if (QBChat.instance().isLoggedIn()) {
            QBChat.instance().logout()
        }

    }

//- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
//return [[FBSDKApplicationDelegate sharedInstance] application:application
//openURL:url
//sourceApplication:sourceApplication
//annotation:annotation];
//}

    func application(application: UIApplication,
                     openURL url: NSURL,
                     sourceApplication: String?,
                     annotation: AnyObject?) -> Bool {
        return FBSDKApplicationDelegate.sharedInstance().application(
        application,
                openURL: url,
                sourceApplication: sourceApplication,
                annotation: annotation)
    }

    func applicationWillEnterForeground(application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.

        FBSDKAppEvents.activateApp();

    }

    func applicationDidBecomeActive(application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        if (currentUser.loggedIn()) {
            myChatsListener.quickBloxConnect?.createSession(true);
        }
    }

    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        if (QBChat.instance().isLoggedIn()) {
            QBChat.instance().logout()
        }
    }



}

