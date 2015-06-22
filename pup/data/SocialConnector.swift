//
// Created by Alex Hartwell on 6/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import FBSDKCoreKit
import FBSDKShareKit
import FBSDKLoginKit
import SwiftLoader
import Alamofire
import JLToast

enum SocialConnect {
    case Facebook, Twitter, Reddit, Tumblr;

}

enum useFacebookAPI {
    case No, Yes;
}

class SocialConnector: NSObject, UIWebViewDelegate, UIScrollViewDelegate {

    var type = SocialConnect.Facebook;
    var service = ""
    var OAuthWebView: UIWebView = UIWebView()
    var cancelOauth: ((String) -> Void)?

    override init() {
        super.init()
        OAuthWebView.delegate = self;
        OAuthWebView.scrollView.delegate = self;
    }

  func startAuthentication() {
      if (type == .Facebook) {

          setUpUsingFacebookSDK();

      } else {

            setUpOurOwnApi();


      }
  }


    func scrollViewDidScroll(scrollView: UIScrollView) {

        println(scrollView.contentOffset)
        if (scrollView.contentOffset.y < -25) {
            UIView.animateWithDuration(1.0, animations: {
                var trans = CGAffineTransformMakeTranslation(0, UIScreen.mainScreen().bounds.size.height)
                self.OAuthWebView.transform = trans;
                self.cancelOauth?(self.service);
            });
        }

    }

    func setTypeAndAuthenticate(service: String) {
        self.service = service;
        switch (service) {
            case "twitter":
                self.type = .Twitter
            case "reddit":
                self.type = .Reddit
            case "tumblr":
                self.type = .Tumblr
            default:
                self.type = .Facebook
        }

        startAuthentication();

    }


    func setUpUsingFacebookSDK() {
        var login = FBSDKLoginManager();
        login.logInWithReadPermissions(["email"], handler: {
            (result, error) -> Void in
            if (error != nil) {
                println("there was an error")
            } else if (result.isCancelled) {
                println("they cancelled it")
            } else {
                println("connected")
                println(result);
            }

        })
    }

    func setUpOurOwnApi() {


        var OAuthUrl = ""
        switch(self.type) {
            case .Reddit:
                OAuthUrl = urls.Reddit
            case .Tumblr:
                OAuthUrl = urls.Tumblr
            default:
                OAuthUrl = urls.Twitter
        }

        OAuthUrl += "?user_token=" + currentUser.data.accessToken



        if (isAlreadyAuthenticated() == false && currentUser.loggedIn()) {
            showOAuthScreen(OAuthUrl);
        } else {
            println("is authenticated")
        }
    }

    func isAlreadyAuthenticated() -> Bool {
        switch (self.type) {
        case .Tumblr:
           return currentUser.data.social["tumblr"]!;
        case .Twitter:
           return currentUser.data.social["twitter"]!;
        case .Reddit:
           return currentUser.data.social["reddit"]!;
        default:
           return currentUser.data.social["facebook"]!;
        }

        return false;
    }


    func showOAuthScreen(OAuthUrl: String) {
        if (OAuthWebView.superview == nil) {
            UIApplication.sharedApplication().windows.first!.addSubview(OAuthWebView)

            self.OAuthWebView.snp_remakeConstraints {
                (make) -> Void in
                make.top.equalTo(self.OAuthWebView.superview!).offset(150)
                make.left.equalTo(self.OAuthWebView.superview!).offset(0)
                make.right.equalTo(self.OAuthWebView.superview!).offset(0)
                make.bottom.equalTo(self.OAuthWebView.superview!).offset(0)
            }

            var trans = CGAffineTransformMakeTranslation(0, UIScreen.mainScreen().bounds.size.height)
            self.OAuthWebView.transform = trans;
        }

        UIView.animateWithDuration(1.0, animations: {
            var trans = CGAffineTransformMakeTranslation(0,0)
            self.OAuthWebView.transform = trans;
        });


        OAuthWebView.loadRequest(NSURLRequest(URL: NSURL(string: OAuthUrl)!))

    }

    func saveData() {
        switch (self.type) {
            case .Tumblr:
                currentUser.data.social["tumblr"] = true;
            case .Twitter:
                currentUser.data.social["twitter"] = true;
        case .Reddit:
            currentUser.data.social["reddit"] = true;
            default:
                currentUser.data.social["facebook"] = true;
        }
        currentUser.setLocalStorage();
    }







    func webViewDidStartLoad(webView: UIWebView) {
        println("Webview started Loading")
        println(webView.request!.URL!.absoluteString)
    }

    func webViewDidFinishLoad(webView: UIWebView) {
        println("Webview did finish load")
        println(webView.request!.URL!.absoluteString)

        var currentURL = webView.request!.URL!.absoluteString;
        if (currentURL?.lowercaseString.rangeOfString("/oauth/done") != nil) {
            OAuthWebView.removeFromSuperview();
            saveData();
        }
    }



    func sendInvites(sites: Array<SocialToggle>, lobbyData: LobbyData) {



        var inviteUrl = urls.lobbies + "invite/" + lobbyData.id + "?LocalTime=" + lobbyData.timeInHuman;


        for (var i = 0; i<sites.count; i++) {
            if (sites[i].checked) {

                inviteUrl += "&Types=" + sites[i].returnType;
            }
        }

        //&platforms=\(platforms[i])

        var config = SwiftLoader.Config()
        config.size = 150
        config.spinnerColor = UIColor(rgba: colors.orange)
        config.backgroundColor = UIColor.whiteColor()
        SwiftLoader.setConfig(config);

        SwiftLoader.show(title: "Sharing...", animated: true)
        println(inviteUrl)
        let URL = NSURL(string: inviteUrl.URLEncodedString()!)
        println(URL)
        let mutableURLRequest = NSMutableURLRequest(URL: URL!)
        mutableURLRequest.HTTPMethod = "POST"

        var JSONSerializationError: NSError? = nil
        //mutableURLRequest.HTTPBody = urlEnd;
        mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")

        Alamofire.request(mutableURLRequest).responseJSON { (request, response, JSON, error) in
            println(response)
            println(JSON)
            if (error == nil) {

                SwiftLoader.hide()
                JLToast.makeText("Game has been shared!", duration: JLToastDelay.LongDelay).show()
                println("success")
            } else {
                SwiftLoader.hide()
                println("failure")

            }

        }
    }
    
}