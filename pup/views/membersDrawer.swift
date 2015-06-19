//
// Created by Alex Hartwell on 6/18/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import Haneke


class MembersDrawerView: UIView {

    var transX = UIScreen.mainScreen().bounds.width * 0.9
    var transMax = UIScreen.mainScreen().bounds.width * 0.9
    var transMin = 0;
    var headerBackground: UIView = UIView();
    var header: UILabel = UILabel();

    var membersList: UICollectionView?

    var shadow: UIView = UIView();

    override init(frame: CGRect) {
        super.init(frame: frame)

    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setUpView(parentController: UIViewController) {


        var panRecognizer = UIPanGestureRecognizer(target: self, action:"detectPan:")
        self.addGestureRecognizer(panRecognizer)

        let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsetsZero;
        layout.itemSize = CGSize(width: UIScreen.mainScreen().bounds.width * 0.9, height: 45);
        layout.minimumInteritemSpacing = 1.0;
        layout.minimumLineSpacing = 1.0;
        membersList = UICollectionView(frame: self.frame, collectionViewLayout: layout)
        membersList!.dataSource = parentController as? UICollectionViewDataSource;
        membersList!.delegate = parentController as? UICollectionViewDelegate
        membersList!.registerClass(MembersListCell.self, forCellWithReuseIdentifier: "MemberCell")
        membersList!.backgroundColor = UIColor(rgba: colors.lightGray)

        headerBackground.backgroundColor = UIColor(rgba: colors.lightGray);

        header.text = "MEMBERS"
        header.font = header.font.fontWithSize(12)
        header.textAlignment = NSTextAlignment.Left

        shadow.backgroundColor = UIColor.clearColor();
        shadow.userInteractionEnabled = false;

        addViews();
        addConstraints();

        var trans = CGAffineTransformMakeTranslation(UIScreen.mainScreen().bounds.width * 0.9, 0);
        self.transform = trans;
    }


    func animateDrawer(alpha: CGFloat, userInteractionEnabled: Bool, transX: CGFloat) {
        self.transX = transX;

        UIView.animateWithDuration(0.4, animations: { () -> Void in
            self.shadow.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: alpha);
        })

        shadow.userInteractionEnabled = userInteractionEnabled;


        UIView.animateWithDuration(0.4, animations: { () -> Void in
            var trans = CGAffineTransformMakeTranslation(transX, 0);
            self.transform = trans;
        })
    }


    func detectPan(recognizer:UIPanGestureRecognizer) {
        var translation  = recognizer.translationInView(self.superview!)
        if (translation.x < -15) {
            animateDrawer(0.6, userInteractionEnabled: true, transX: CGFloat(transMin))

        } else if (translation.x > 15) {
            animateDrawer(0.0, userInteractionEnabled: false, transX: CGFloat(transMax))

        }





    }


    func removeViews() {
        self.shadow.removeFromSuperview()
        self.removeFromSuperview();
    }

    func addViews() {
        UIApplication.sharedApplication().windows.first!.addSubview(shadow)
        UIApplication.sharedApplication().windows.first!.addSubview(self)
        self.addSubview(headerBackground)
        self.addSubview(header)
        self.addSubview(membersList!)

    }


    func addConstraints() {

        self.snp_makeConstraints{
            (make) -> Void in
            make.top.equalTo(self.superview!).offset(0)
            make.left.equalTo(self.superview!).offset(0)
            make.bottom.equalTo(self.superview!).offset(0)
            make.right.equalTo(self.superview!).offset(0)
        }
        self.shadow.snp_makeConstraints{
            (make) -> Void in
            make.top.equalTo(self.superview!).offset(0)
            make.left.equalTo(self.superview!).offset(0)
            make.bottom.equalTo(self.superview!).offset(0)
            make.right.equalTo(self.superview!).offset(0)
        }


        //get a copy of the navigation controller from side menu, which is  a UINavigationController, not UIViewController
        var navController = UIApplication.sharedApplication().keyWindow!.rootViewController as! UINavigationController

        membersList!.snp_makeConstraints{
            (make) -> Void in
            make.top.equalTo(self).offset(navController.navigationBar.frame.height + UIApplication.sharedApplication().statusBarFrame.size.height)
            make.left.equalTo(self).offset(UIScreen.mainScreen().bounds.width * 0.1)
            make.bottom.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
        }

        headerBackground.snp_makeConstraints {
            (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(UIScreen.mainScreen().bounds.width * 0.1)
            make.bottom.equalTo(membersList!.snp_top).offset(0)
            make.right.equalTo(self).offset(0)
        }
        header.snp_makeConstraints {
            (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(Double(UIScreen.mainScreen().bounds.width * 0.1) + UIConstants.horizontalPadding)
            make.bottom.equalTo(membersList!.snp_top).offset(0)
            make.right.equalTo(self).offset(-UIConstants.horizontalPadding)
        }

    }


}



class MembersListCell: UICollectionViewCell {

    var memberName: UILabel = UILabel();
    var memberPhoto: UIImageView = UIImageView();

    var hasBeenSetUp = false;

    override init(frame: CGRect) {
        super.init(frame: frame)


        contentView.addSubview(memberName)
        contentView.addSubview(memberPhoto)

    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }



    func setUpCell(data: SingleLobbyUser) {

        self.backgroundColor = UIColor(rgba: "#f1f1f1")

        self.contentView.layer.borderColor = UIColor(rgba: colors.lightGray).CGColor
        self.contentView.layer.borderWidth = 4.0;

        memberName.text = data.name;
        memberName.font = memberName.font.fontWithSize(13)
        memberName.textColor = UIColor(rgba: colors.mainGrey)


        var proPicUrl = data.portraitUrl.stringByReplacingOccurrencesOfString("~", withString: urls.siteBase, options: NSStringCompareOptions.LiteralSearch, range: nil);
        if (proPicUrl == "") { //has no profile picture
            var img = UIImage(named: "iconWithText");
            self.memberPhoto.image = img;
        } else { //has a profile picture
            var url = NSURL(string: proPicUrl)
            self.memberPhoto.clipsToBounds = true;
            self.memberPhoto.contentMode = UIViewContentMode.ScaleAspectFill;
            memberPhoto.frame.size = CGSizeMake(42, 42);
            memberPhoto.layer.cornerRadius = 17.0;
            self.memberPhoto.hnk_setImageFromURL(url!)
        }





        memberPhoto.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self.contentView).offset(UIConstants.horizontalPadding)
            make.centerY.equalTo(self.contentView.snp_centerY)
            make.height.equalTo(34)
            make.width.equalTo(34)
        }

        if (hasBeenSetUp == false) {

            memberName.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.memberPhoto.snp_right).offset(UIConstants.horizontalPadding);
                make.centerY.equalTo(self.contentView.snp_centerY)
                make.height.equalTo(14);
                make.right.equalTo(self.contentView).offset(-UIConstants.horizontalPadding*6);

            }




            hasBeenSetUp = true;
        }

    }

}