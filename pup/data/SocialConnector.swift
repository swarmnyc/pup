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



class SocialViewController: UIViewController {

    var OAuthWebView: UIWebView = UIWebView()
    var onCancel: (() -> Void)?
    var didHitBackButton = true;
    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .Gray)
    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }


    func startLoader() {
        activityIndicator.startAnimating();
    }

    func stopLoader() {
        activityIndicator.stopAnimating();
    }




    func setDelegate(delegate: UIWebViewDelegate) {
        OAuthWebView.delegate = delegate;
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        var barButton = UIBarButtonItem(customView: activityIndicator);
        self.navigationItem.rightBarButtonItem = barButton;
        self.view.addSubview(OAuthWebView)

        self.OAuthWebView.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self.view)
            make.top.equalTo(self.view)
            make.bottom.equalTo(self.view)
            make.right.equalTo(self.view)
        }

    }

    func setTheTitle(service: String) {
        title = "Login to " + service.capitalizeIt
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)

        didHitBackButton = true;

        self.navigationController?.navigationBar.translucent = false;
//        self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
//        self.navigationController?.navigationBar.shadowImage = nil

    }

    override func viewWillDisappear(animated: Bool) {
        self.navigationController?.navigationBar.translucent = false;

        if (didHitBackButton) {
            onCancel?();
        }
    }

}




class SocialConnector: NSObject, UIWebViewDelegate, UIScrollViewDelegate {

    var type = SocialConnect.Facebook;
    var service = ""
    var OAuthWebView: UIWebView = UIWebView()
    var cancelOauth: ((String) -> Void)?

    var progressBar: UIView = UIView();
    var label = UILabel();
    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .Gray)
    var parentViewController: UIViewController?
    var socialController = SocialViewController();

    convenience init(viewController: UIViewController?) {
        self.init()
        OAuthWebView.delegate = self;
        OAuthWebView.scrollView.delegate = self;

        if (viewController != nil) {
            parentViewController = viewController!;
        }

        socialController.setDelegate(self as! UIWebViewDelegate);
        socialController.onCancel =  self.cancelTheAuthentication;

        progressBar.backgroundColor = UIColor.whiteColor();
        label.textAlignment = NSTextAlignment.Center
        progressBar.addSubview(label)
        progressBar.addSubview(activityIndicator)


    }

  func startAuthentication() {
      if (type == .Facebook) {

          setUpUsingFacebookSDK();

      } else {

          setUpOurOwnApi();


      }
  }


