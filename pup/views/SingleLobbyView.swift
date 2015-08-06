//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import Haneke

class SingleLobbyTopCell: UITableViewCell {

    var topContentBox: UIView = UIView();
    var lobbyTitle: UITextView = UITextView();
    var gradientBox: UIView = UIView();
    var gradient: CAGradientLayer = CAGradientLayer()
    var descBox: UIView = UIView();

    var topBoxCopy: UIView = UIView();
    var lobbyTitleCopy: UITextView = UITextView();
    var gradientBoxCopy: UIView = UIView();
    var gradientCopy: CAGradientLayer = CAGradientLayer()

    var tags: UILabel = UILabel();
    var dateAndTime: UITextView = UITextView()
    var desc: UITextView = UITextView()
    var divider: UIView = UIView()
    var isMember = false;


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        // println(style);
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }




    func setUpCell(data: singleLobby) {
        isMember = data.data.isMember


        self.userInteractionEnabled = false;
        self.backgroundColor = UIColor.clearColor();


        lobbyTitle.text = "\(data.data.owner.name)'s \n" +
                "\(data.data.name)";
        lobbyTitle.backgroundColor = UIColor.clearColor()
        lobbyTitle.textColor = UIColor.whiteColor()
        lobbyTitle.font = UIFont(name: "AvenirNext-Regular", size: 19.0)
        lobbyTitle.editable = false;
        lobbyTitle.scrollEnabled = false;
        lobbyTitle.userInteractionEnabled = false;

        lobbyTitleCopy.text = "\(data.data.owner.name)'s \n" +
                "\(data.data.name)";
        lobbyTitleCopy.backgroundColor = UIColor.clearColor()
        lobbyTitleCopy.textColor = UIColor.whiteColor()
        lobbyTitleCopy.font = UIFont(name: "AvenirNext-Regular", size: 19.0)
        lobbyTitleCopy.editable = false;
        lobbyTitleCopy.scrollEnabled = false;
        lobbyTitleCopy.userInteractionEnabled = false;

        gradient.colors = [UIColor.clearColor().CGColor, UIColor(red: 0, green: 0, blue: 0, alpha: 0.5).CGColor]
        gradient.startPoint = CGPoint(x: 0.5,y: 0.5)

        gradientCopy.colors = [UIColor.clearColor().CGColor, UIColor(red: 0, green: 0, blue: 0, alpha: 0.5).CGColor]
        gradientCopy.startPoint = CGPoint(x: 0.5,y: 0.5)


        //descBox.backgroundColor = UIColor.redColor()

        tags.text = data.data.getTagText
        tags.textColor = UIColor(rgba: colors.orange)
        tags.font = UIFont(name: "AvenirNext-Regular", size: 10.0)
        tags.userInteractionEnabled = false;




        desc.text = data.data.description
        desc.font = UIFont(name: "AvenirNext-Regular", size: 13.0)
        desc.userInteractionEnabled = false;
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.backgroundColor = UIColor.whiteColor()


        dateAndTime.text = data.data.platform + ": " + data.data.timeInHuman;
        dateAndTime.textColor = UIColor(rgba: colors.orange)
        dateAndTime.font = UIFont(name: "AvenirNext-Regular", size: 10.0)
        dateAndTime.userInteractionEnabled = false;
        dateAndTime.scrollEnabled = false
        dateAndTime.textAlignment = .Right;
        dateAndTime.textContainerInset = UIEdgeInsetsZero
        dateAndTime.textContainer.lineFragmentPadding = 0

        descBox.backgroundColor = UIColor.whiteColor();

        self.descBox.layer.masksToBounds = true;
        self.descBox.layer.shadowRadius = 0;
        self.descBox.layer.shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor
        self.descBox.layer.shadowOpacity = 1;
        self.descBox.layer.shadowOffset = CGSizeMake(0, -2.0);

        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2)
        insertViews();
        setUpConstraints()
    }

    func insertViews() {
        gradientBox.layer.insertSublayer(gradient, atIndex: 0)
        topContentBox.addSubview(gradientBox)
        topContentBox.addSubview(lobbyTitle)




        descBox.addSubview(tags)
        descBox.addSubview(desc)
        descBox.addSubview(dateAndTime)
        descBox.addSubview(divider)

        addSubview(topContentBox)
        addSubview(descBox)
    }



    func setUpConstraints() {


        topContentBox.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.lobbyImageHeight)
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
            make.bottom.equalTo(self)
        }



        tags.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.dateAndTime.snp_left).offset(-UIConstants.horizontalPadding / 4)
            make.height.equalTo(16)
        }

        dateAndTime.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.tags.snp_right).offset(UIConstants.horizontalPadding / 4)
            make.top.equalTo(self.descBox).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.descBox).offset(-UIConstants.horizontalPadding)
            make.height.equalTo(16)
        }

        desc.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.tags.snp_bottom).offset(UIConstants.horizontalPadding / 2)
            make.bottom.equalTo(self.descBox).offset(-UIConstants.horizontalPadding)
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

}


