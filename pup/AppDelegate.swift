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

var nav: TabBarController?
let priority = DISPATCH_QUEUE_PRIORITY_DEFAULT
var myGamesController = MyChatsController();
var myChatsListener = MyChatsListener();
var settingsController = SettingsController();
var globalRegister: RegistrationController = RegistrationController(parentController: nil);
var mainWindow: UIWindow?


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {






    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {

        mainWindow = UIWindow(frame: UIScreen.mainScreen().bounds)

        mainWindow!.backgroundColor = UIColor(rgba: colors.lightGray)
        mainWindow!.makeKeyAndVisible()

        UINavigationBar.appearance().shadowImage = UIImage();
        UINavigationBar.appearance().layer.shadowRadius = 0;
        UINavigationBar.appearance().layer.shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor
        UINavigationBar.appearance().layer.shadowOpacity = 1;
        UINavigationBar.appearance().layer.shadowOffset = CGSizeMake(0, 2.0);

       // var menuController: MenuNavigationController? = MenuNavigationController( menuTableViewController: MenuTableController(), contentViewController: LobbyListController())
        nav = TabBarController();

//        nav?.delegate = TabBarController();
//        self.nav!.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName: UIColor(rgba: colors.tealMain)]
//         self.nav!.navigationBar.tintColor = UIColor(rgba: colors.tealMain)

        UILabel.appearance().font = UIFont(name: "AvenirNext-Medium", size: 11)
        UINavigationBar.appearance().tintColor = UIColor(rgba: colors.tealMain);
//        UITextField.appearance().font = UIFont(name: "Avenir Next", size: 11)
//        UITextView.appearance().font = UIFont(name: "Avenir Next", size: 11)


        mainWindow!.rootViewController = nav
        globalRegister.setUpView();
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
            myChatsListener.quickBloxConnect?.createSession();
        }

        if (currentUser.loggedIn()) {
            println(FBSDKAccessToken)
            println(FBSDKAccessToken.currentAccessToken())
            var token = FBSDKAccessToken.currentAccessToken();
            if (token != nil) {
            if (token.tokenString != currentUser.data.fbAccessToken) {
                println("facebook access token updated");
                currentUser.setNewToken((FBSDKAccessToken.currentAccessToken().tokenString, FBSDKAccessToken.currentAccessToken().userID, FBSDKAccessToken.currentAccessToken().expirationDate))
            }
        }
        }
    }

    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        if (QBChat.instance().isLoggedIn()) {
            QBChat.instance().logout()
        }
    }



}