func cancelTheAuthentication() {
    self.cancelOauth?(self.service);

}



    func hideView(removeAfter: Bool) {
        self.socialController.didHitBackButton = false;
        parentViewController?.navigationController?.popViewControllerAnimated(true)


    }

    func setTypeAndAuthenticate(service: String) {
        self.service = service.lowercaseString;
        self.label.text = "Add your " + self.service.capitalizeIt + " account";
        switch (self.service) {
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

        if (isAlreadyAuthenticated() == false && currentUser.loggedIn()) {
            loginToFacebook()
        } else {
            println("is authenticated")
        }

    }


    func loginToFacebook() {
        var login = FBSDKLoginManager();
        login.logInWithPublishPermissions(["publish_actions"], handler: {
            (result, error) -> Void in
            if (error != nil) {

                self.cancelOauth?("facebook")
                Error(alertTitle: "There was an error", alertText: "Please try logging into facebook again.")
            } else if (result.isCancelled) {

                self.cancelOauth?("facebook");
                Error(alertTitle: "Decided not to connect facebook?", alertText: "That's ok, you can always connect it later")
            } else {

                self.sendFacebookDataToPup((result.token.tokenString, result.token.userID, result.token.expirationDate));
            }

        })
    }


    func sendFacebookDataToPup(fbData: (String, String, NSDate)) {
        //date setup
        var expiredAtUtc = "";

        var dateFormatter = NSDateFormatter();
        var timeZone = NSTimeZone(name: "UTC")
        dateFormatter.timeZone = timeZone;
        dateFormatter.dateFormat = "yyy-MM-dd HH:mm:ss";
        var dateString = dateFormatter.stringFromDate(fbData.2) as! String

        expiredAtUtc = dateString.stringByReplacingOccurrencesOfString(" ", withString: "T", options: NSStringCompareOptions.LiteralSearch, range: nil) + "Z"

        var AccessToken = fbData.0;
        var userID = fbData.1;

        currentUser.data.fbAccessToken = AccessToken;
        currentUser.data.fbUserId = userID;
        currentUser.setLocalStorage();

        SRWebClient.POST(urls.User + "SocialMedia")
        .headers(["Authorization":"Bearer " + currentUser.data.accessToken, "Content-Type":"application/x-www-form-urlencoded"])
        .data(["type": "facebook", "token": AccessToken, "userId": userID, "expireAtUtc": expiredAtUtc])
        .send({(response:AnyObject!, status:Int) -> Void in
            self.saveAuthenticationStatus();
            //process success response
        },failure:{(error:NSError!) -> Void in
            self.cancelOauth?("facebook")
            Error(alertTitle: "There was an error", alertText: "Please try logging into facebook again.")
            //process failure response
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

    }


    func showOAuthScreen(OAuthUrl: String) {
        println("show oauth screen")
        self.socialController.hidesBottomBarWhenPushed = true;
        parentViewController?.navigationController?.pushViewController(self.socialController, animated: true);
        self.socialController.setTheTitle(self.service);
//        if (OAuthWebView.superview == nil) {
//            println(self.parentView!);
////            self.parentView?.addSubview(OAuthWebView)
////            self.parentView?.addSubview(progressBar)
//
//            self.OAuthWebView.snp_remakeConstraints {
//                (make) -> Void in
//                make.top.equalTo(self.OAuthWebView.superview!).offset(80)
//                make.left.equalTo(self.OAuthWebView.superview!).offset(0)
//                make.right.equalTo(self.OAuthWebView.superview!).offset(0)
//                make.bottom.equalTo(self.OAuthWebView.superview!).offset(0)
//            }
//
//
//            self.progressBar.snp_remakeConstraints {
//                (make) -> Void in
//                make.top.equalTo(self.OAuthWebView.superview!).offset(0)
//                make.left.equalTo(self.OAuthWebView.superview!).offset(0)
//                make.right.equalTo(self.OAuthWebView.superview!).offset(0)
//                make.height.equalTo(80)
//            }
//
//            self.label.snp_remakeConstraints {
//                (make) -> Void in
//                make.centerX.equalTo(self.progressBar.snp_centerX).offset(0)
//                make.centerY.equalTo(self.progressBar.snp_centerY).offset(20)
//                make.width.equalTo(UIScreen.mainScreen().bounds.size.width - 80)
//
//            }
//
//            self.activityIndicator.snp_remakeConstraints {
//                (make) -> Void in
//                make.right.equalTo(self.progressBar).offset(0)
//                make.centerY.equalTo(self.progressBar.snp_centerY).offset(20)
//                //make.bottom.equalTo(self.progressBar).offset(0)
//                make.left.equalTo(self.progressBar.snp_right).offset(-40)
//
//            }
//
//            var trans = CGAffineTransformMakeTranslation(0, UIScreen.mainScreen().bounds.size.height)
//            self.OAuthWebView.transform = trans;
//
//            var trans2 = CGAffineTransformMakeTranslation(0,-80);
//            self.progressBar.transform = trans2;
//        }
//
//        UIView.animateWithDuration(0.4, animations: {
//            var trans = CGAffineTransformMakeTranslation(0,0)
//            self.OAuthWebView.transform = trans;
//            self.progressBar.transform = trans;
//        });


        self.socialController.OAuthWebView.loadRequest(NSURLRequest(URL: NSURL(string: OAuthUrl)!))

    }

    func saveAuthenticationStatus() {
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

        self.socialController.startLoader();

        println(webView.request!.URL!.absoluteString)
    }

    func webViewDidFinishLoad(webView: UIWebView) {
        println("Webview did finish load")
        self.socialController.stopLoader();
        println(webView.request!.URL!.absoluteString)

        var currentURL = webView.request!.URL!.absoluteString;
        if (currentURL?.lowercaseString.rangeOfString("/oauth/done") != nil) {
            hideView(true)
            saveAuthenticationStatus();
        }

        if (currentURL?.rangeOfString("/Reddit/Done") != nil) {
            hideView(true)
            println("success")
            redditSuccess();
        }
    }



    func sendInvites(sites: Array<SocialToggle>, lobbyData: LobbyData, success: (() -> Void)) {



        var inviteUrl = urls.lobbies + "invite/" + lobbyData.id + "?LocalTime=" + lobbyData.timeInHuman;

        var siteCount = 0;
        for (var i = 0; i<sites.count; i++) {
            if (sites[i].checked) {
                if (sites[i].site == .Reddit) {
                    println("we got a redittor")
                    redditCaptcha(lobbyData);
                } else {
                    inviteUrl += "&Types=" + sites[i].returnType;
                    siteCount++;
                }
            }
        }

        if (siteCount==0) {
            return
        }


        //&platforms=\(platforms[i])



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
                JLToast.makeText("Job's Done. Game has been shared", duration: JLToastDelay.LongDelay).show()
                success();
                println("success")
            } else {
                SwiftLoader.hide()
                println("failure")

            }

        }
    }

    func deleteService(site: String) {
        Alamofire.request(.DELETE, urls.User + "socialMedia", parameters: ["type": site])
        .response {
            (request, response, data, error) in
            println(request)
            println(response)
            println(data)
            println(error)
        }
    }

    func redditCaptcha(lobbyData: LobbyData) {
        var redditUrl = urls.siteBase + "/Reddit/Share?user_token=" + currentUser.data.accessToken + "&lobbyId=" + lobbyData.id + "&localTime=" + lobbyData.timeInHuman
       println(redditUrl);
        showOAuthScreen(redditUrl.URLEncodedString()!);
    }

    func redditSuccess() {
        JLToast.makeText("Game has been shared on Reddit!", duration: JLToastDelay.LongDelay).show()
    }
    
}