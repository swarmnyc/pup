//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore

class SingleLobbyController: UIViewController {

    var data: singleLobby = singleLobby();

    var topContentBox: UIView = UIView();
    var lobbyTitle: UITextView = UITextView();
    var lobbyImg: UIImageView = UIImageView();
    var gradientBox: UIView = UIView();
    var gradient: CAGradientLayer = CAGradientLayer()

    var descBox: UIView = UIView();
    var startTime: UILabel = UILabel();
    var tags: UILabel = UILabel();
    var desc: UITextView = UITextView()
    var divider: UIView = UIView()

    convenience init(info: lobbyData) {

        self.init();
        println(info);
        println(info.Name);
        data.data = info;

        let requestUrl = NSURL(string: "\(urls.lobbies)\(data.data.id)")

        let task = NSURLSession.sharedSession().dataTaskWithURL(requestUrl!) {(data, response, error) in
            println(error)
            let jsonResponse = JSON(data: data)
            self.data.addDetailed(jsonResponse)
           // println(jsonResponse)
            dispatch_async(dispatch_get_main_queue(),{
                self.setUpViews()

            })
        }

        task.resume()




    }




    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor=UIColor.whiteColor()
         self.title = data.data.Name;
       // setUpViews();

    }


    func insertViews() {

        topContentBox.addSubview(lobbyImg)
       gradientBox.layer.insertSublayer(gradient, atIndex: 0)
        topContentBox.addSubview(gradientBox)
        topContentBox.addSubview(lobbyTitle)

        descBox.addSubview(startTime)
        descBox.addSubview(tags)
        descBox.addSubview(desc)
        descBox.addSubview(divider)

        self.view.addSubview(topContentBox)
        self.view.addSubview(descBox)


    }

    func setUpViews() {

        var url = NSURL(string: data.data.PictureUrl)

        var request:NSURLRequest = NSURLRequest(URL: url!)
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            var img = UIImage(data: data) as UIImage!
            self.lobbyImg.image = img;
            self.lobbyImg.contentMode = UIViewContentMode.ScaleAspectFill;
            self.lobbyImg.clipsToBounds = true;
        })

        lobbyTitle.text = "\(data.data.owner.name)'s \n" +
                          "\(data.data.Name)";
        lobbyTitle.backgroundColor = UIColor.clearColor()
        lobbyTitle.textColor = UIColor.whiteColor()
        lobbyTitle.font = lobbyTitle.font.fontWithSize(19)
        gradient.colors = [UIColor.clearColor().CGColor, UIColor(red: 0, green: 0, blue: 0, alpha: 0.5).CGColor]
        gradient.startPoint = CGPoint(x: 0.5,y: 0.5)


        //descBox.backgroundColor = UIColor.redColor()

        tags.text = data.data.getTagText
        tags.textColor = UIColor(rgba: colors.orange)
        tags.font = tags.font.fontWithSize(10)

        startTime.text = data.data.timeInHuman
        startTime.textColor = UIColor(rgba: colors.tealMain)
        startTime.font = startTime.font.fontWithSize(10)


        desc.text = data.data.Description
        desc.font = UIFont.systemFontOfSize(13.0)
        desc.editable = false
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.setTranslatesAutoresizingMaskIntoConstraints(false)
        desc.backgroundColor = UIColor.clearColor()


        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2)

        insertViews();
        setUpConstraints();
    }


    func setUpConstraints() {

        topContentBox.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.view).offset(0)
            make.left.equalTo(self.view).offset(0)
            make.right.equalTo(self.view).offset(0)
            make.height.equalTo(250)
        }


        lobbyImg.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.topContentBox).offset(0)
            make.left.equalTo(self.topContentBox).offset(0)
            make.right.equalTo(self.topContentBox).offset(0)
            make.bottom.equalTo(self.topContentBox).offset(0)

        }

        lobbyTitle.snp_remakeConstraints { (make) -> Void in
            make.bottom.equalTo(self.topContentBox).offset(0)
            make.left.equalTo(self.topContentBox).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.topContentBox).offset(-UIConstants.horizontalPadding)
            make.height.greaterThanOrEqualTo(75)
        }

        gradientBox.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.topContentBox).offset(0)
            make.right.equalTo(self.topContentBox).offset(0)
            make.top.equalTo(self.topContentBox).offset(0)
            make.bottom.equalTo(self.topContentBox).offset(0)


        }


        gradient.frame = CGRect(x: 0,y: 0,width: 800,height: 100) //set an initial value that is wider than needed to stop gradient from animating


        descBox.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.view).offset(0)
            make.right.equalTo(self.view).offset(0)
            make.top.equalTo(self.topContentBox.snp_bottom).offset(0)
            make.height.equalTo(100)
        }

        tags.snp_remakeConstraints { (make) -> Void in
            make.right.equalTo(self.descBox).offset(-UIConstants.horizontalPadding)
            make.top.equalTo(self.descBox).offset(UIConstants.horizontalPadding)

        }

        startTime.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.tags.snp_left).offset(5)
        }

        desc.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.tags.snp_bottom).offset(UIConstants.horizontalPadding)
            make.bottom.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.left.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.descBox).offset(-UIConstants.horizontalPadding)
        }

        divider.snp_remakeConstraints { (make) -> Void in
            make.bottom.equalTo(self.descBox.snp_bottom).offset(0)
            make.left.equalTo(self.descBox).offset(0);
            make.right.equalTo(self.descBox).offset(0);
            make.height.equalTo(UIConstants.dividerWidth);

        }

    }





    override func viewDidLayoutSubviews() {  //set up layer properties that don't get set by constraints
            super.viewDidLayoutSubviews();
            gradient.frame = gradientBox.frame;



    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }




}



class JoinButton {

    var parent: SingleLobbyController
    var view: UIView = UIView();
    var topLabel: UILabel = UILabel();
    var bottomLabel: UILabel = UILabel();

    init(parentController: SingleLobbyController) {
        parent = parentController;

        setUpViews();
    }

    func setUpViews() {


        addViews();
        setUpConstraints();
    }


    func addViews() {

        view.addSubview(topLabel)
        view.addSubview(bottomLabel)

        parent.view.addSubview(view)

    }

    func setUpConstraints() {
        view.snp_makeConstraints{(make) -> Void in
            make.left.equalTo(self.parent.view).offset(0)
            make.right.equalTo(self.parent.view).offset(0)
            make.bottom.equalTo(self.parent.view).offset(0)
            make.height.equalTo(58)

        }


    }


}