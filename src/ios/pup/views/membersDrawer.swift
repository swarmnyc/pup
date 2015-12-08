//
// Created by Alex Hartwell on 6/18/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class MembersDrawerView: UIView {

    var transX = UIScreen.mainScreen().bounds.width * 0.98
    var transMax = UIScreen.mainScreen().bounds.width * 0.98
    var transMin = 0;
    var headerBackground: UIView = UIView();
    var header: UILabel = UILabel();

    var membersList: UICollectionView?
    var touchToClose = UIView();
    var shadow: UIView = UIView();

    var open = false;

    override init(frame: CGRect) {
        super.init(frame: frame)

    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setUpView(parentController: UIViewController, navBarController: UINavigationController) {




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

        headerBackground.backgroundColor = UIColor(rgba: colors.orange);

        header.text = "MEMBERS"
        header.font = UIConstants.titleFont;
        header.textColor = UIColor.whiteColor();
        header.textAlignment = NSTextAlignment.Left

        shadow.backgroundColor = UIColor.clearColor();
        shadow.userInteractionEnabled = false;

        addViews();
        createConstraints(navBarController);

        var trans = CGAffineTransformMakeTranslation(transX, 0);
        self.transform = trans;


        var panRecognizer = UIPanGestureRecognizer(target: self, action:"detectPan:")
        self.addGestureRecognizer(panRecognizer)

        var tapGesture = UITapGestureRecognizer(target: self, action: "shadowClicked");
        tapGesture.numberOfTapsRequired = 1;
        tapGesture.numberOfTouchesRequired = 1;
        self.touchToClose.addGestureRecognizer(tapGesture);
    }


    func animateDrawer(alpha: CGFloat, userInteractionEnabled: Bool, transX: CGFloat) {
        self.transX = transX;

        UIView.animateWithDuration(0.3, animations: { () -> Void in
            self.shadow.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: alpha);
        })

        self.shadow.userInteractionEnabled = userInteractionEnabled;
//        self.touchToClose.userInteractionEnabled = userInteractionEnabled;


        UIView.animateWithDuration(0.3, animations: { () -> Void in
            var trans = CGAffineTransformMakeTranslation(transX, 0);
            self.transform = trans;
        })
    }

    func toggle() {
        if (self.open) {
            animateDrawer(0.0, userInteractionEnabled: false, transX: CGFloat(transMax))
            self.open = false;
        } else {
            animateDrawer(0.6, userInteractionEnabled: true, transX: CGFloat(transMin))
            self.open = true;
        }
    }

    func shadowClicked() {
        println("shadowClicked");
        animateDrawer(0.0, userInteractionEnabled: false, transX: CGFloat(transMax))
        self.open = false;
    }

    func detectPan(recognizer:UIPanGestureRecognizer) {
        var translation  = recognizer.translationInView(self.superview!)
        if (translation.x < -15) {
            self.open = true;
            animateDrawer(0.6, userInteractionEnabled: true, transX: CGFloat(transMin))

        } else if (translation.x > 15) {
            self.open = false;
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
        self.addSubview(touchToClose)
        self.addSubview(headerBackground)
        self.addSubview(header)
        self.addSubview(membersList!)

    }


    func createConstraints(navBar: UINavigationController) {

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
        var navController = navBar

        membersList!.snp_makeConstraints{
            (make) -> Void in
            make.top.equalTo(self).offset(navController.navigationBar.frame.height + UIApplication.sharedApplication().statusBarFrame.size.height)
            make.left.equalTo(self).offset(UIScreen.mainScreen().bounds.width * 0.1)
            make.bottom.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
        }

        touchToClose.snp_makeConstraints{
            (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
            make.width.equalTo(self).offset(0)
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
            make.centerY.equalTo(self.headerBackground.snp_centerY).offset(10)
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
        memberName.font = UIConstants.titleFont;
        memberName.textColor = UIColor(rgba: colors.mainGrey)


        var proPicUrl = data.portraitUrl.getPUPUrl();
        if (proPicUrl == "") { //has no profile picture
            var img = UIImage(named: "iconWithText");
            self.memberPhoto.image = img;
        } else { //has a profile picture
            var url = NSURL(string: proPicUrl)
            self.memberPhoto.clipsToBounds = true;
            self.memberPhoto.contentMode = UIViewContentMode.ScaleAspectFill;
            memberPhoto.frame.size = CGSizeMake(34, 34);
            memberPhoto.layer.cornerRadius = 17.0;
//            self.memberPhoto.hnk_setImageFromURL(url!)
            if (url != nil) {
            self.memberPhoto.sd_setImageWithURL(url!, placeholderImage: nil, options: SDWebImageOptions.RefreshCached);
            } else {
                var img = UIImage(named: "iconWithText");
                self.memberPhoto.image = img;
            }
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