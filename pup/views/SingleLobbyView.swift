//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SingleLobbyView: UIView {




    var topContentBox: UIView = UIView();
    var lobbyTitle: UITextView = UITextView();
    var lobbyImg: UIImageView = UIImageView();
    var gradientBox: UIView = UIView();
    var gradient: CAGradientLayer = CAGradientLayer()

    var descBox: UIView = UIView();

    var tags: UILabel = UILabel();
    var desc: UITextView = UITextView()
    var divider: UIView = UIView()

    var joinLobbyButton: UIButton = UIButton();

    var newMessage: UITextField = UITextField();
    var send: UIButton = UIButton();

    var isMember = false;

    var table: UITableView?



    override init(frame: CGRect) {
        super.init(frame: frame)
        println(frame)
        println(frame.width)
        println(frame.height)
        backgroundColor=UIColor.blackColor()

        clipsToBounds = true;




    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func addTable(delegate: UITableViewDelegate) {
        table = UITableView();
        table?.delegate = delegate;
        table?.dataSource = delegate as! UITableViewDataSource
        table?.registerClass(MessageCell.self, forCellReuseIdentifier: "message")
        insertTable();
        setUpTableConstraints();
        table?.setContentOffset(CGPointMake(0, CGFloat.max), animated: false);

        newMessage.delegate = delegate as! UITextFieldDelegate;

    }

    func hasMessages() -> Bool {
        if (table == nil) {
            return false;
        }
        return true;
    }




    func shortenView(notification: NSNotification) {
        println("shortening view")
        if (newMessage.isFirstResponder()) {
            UIView.animateWithDuration(0.5) {
                var keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.CGRectValue()
                var keyboardHeight = keyboardSize!.height;

                var trans = CGAffineTransformMakeTranslation(0, -keyboardHeight - 35);
                self.transform = trans;


                self.layoutIfNeeded()
            }
        }




    }

    func restoreView() {
        UIView.animateWithDuration(0.5) {

            var trans = CGAffineTransformMakeTranslation(0, 0);
            self.transform = trans;

            self.layoutIfNeeded()
        }
    }





    func insertTable() {
        self.addSubview(table!)

        bringSubviewToFront(newMessage)
        bringSubviewToFront(send)
        if (!isMember) {
            bringSubviewToFront(self.joinLobbyButton);

        }


    }

    func setUpTableConstraints() {

        table!.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.divider.snp_bottom).offset(0)
            make.left.equalTo(self).offset(0);
            make.right.equalTo(self).offset(0);
            make.height.equalTo(600);

        }
    }

    func insertViews() {

        topContentBox.addSubview(lobbyImg)
        gradientBox.layer.insertSublayer(gradient, atIndex: 0)
        topContentBox.addSubview(gradientBox)
        topContentBox.addSubview(lobbyTitle)

        
        descBox.addSubview(tags)
        descBox.addSubview(desc)
        descBox.addSubview(divider)

        addSubview(topContentBox)
        addSubview(descBox)
        addSubview(newMessage)
        addSubview(send)
        if (!isMember) {
            addSubview(joinLobbyButton)
        }


    }

    func sendMessage() {
        println("sending it")
        println(newMessage.text)
        newMessage.resignFirstResponder()
    }


    func setUpDelegates(parentController: UIViewController) {
        joinLobbyButton.addTarget(parentController as! SingleLobbyController, action: "joinLobby", forControlEvents: .TouchUpInside)
    }

    func setUpViews(data: singleLobby) {

        isMember = data.isMember


        var url = NSURL(string: data.data.pictureUrl)

        var request:NSURLRequest = NSURLRequest(URL: url!)
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            var img = UIImage(data: data) as UIImage!
            self.lobbyImg.image = img;
            self.lobbyImg.contentMode = UIViewContentMode.ScaleAspectFill;
            self.lobbyImg.clipsToBounds = true;
        })


        if (!isMember) {
            joinLobbyButton.setTitle("Join Lobby", forState: .Normal)
            joinLobbyButton.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
            joinLobbyButton.backgroundColor = UIColor.whiteColor()
        }

        lobbyTitle.text = "\(data.data.owner.name)'s \n" +
                "\(data.data.name)";
        lobbyTitle.backgroundColor = UIColor.clearColor()
        lobbyTitle.textColor = UIColor.whiteColor()
        lobbyTitle.font = lobbyTitle.font.fontWithSize(19)
        lobbyTitle.editable = false;
        lobbyTitle.userInteractionEnabled = false;

        gradient.colors = [UIColor.clearColor().CGColor, UIColor(red: 0, green: 0, blue: 0, alpha: 0.5).CGColor]
        gradient.startPoint = CGPoint(x: 0.5,y: 0.5)


        //descBox.backgroundColor = UIColor.redColor()

        tags.text = data.data.getTagText
        tags.textColor = UIColor(rgba: colors.orange)
        tags.font = tags.font.fontWithSize(10)
        tags.userInteractionEnabled = false;




        desc.text = data.data.description
        desc.font = UIFont.systemFontOfSize(13.0)
        desc.editable = false
        desc.userInteractionEnabled = false;
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.backgroundColor = UIColor.clearColor()


        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2)

        newMessage.layer.masksToBounds = true;
        newMessage.layer.cornerRadius = 12;
        newMessage.layer.borderColor = UIColor.blackColor().CGColor;
        newMessage.layer.borderWidth = 1;

        send.setTitle("Send", forState: .Normal)
        send.setTitleColor(UIColor.blackColor(), forState: .Normal);
        send.addTarget(self, action: "sendMessage", forControlEvents: UIControlEvents.TouchUpInside)

        insertViews();
        setUpConstraints();
    }

    func makeTableViewLonger(howManyMessages: Int) {

        var howLong: Float = Float(howManyMessages) * 58.0;

        if (CGFloat(howLong) > UIScreen.mainScreen().bounds.height) {
            howLong = Float(UIScreen.mainScreen().bounds.height);
        }

        table!.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.divider.snp_bottom).offset(0)
            make.left.equalTo(self).offset(0);
            make.right.equalTo(self).offset(0);
            make.height.equalTo(howLong);

        }


    }


    func setUpConstraints() {


        topContentBox.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.lobbyImageHeight)
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
            make.height.greaterThanOrEqualTo(UIConstants.halfLobbyImage / 2.0)
        }

        gradientBox.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.topContentBox).offset(0)
            make.right.equalTo(self.topContentBox).offset(0)
            make.top.equalTo(self.topContentBox).offset(0)
            make.bottom.equalTo(self.topContentBox).offset(0)


        }


        gradient.frame = CGRect(x: 0,y: 0,width: 800,height: UIConstants.lobbyImageHeight) //set an initial value that is wider than needed to stop gradient from animating


        descBox.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.top.equalTo(self.topContentBox.snp_bottom).offset(0)
            make.height.equalTo(100)
        }



        tags.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.descBox).offset(-UIConstants.horizontalPadding)
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

        self.newMessage.snp_remakeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 8.0)
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.height.equalTo(25)

        }

        self.send.snp_remakeConstraints {
            (make) -> Void in
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.height.equalTo(25)
            make.width.equalTo(UIConstants.horizontalPadding * 7)
        }

        if (!isMember) {
            self.joinLobbyButton.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.bottom.equalTo(self).offset(0)
                make.height.equalTo(58)

            }
        }



    }



}