class SingleLobbyView: UIView {

    var joinLobbyButton: UIButton = UIButton();

    var newMessage: UITextView = UITextView();
    var send: UIButton = UIButton();
    var newMessageBackground = UIView();

    var isMember = false;
    var isFullHeight = true;

    var table: UITableView?
    var lobbyImg: UIImageView = UIImageView();
    var lobbyImgBack: UIImageView = UIImageView();


    var gradient: CAGradientLayer = CAGradientLayer()
    var gradientBox = UIView();

    var whiteBottom: UIView = UIView();
    var sendTheMessage: ((newMessage: String) -> Void)?

    var drawer: UIView?
    var imageCover: UIView?;

    var firstLoad = true;

    override init(frame: CGRect) {
        super.init(frame: frame)
        println(frame)
        println(frame.width)
        println(frame.height)

        clipsToBounds = true;




    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setHeaderCover(imgCover: UIView) {
        if (isMember) {

            self.table?.userInteractionEnabled = false; //ignore touches to table until messages are loaded

            println("header cover functions")
            self.imageCover = imgCover;
            self.userInteractionEnabled = true;

            gradient.colors = [UIColor.clearColor().CGColor, UIColor(red: 0, green: 0, blue: 0, alpha: 0.5).CGColor]
            gradient.startPoint = CGPoint(x: 0.5,y: 0.5)


            self.gradientBox.layer.insertSublayer(gradient, atIndex: 0)
            self.imageCover!.addSubview(self.gradientBox)
            self.imageCover!.sendSubviewToBack(self.gradientBox)

            self.addSubview(self.imageCover!)
            self.imageCover!.clipsToBounds = true;
            self.imageCover!.snp_makeConstraints {
                (make) -> Void in
                make.bottom.equalTo(self).offset(-(UIScreen.mainScreen().bounds.height - CGFloat(UIConstants.lobbyImageHeight)));
              //  make.bottom.equalTo(self).offset(0)
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.height.equalTo(UIScreen.mainScreen().bounds.height)
            }

            self.gradientBox.snp_makeConstraints {
                (make) -> Void in
                make.top.equalTo(self).offset(0)
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.bottom.equalTo(UIConstants.lobbyImageHeight)
            }


            println("preview image created")
            self.bringSubviewToFront(self.imageCover!)
        }
    }

    func addDrawer(drawer: UIView) {
        self.drawer = drawer;
        addSubview(self.drawer!)
        self.drawer?.snp_makeConstraints {
            (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
        }
    }


    func createTable() {
        table = UITableView();
        table?.registerClass(MessageCell.self, forCellReuseIdentifier: "message")
        insertTable();
        setUpTableConstraints();
        reloadTable();

    }

    func addTable(delegate: UITableViewDelegate) {

        table?.backgroundColor = UIColor.clearColor();
        table?.delegate = delegate;
        table?.dataSource = delegate as! UITableViewDataSource


        newMessage.delegate = delegate as! UITextViewDelegate;
        reloadTable();

    }


    func reloadTable() {
        table?.reloadData()

        if (self.imageCover != nil) {
            self.bringSubviewToFront(self.imageCover!)
        }
    }

    func hasMessages() -> Bool {
        if (table == nil) {
            return false;
        }
        return true;
    }


     func scaleImage(amount: CGFloat) {
         if (amount >= 1) {
             var scale = CGAffineTransformMakeScale(amount*1.1, amount*1.1)
             //  self.lobbyImg.layer.anchorPoint = CGPointMake(self.lobbyImg.bounds.size.width / 2.0, self.lobbyImg.bounds.size.height / 2.0)
             self.lobbyImg.transform = scale;
         }
     }

    func scrollImageAndFadeText(amount: CGFloat) {
        var opac:CGFloat = 1;
        if (amount >= 1) {
            var trans = CGAffineTransformMakeTranslation(0, -amount / 2)
            //  self.lobbyImg.layer.anchorPoint = CGPointMake(self.lobbyImg.bounds.size.width / 2.0, self.lobbyImg.bounds.size.height / 2.0)
            self.lobbyImg.transform = trans;
             opac = (1.0 - ((amount*1.08) / CGFloat(UIConstants.lobbyImageHeight)));

        }

            var headerCell = self.table?.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 0)) as? SingleLobbyTopCell
            if (headerCell != nil) {
                headerCell!.lobbyTitle.textColor = UIColor(red: 255, green: 255, blue: 255, alpha: opac);
            }

    }


    func isTyping() -> Bool {
        if (newMessage.isFirstResponder()) {
            return true;
        }
        return false;
    }

