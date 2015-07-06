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
        self.backgroundColor = UIColor.clearColor()


        lobbyTitle.text = "\(data.data.owner.name)'s \n" +
                "\(data.data.name)";
        lobbyTitle.backgroundColor = UIColor.clearColor()
        lobbyTitle.textColor = UIColor.whiteColor()
        lobbyTitle.font = lobbyTitle.font.fontWithSize(19)
        lobbyTitle.editable = false;
        lobbyTitle.scrollEnabled = false;
        lobbyTitle.userInteractionEnabled = false;

        lobbyTitleCopy.text = "\(data.data.owner.name)'s \n" +
                "\(data.data.name)";
        lobbyTitleCopy.backgroundColor = UIColor.clearColor()
        lobbyTitleCopy.textColor = UIColor.whiteColor()
        lobbyTitleCopy.font = lobbyTitleCopy.font.fontWithSize(19)
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
        tags.font = tags.font.fontWithSize(10)
        tags.userInteractionEnabled = false;




        desc.text = data.data.description
        desc.font = UIFont.systemFontOfSize(13.0)
        desc.editable = false
        desc.userInteractionEnabled = false;
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.backgroundColor = UIColor.whiteColor()

        descBox.backgroundColor = UIColor.whiteColor();

        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2)
        insertViews();
        setUpConstraints()
    }

    func insertViews() {
        gradientBox.layer.insertSublayer(gradient, atIndex: 0)
        topContentBox.addSubview(gradientBox)
        topContentBox.addSubview(lobbyTitle)

        gradientBoxCopy.layer.insertSublayer(gradientCopy, atIndex: 0)
        topBoxCopy.addSubview(gradientBoxCopy)
        topBoxCopy.addSubview(lobbyTitleCopy)


        descBox.addSubview(tags)
        descBox.addSubview(desc)
        descBox.addSubview(divider)

        addSubview(topContentBox)
        addSubview(descBox)
    }

    func getTopBox() -> UIView {
        return topBoxCopy;
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

        lobbyTitleCopy.snp_remakeConstraints { (make) -> Void in
            make.bottom.equalTo(self.topBoxCopy).offset(0)
            make.left.equalTo(self.topBoxCopy).offset(UIConstants.horizontalPadding)
            make.right.equalTo(self.topBoxCopy).offset(-UIConstants.horizontalPadding)
            make.height.greaterThanOrEqualTo(UIConstants.halfLobbyImage / 2.0)
        }

        gradientBox.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.topContentBox).offset(0)
            make.right.equalTo(self.topContentBox).offset(0)
            make.top.equalTo(self.topContentBox).offset(0)
            make.bottom.equalTo(self.topContentBox).offset(0)


        }

        gradientBoxCopy.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self.topBoxCopy).offset(0)
            make.right.equalTo(self.topBoxCopy).offset(0)
            make.top.equalTo(self.topBoxCopy).offset(0)
            make.bottom.equalTo(self.topBoxCopy).offset(0)


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

    var newMessage: UITextField = UITextField();
    var send: UIButton = UIButton();

    var isMember = false;

    var table: UITableView?
    var lobbyImg: UIImageView = UIImageView();
    var lobbyImgBack: UIImageView = UIImageView();

    var lobbyImgCopy: UIImageView = UIImageView();
    var gradient: CAGradientLayer = CAGradientLayer()
    var gradientBox = UIView();

    var whiteBottom: UIView = UIView();
    var sendTheMessage: ((newMessage: String) -> Void)?

    var drawer: UIView?
    var imageCover: UIView?;

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

    func setHeaderCover(imgCover: UIView) {
        if (isMember) {

            self.table?.userInteractionEnabled = false; //ignore touches to table until messages are loaded

            println("header cover functions")
            self.imageCover = imgCover;
            self.userInteractionEnabled = true;

            gradient.colors = [UIColor.clearColor().CGColor, UIColor(red: 0, green: 0, blue: 0, alpha: 0.5).CGColor]
            gradient.startPoint = CGPoint(x: 0.5,y: 0.5)

            self.imageCover!.addSubview(self.lobbyImgCopy) //add image and gradient/ bring the to the back

            self.gradientBox.layer.insertSublayer(gradient, atIndex: 0)
            self.imageCover!.addSubview(self.gradientBox)
            self.imageCover!.sendSubviewToBack(self.gradientBox)
            self.imageCover!.sendSubviewToBack(self.lobbyImgCopy)

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

            self.lobbyImgCopy.snp_remakeConstraints {
                (make) -> Void in
                make.top.equalTo(self)
                make.left.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.height.equalTo(UIConstants.lobbyImageHeight * 1.1)

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


    func addTable(delegate: UITableViewDelegate) {
        table = UITableView();
        table?.backgroundColor = UIColor.clearColor()
        table?.delegate = delegate;
        table?.dataSource = delegate as! UITableViewDataSource
        table?.registerClass(MessageCell.self, forCellReuseIdentifier: "message")
        insertTable();
        setUpTableConstraints();
        newMessage.delegate = delegate as! UITextFieldDelegate;
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
             var scale = CGAffineTransformMakeScale(amount*1.05, amount*1.05)
             //  self.lobbyImg.layer.anchorPoint = CGPointMake(self.lobbyImg.bounds.size.width / 2.0, self.lobbyImg.bounds.size.height / 2.0)
             self.lobbyImg.transform = scale;
         }
     }

    func scrollImage(amount: CGFloat) {
        if (amount >= 1) {
            var trans = CGAffineTransformMakeTranslation(0, -amount / 2)
            //  self.lobbyImg.layer.anchorPoint = CGPointMake(self.lobbyImg.bounds.size.width / 2.0, self.lobbyImg.bounds.size.height / 2.0)
            self.lobbyImg.transform = trans;
        }
    }

    func shortenView(notification: NSNotification) {
        if (newMessage.isFirstResponder()) {
            UIView.animateWithDuration(0.5) {
                var keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.CGRectValue().height


                var trans = CGAffineTransformMakeTranslation(0, -1 * (keyboardSize!));
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
                var trans2 = CGAffineTransformMakeTranslation(0,-1 * yChange/2)
                self.lobbyImgCopy.transform = trans2;
            }, completion: {
                (completed) -> Void in
                UIView.animateWithDuration(1.0, animations: {
                    self.imageCover!.removeFromSuperview();

                });
                println("remove cover image")
            })

        }
    }

    func scrollToBottom(duration: NSTimeInterval) {
        println("scroll to bottom")

            if (table!.contentSize.height - table!.frame.height > 0) {
                UIView.animateWithDuration(duration, animations: {
                    table?.contentOffset = CGPointMake(0, (table!.contentSize.height - table!.frame.height));
                })
            }
            moveCoverImage()

    }



    func insertViews() {
       addSubview(lobbyImgBack)
       addSubview(lobbyImg)
       addSubview(whiteBottom)
        bringSubviewToFront(table!)
        addSubview(newMessage)
        addSubview(send)
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


    func setUpDelegates(parentController: UIViewController) {
        joinLobbyButton.addTarget(parentController as! SingleLobbyController, action: "joinLobby", forControlEvents: .TouchUpInside)
    }

    func setUpViews(data: singleLobby) {

        isMember = data.data.isMember



        var url = NSURL(string: data.data.pictureUrl.getPUPUrl())
        println(data.data.pictureUrl)



        self.lobbyImg.frame.size = CGSizeMake(460, 500); //real image
        self.lobbyImgCopy.frame.size = CGSizeMake(460, 500); //fake cover iamge

        self.lobbyImgBack.contentMode = UIViewContentMode.ScaleAspectFill; //real image
        self.lobbyImgBack.clipsToBounds = true;
        self.lobbyImgBack.image = getImageWithColor(UIColor(rgba: colorFromSystem(data.data.platform)), CGSizeMake(460,500))


        self.lobbyImg.frame.size = CGSizeMake(460, 500);

        self.lobbyImg.contentMode = UIViewContentMode.ScaleAspectFill;
        self.lobbyImg.clipsToBounds = true;
        self.lobbyImg.alpha = 0;


        self.lobbyImgCopy.frame.size = CGSizeMake(460, 500);

        self.lobbyImgCopy.contentMode = UIViewContentMode.ScaleAspectFill;
        self.lobbyImgCopy.clipsToBounds = true;
        self.lobbyImgCopy.alpha = 0.4;


        self.lobbyImg.hnk_setImageFromURL(url!, placeholder:nil, format: nil, failure: nil, success: {
                (image) -> Void in
                println("fading image");
                self.lobbyImg.image = image;
                UIView.animateWithDuration(0.6, animations: {
                    () -> Void in
                    self.lobbyImg.alpha = 1;
                    self.lobbyImgBack.alpha = 0;
                });

            })

        self.lobbyImgCopy.hnk_setImageFromURL(url!, placeholder:nil, format: nil, failure: nil, success: { //fake cover image
                (image) -> Void in
                println("fading image");
                self.lobbyImgCopy.image = image;
                UIView.animateWithDuration(0.3, animations: {
                    () -> Void in
                    self.lobbyImgCopy.alpha = 1;
                });

            })







        if (!isMember) {
            joinLobbyButton.setTitle("Join Lobby", forState: .Normal)
            joinLobbyButton.setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
            joinLobbyButton.backgroundColor = UIColor.whiteColor()
        }

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

    func clearText() {
        println("clear text 2")
        self.newMessage.text = "";
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
            make.height.equalTo(UIConstants.lobbyImageHeight * 1.1)

        }
        whiteBottom.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(lobbyImg.snp_bottom).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.height.equalTo(UIConstants.lobbyImageHeight * 1.3)

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