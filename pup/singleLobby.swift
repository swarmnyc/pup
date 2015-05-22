//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SingleLobbyController: UIViewController {

    var data: lobbyData = lobbyData();

    var topContentBox: UIView = UIView();
    var lobbyTitle: UITextView = UITextView();
    var lobbyImg: UIImageView = UIImageView();



    convenience init(info: lobbyData) {

        self.init();
        println(info);
        println(info.Name);
        data = info;




    }




    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor=UIColor.whiteColor()

        self.title = data.Name;
        setUpViews();

    }


    func insertViews() {

        topContentBox.addSubview(lobbyImg)
        topContentBox.addSubview(lobbyTitle)
        self.view.addSubview(topContentBox)


    }

    func setUpViews() {

        var url = NSURL(string: data.PictureUrl)

        var request:NSURLRequest = NSURLRequest(URL: url!)
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            var img = UIImage(data: data) as UIImage!
            self.lobbyImg.image = img;
            self.lobbyImg.contentMode = UIViewContentMode.ScaleAspectFill;
            self.lobbyImg.clipsToBounds = true;
        })

        lobbyTitle.text = data.Name;




        insertViews();
        setUpConstraints();
    }


    func setUpConstraints() {

        topContentBox.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view).offset(0)
            make.left.equalTo(self.view).offset(0)
            make.right.equalTo(self.view).offset(0)
            make.height.equalTo(250)
        }

        lobbyImg.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.topContentBox).offset(0)
            make.left.equalTo(self.topContentBox).offset(0)
            make.right.equalTo(self.topContentBox).offset(0)
            make.bottom.equalTo(self.topContentBox).offset(0)

        }


    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }




}