    func shortenView(notification: NSNotification) {
        if (newMessage.isFirstResponder()) {

            self.bringSubviewToFront(self.newMessageBackground);

            UIView.animateWithDuration(0.5) {
                var keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.CGRectValue().height

//
                var trans = CGAffineTransformMakeTranslation(0, -1 * (keyboardSize!));
                self.newMessageBackground.transform = trans;
                self.table!.snp_remakeConstraints { (make) -> Void in
                    make.height.equalTo(UIScreen.mainScreen().bounds.height - 58 - keyboardSize!)
                    make.width.equalTo(UIScreen.mainScreen().bounds.width);
                    make.right.equalTo(self).offset(0);
                    make.bottom.equalTo(self).offset(-58 - keyboardSize!);

                }

                self.lobbyImg.snp_remakeConstraints { (make) -> Void in
                    make.top.equalTo(self)
                    make.left.equalTo(self).offset(0)
                    make.right.equalTo(self).offset(0)
                    make.height.equalTo(Double(UIScreen.mainScreen().bounds.height) - Double(keyboardSize!) - 58)

                }


                self.layoutIfNeeded()
            }

            self.isFullHeight = false;

        }
    }

    func restoreView() {
        UIView.animateWithDuration(0.5) {
            var trans = CGAffineTransformMakeTranslation(0, 0);
            self.newMessageBackground.transform = trans;
            self.table!.snp_remakeConstraints { (make) -> Void in
                make.height.equalTo(UIScreen.mainScreen().bounds.height - 58)
                make.width.equalTo(UIScreen.mainScreen().bounds.width);
                make.right.equalTo(self).offset(0);
                make.bottom.equalTo(self).offset(-58);

            }

            self.lobbyImg.snp_remakeConstraints { (make) -> Void in
                make.top.equalTo(self)
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.height.equalTo(UIConstants.lobbyImageHeight * 1.1)

            }

            self.layoutIfNeeded()
        }

        self.isFullHeight = true;
        self.newMessage.layer.shadowColor = UIColor.clearColor().CGColor;
    }





    func insertTable() {
        self.addSubview(table!)

        bringSubviewToFront(newMessage)
        bringSubviewToFront(send)
        if (!isMember) {
            bringSubviewToFront(self.joinLobbyButton);

        }

        if (self.drawer != nil) {
            bringSubviewToFront(self.drawer!);
        }


    }

    func setUpTableConstraints() {

        table!.snp_remakeConstraints { (make) -> Void in
            make.height.equalTo(UIScreen.mainScreen().bounds.height - 58)
            make.width.equalTo(UIScreen.mainScreen().bounds.width);
            make.right.equalTo(self).offset(0);
            make.bottom.equalTo(self).offset(-58);

        }
        println("table constraints!!")
        if (isMember) {

           scrollToBottom(0);

           println("scrollBottom")

        }
    }

    func moveCoverImage() {
        println("move cover image")
        if (isMember && self.imageCover != nil) {
        self.table?.userInteractionEnabled = true;  //let people use the table now

            var yChange = -table!.contentOffset.y;
            if (yChange < -CGFloat(UIConstants.lobbyImageHeight)) {
                yChange = -CGFloat(UIConstants.lobbyImageHeight);
            }
            println("ychange ")
            println(yChange)
            UIView.animateWithDuration(1.0, animations: {
                var trans = CGAffineTransformMakeTranslation(0,yChange)
                self.imageCover!.transform = trans;

            })

        }
    }


    func setCoverImageTimer() {
        println("set cover image")
        var timeIt = NSTimer.scheduledTimerWithTimeInterval(0.7, target: self, selector: Selector("moveCoverImage"), userInfo: nil, repeats: false);
    }

    func scrollToBottom(duration: NSTimeInterval) {
        println("scroll to bottom")

            if (table!.contentSize.height - table!.frame.height > 0) {
                UIView.animateWithDuration(duration, animations: {
                    table?.contentOffset = CGPointMake(0, (table!.contentSize.height - table!.frame.height));
                })
            }
           // moveCoverImage()

    }



    func insertViews() {
       addSubview(lobbyImgBack)
       addSubview(lobbyImg)
       addSubview(whiteBottom)
        bringSubviewToFront(table!)
        addSubview(newMessageBackground)
        newMessageBackground.addSubview(newMessage)
        newMessageBackground.addSubview(send)
        if (!isMember) {
            addSubview(joinLobbyButton)
        }

        if (self.drawer != nil) {
            println("bringing drawer to front")
            bringSubviewToFront(drawer!)
        }


    }

    func sendMessage() {
        println("sending it")
        println(newMessage.text)
        sendTheMessage?(newMessage: newMessage.text);
        newMessage.resignFirstResponder()
    }


    func resizeMessageBox() {
        var trans = CGAffineTransformMakeTranslation(0,0);

        self.newMessageBackground.snp_remakeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
            make.height.equalTo(37 + 25)

        }


        self.newMessage.snp_remakeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 8.0)
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.height.equalTo(37)

        }

        self.newMessage.transform = trans;
    }

    func setUpDelegates(parentController: UIViewController) {
        joinLobbyButton.addTarget(parentController as! SingleLobbyController, action: "joinLobby", forControlEvents: .TouchUpInside)
    }

    func setUpViews(data: singleLobby) {

        isMember = data.data.isMember



        var url = NSURL(string: data.data.pictureUrl.getPUPUrl())
        println(data.data.pictureUrl)



        self.lobbyImg.frame.size = CGSizeMake(460, 500); //real image

        self.lobbyImgBack.contentMode = UIViewContentMode.ScaleAspectFill; //real image
        self.lobbyImgBack.clipsToBounds = true;
        self.lobbyImgBack.image = getImageWithColor(UIColor(rgba: colorFromSystem(data.data.platform)), CGSizeMake(460,500))
        self.lobbyImgBack.alpha = 0;

        self.lobbyImg.frame.size = CGSizeMake(460, 500);

        self.lobbyImg.contentMode = UIViewContentMode.ScaleAspectFill;
        self.lobbyImg.clipsToBounds = true;
        self.lobbyImg.alpha = 0;



        self.lobbyImg.hnk_setImageFromURL(url!, placeholder:nil, format: nil, failure: nil, success: {
                (image) -> Void in
                println("fading image");
                self.lobbyImg.image = image;

            })







        if (!isMember) {
            joinLobbyButton.setTitle("Join Lobby", forState: .Normal)
            joinLobbyButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
            joinLobbyButton.titleLabel?.font = UIFont(name: "AvenirNext-Medium", size: 12.0)
            joinLobbyButton.backgroundColor = UIColor(rgba: colors.tealMain);
        }

//        self.backgroundColor = UIColor(rgba: colors.lightGray);
        self.backgroundColor = UIColor.clearColor();

        newMessageBackground.backgroundColor = UIColor.whiteColor();


        self.newMessageBackground.layer.shadowRadius = 0;
        self.newMessageBackground.layer.shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor
        self.newMessageBackground.layer.shadowOpacity = 1;
        self.newMessageBackground.layer.shadowOffset = CGSizeMake(0, -2.0);

        newMessage.layer.masksToBounds = true;

        newMessage.layer.borderColor = UIColor.blackColor().lighterColor(0.35).CGColor;
        newMessage.layer.borderWidth = 0.5;
        newMessage.layer.cornerRadius = 5;
        newMessage.font = UIFont(name: "AvenirNext-Regular", size: 11.0)

        send.setTitle("Send", forState: .Normal)
        send.setTitleColor(UIColor(rgba: colors.tealDark), forState: .Normal);
        send.addTarget(self, action: "sendMessage", forControlEvents: UIControlEvents.TouchUpInside)
        send.titleLabel?.font = UIFont(name: "AvenirNext-Regular", size: 16.0);

        insertViews();
        setUpConstraints();
    }


    func isMessageBiggerThanTextBar() -> Bool {


       if (self.newMessage.contentSize.height + 5 != self.newMessage.frame.height) {
           return true;
       } else {
           return false;
       }

    }

    func increaseMessageInputSize() {

        var height = self.newMessage.frame.height;

            height = self.newMessage.contentSize.height + 5;
        if (height>60) {
            height = 60;
        }

            UIView.animateWithDuration(0.5, animations: {
            self.newMessage.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self).offset(UIConstants.horizontalPadding)
                make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 8.0)
                make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
                make.height.equalTo(height)

            }

            self.newMessageBackground.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.bottom.equalTo(self).offset(0)
                make.height.equalTo(height + 25)

            }
            });

        println(height);


    }



func clearText() {
        self.newMessage.text = "";
        self.resizeMessageBox();
    }


    func setUpConstraints() {

        lobbyImgBack.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.lobbyImageHeight)

        }
        lobbyImg.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.lobbyImageHeight * 1.17)

        }
        whiteBottom.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(lobbyImg.snp_bottom).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.lobbyImageHeight * 1.3)

        }

        self.newMessageBackground.snp_remakeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
            make.height.equalTo(37 + 25)

        }


        self.newMessage.snp_remakeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 8.0)
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.height.equalTo(37)

        }

        self.send.snp_remakeConstraints {
            (make) -> Void in
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
            make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            make.height.equalTo(35)
            make.width.equalTo(UIConstants.horizontalPadding * 7)
        }

        if (!isMember) {
            self.joinLobbyButton.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.bottom.equalTo(self).offset(0)
                make.height.equalTo(62)

            }
        }




    }



}