//
// Created by Alex Hartwell on 6/17/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class animatedFlowLayout: UICollectionViewFlowLayout {

    override func finalLayoutAttributesForDisappearingItemAtIndexPath(itemIndexPath: NSIndexPath!) -> UICollectionViewLayoutAttributes! {
        var attr = UICollectionViewLayoutAttributes(forCellWithIndexPath: itemIndexPath);

        attr.transform = CGAffineTransformMakeScale(1.0, 0.0);

        return attr;

    }



}

class MyChatsView: UIView {
        var chatsCollection: UICollectionView?;

    override init(frame: CGRect) {
        super.init(frame: frame)

    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setUpView(parent: UIViewController) {

        self.backgroundColor = UIColor.blackColor()

        let layout = animatedFlowLayout()
        layout.sectionInset = UIEdgeInsetsZero;
        layout.itemSize = CGSize(width: UIScreen.mainScreen().bounds.width, height: 90);
        layout.minimumInteritemSpacing = 1.0;
        layout.minimumLineSpacing = 1.0;
        chatsCollection = UICollectionView(frame: self.frame, collectionViewLayout: layout)
        chatsCollection!.dataSource = parent as? UICollectionViewDataSource;
        chatsCollection!.delegate = parent as? UICollectionViewDelegate
        chatsCollection!.registerClass(MyChatsCell.self, forCellWithReuseIdentifier: "myChatCell")
        chatsCollection!.registerClass(EmptyCell.self, forCellWithReuseIdentifier: "emptyCell")
        chatsCollection!.backgroundColor = UIColor(rgba: colors.lightGray)
        chatsCollection!.alwaysBounceVertical = true;

        addViews();
        setUpConstraints();

    }

    func addViews() {
        self.addSubview(chatsCollection!)
    }

    func setUpConstraints() {
        chatsCollection?.snp_makeConstraints {
            (make) -> Void in
            make.top.equalTo(self).offset(0)
            make.left.equalTo(self).offset(0)
            make.right.equalTo(self).offset(0)
            make.bottom.equalTo(self).offset(0)
        }
    }

}



class EmptyCell: UICollectionViewCell {

    var label = UITextView();

    override init(frame: CGRect) {
        super.init(frame: frame)

        contentView.addSubview(label)
        label.text = "\n \nYou haven't joined any games\nyet, and all the cake is gone.";
        label.font = UIFont(name: "AvenirNext-Regular", size: 16.0);

        label.textAlignment = NSTextAlignment.Center
        label.editable = false;
        label.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self)
            make.top.equalTo(self)
            make.right.equalTo(self)
            make.height.equalTo(UIScreen.mainScreen().bounds.height - 90)
        }

        self.clipsToBounds = false;
        self.layer.masksToBounds = false;

    }

    func updateSize() {
        frame.size = CGSize(width: UIScreen.mainScreen().bounds.width, height: UIScreen.mainScreen().bounds.height - 80)
    }

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


}


class MyChatsCell: UICollectionViewCell, UIGestureRecognizerDelegate {

    var lobbyName: UILabel = UILabel();
    var lobbyImage: UIImageView = UIImageView();
    var time: UILabel = UILabel();
    var desc: UILabel = UILabel();
    var platform: UILabel = UILabel();
    var divider: UIView = UIView();
    var trashView: UIButton = UIButton();

    var hasBeenSetUp = false;
    var data: LobbyData?
    var panRecognize: UIPanGestureRecognizer?;
    var tapRecognize: UITapGestureRecognizer?
    var movedLeft = false;
    var transX: CGFloat = 0.0;
    var startValue: CGFloat = 0.0;
    var removeCellAction: ((String?, String?) -> Void)?
    var contentContainer: UIView = UIView();

    override init(frame: CGRect) {
        super.init(frame: frame)
        panRecognize = UIPanGestureRecognizer(target: self, action: "detectedPan:");
        panRecognize!.delegate = self;


        self.contentView.addGestureRecognizer(panRecognize!);
        self.contentContainer.addSubview(lobbyName)


        self.contentContainer.addSubview(lobbyImage)
        self.contentContainer.addSubview(time)
        self.contentContainer.addSubview(desc)
        self.contentContainer.addSubview(platform)
        self.contentContainer.addSubview(divider)
        self.contentView.addSubview(trashView)
        self.contentView.addSubview(self.contentContainer)

    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }




    func detectedPan(gesture: UIGestureRecognizer) {
        var pan = gesture as? UIPanGestureRecognizer
        if (pan != nil) {
            var translation = pan!.translationInView(self.superview!);
            println(translation.x);

            if (translation.x < -1 && movedLeft == false) {
                    if (gesture.state == UIGestureRecognizerState.Ended) { // has ended gesture
                        if (translation.x < -140) { //has swiped far enough to remove chat
                            movedLeft = true;
                            self.startValue = -UIScreen.mainScreen().bounds.width / 2;
                            UIView.animateWithDuration(0.7, animations: {
                                var trans = CGAffineTransformMakeTranslation(self.startValue, 0.0);
                                self.contentContainer.transform = trans;
                                self.trashView.alpha = 1;

                            })

                        } else {  //didn't quite make it far enough, lets go back to normal
                            self.startValue = 0;
                            UIView.animateWithDuration(0.7, animations: {
                                var trans = CGAffineTransformMakeTranslation(self.startValue, 0.0);
                                self.contentContainer.transform = trans;
                            })
                        }
                    } else { // has not ended, in the middle of gesture

                        transX = startValue + translation.x;
                        if (transX >= -UIScreen.mainScreen().bounds.width / 2) { // hasn't gone all the way yet
                            var trans = CGAffineTransformMakeTranslation(transX, 0.0);
                            self.contentContainer.transform = trans;

                            self.trashView.alpha = transX / (-UIScreen.mainScreen().bounds.width / 2.0);
                        }
                    }

            } else if (translation.x > 1 && movedLeft) {

                if (gesture.state == UIGestureRecognizerState.Ended) {
                    if (translation.x > 30) {
                        movedLeft = false;
                        self.startValue = 0;
                        UIView.animateWithDuration(0.3, animations: {
                            var trans = CGAffineTransformMakeTranslation(self.startValue, 0.0);
                            self.contentContainer.transform = trans;
                            self.trashView.alpha = 0;
                        })
                    } else {
                        self.startValue = -UIScreen.mainScreen().bounds.width / 3
                        UIView.animateWithDuration(0.3, animations: {
                            var trans = CGAffineTransformMakeTranslation(self.startValue, 0.0);
                            self.contentContainer.transform = trans;
                        })
                    }

                } else { // has not ended in the middle of gesture
                    transX = startValue + translation.x;
                    var trans = CGAffineTransformMakeTranslation(transX, 0.0);
                    self.contentContainer.transform = trans;
                }


            }


        }
    }

    func moveLeft(speed: Double, success: (() -> Void)?) {
        println("mooooovvviiinng");
        println(speed);
        UIView.animateWithDuration(speed, animations: {
            var trans = CGAffineTransformMakeTranslation(-UIScreen.mainScreen().bounds.width, 0.0);
            self.transform = trans;
        }, completion: {
            finished in
            success?();

        });
    }

    func moveRight(speed: Double, success: (() -> Void)?) {
        println("mooooovvviiinng RIiiiiight");
        println(speed);
        UIView.animateWithDuration(speed, animations: {
            var trans = CGAffineTransformMakeTranslation(0.0, 0.0);
            self.transform = trans;
        }, completion: {
            finished in
            success?();

        });
    }

    func close() {
        movedLeft = false;
        self.startValue = 0;
        var trans = CGAffineTransformMakeTranslation(self.startValue, 0.0);
        self.contentContainer.transform = trans;
        self.trashView.alpha = 0;

    }

    func detectedTap() {
        println("tap!");
        println("remove cell")
        UIView.animateWithDuration(0.3, animations: {
            self.close();
        })

        self.removeCellAction?(data?.id, data?.name);
    }


    override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        var pan = gestureRecognizer as? UIPanGestureRecognizer
        println(pan);
        if (pan != nil) {
            var translation = pan!.translationInView(self.superview!);
            if (abs(translation.x) > abs(translation.y)) {
                return true;
            }
        }
        return false;
    }

    func setUpCell(data: LobbyData) {
        self.movedLeft = false //let's make sure the cell is in the closed position
        self.startValue = 0;
        var trans = CGAffineTransformMakeTranslation(self.startValue, 0.0);
        self.contentContainer.transform = trans;


        self.data = data;
        movedLeft = false;
        self.data?.checkUnreadCounter = self.checkUnread;
        self.backgroundColor = UIColor(rgba: "#f1f1f1")
        if (data.timeInHuman == "Finished") {
            self.contentView.layer.opacity = 0.6
        } else {
            self.contentView.layer.opacity = 1
        }
        self.contentView.layer.borderColor = UIColor(rgba: colors.lightGray).CGColor
        self.contentView.layer.borderWidth = 4.0;

        self.contentContainer.backgroundColor = UIColor(red: 241, green: 241, blue: 241, alpha: 255);

        self.trashView.titleLabel!.font = UIConstants.titleFont;
        self.trashView.backgroundColor = UIColor(rgba: colors.orange);
        self.trashView.setTitleColor(UIColor.whiteColor(), forState: .Normal);
        self.trashView.setTitle("Tap To Leave Game", forState: .Normal);
        self.trashView.alpha = 0;
        self.trashView.addTarget(self, action: "detectedTap", forControlEvents: UIControlEvents.TouchUpInside)

        lobbyName.text = data.name;
        lobbyName.font = UIConstants.titleFont;
        lobbyName.textColor = UIColor(rgba: colors.mainGrey)

        var url = NSURL(string: data.thumbnailPictureUrl.getPUPUrl())
        self.lobbyImage.clipsToBounds = true;
        self.lobbyImage.contentMode = UIViewContentMode.ScaleAspectFill;
        lobbyImage.frame.size = CGSizeMake(42, 42);
        self.lobbyImage.backgroundColor = UIColor(rgba: colors.orange)
//        self.lobbyImage.hnk_setImageFromURL(url!)
        self.lobbyImage.sd_setImageWithURL(url!, placeholderImage: nil, options: SDWebImageOptions.RefreshCached, progress: {
            (recievedSize, exprectedSize) -> Void in

            // println(recievedSize);

        }, completed: {
            (image, error, cacheType, imageUrl) -> Void in
            self.lobbyImage.image = image;
            UIView.animateWithDuration(0.3, animations: {
                () -> Void in
                self.lobbyImage.alpha = 1;
            });
        });

        if (data.unreadMessageCount==0) {
            self.lobbyImage.layer.borderWidth = 0.0;
        } else {
            self.lobbyImage.layer.borderColor = UIColor(rgba: colors.tealMain).CGColor
            self.lobbyImage.layer.borderWidth = 3.0;
        }



        time.text = data.timeInHuman
        time.font = UIConstants.tagType;
        time.textColor = UIColor(rgba: colors.tealMain)

        desc.text = data.description;
        desc.font = UIConstants.paragraphType;
        desc.textColor = UIColor(rgba: colors.midGray)

        platform.text = data.platform
        platform.font = UIConstants.tagType;
        platform.textColor = UIColor(rgba: colors.midGray);
        platform.textAlignment = NSTextAlignment.Right

        divider.backgroundColor = UIColor(rgba: colors.mainGrey).lighterColor(0.75)


        lobbyImage.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self.contentView).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self.contentView).offset(UIConstants.verticalPadding)
            make.height.equalTo(42)
            make.width.equalTo(42)
        }

        if (hasBeenSetUp == false) {

            self.contentContainer.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self).offset(0)
                make.top.equalTo(self).offset(0)
                make.right.equalTo(self).offset(0)
                make.bottom.equalTo(self).offset(0)
            }

            lobbyName.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.lobbyImage.snp_right).offset(UIConstants.horizontalPadding);
                make.top.equalTo(self.contentContainer).offset(UIConstants.verticalPadding);
                make.height.equalTo(20);
                make.right.equalTo(self.contentContainer).offset(-UIConstants.horizontalPadding*6);

            }

            trashView.snp_makeConstraints {
                (make) -> Void in
                make.right.equalTo(self.contentContainer).offset(0);
                make.top.equalTo(self.contentContainer).offset(0);
                make.bottom.equalTo(self.contentContainer).offset(0);
                make.width.equalTo(UIScreen.mainScreen().bounds.width / 2);

            }

            time.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.contentContainer).offset(UIConstants.horizontalPadding)
                make.top.equalTo(self.lobbyImage.snp_bottom).offset(UIConstants.verticalPadding)
                make.width.equalTo(150)
                make.height.equalTo(14)
            }

            desc.snp_makeConstraints {
                (make) -> Void in
                 make.left.equalTo(self.lobbyImage.snp_right).offset(UIConstants.horizontalPadding);
                make.top.equalTo(self.lobbyName.snp_bottom).offset(UIConstants.halfHorizontalPadding / 2.0);
                make.right.equalTo(self.contentContainer).offset(-UIConstants.horizontalPadding)
                make.height.equalTo(16)
            }

            platform.snp_makeConstraints {
                (make) -> Void in
                make.right.equalTo(self.contentContainer).offset(-UIConstants.horizontalPadding)
                make.top.equalTo(self.contentContainer).offset(UIConstants.verticalPadding);
                make.height.equalTo(20);
                make.left.equalTo(lobbyName.snp_right);
            }

            divider.snp_makeConstraints {
                (make) -> Void in
                make.bottom.equalTo(self.contentContainer).offset(0)
                make.left.equalTo(self.contentContainer).offset(0)
                make.right.equalTo(self.contentContainer).offset(0)
                make.height.equalTo(4.5)
            }


            hasBeenSetUp = true;
        }

    }

    func removeBorder() {
        self.data?.unreadMessageCount = 0;
        if (data?.unreadMessageCount==0) {
            self.lobbyImage.layer.borderWidth = 0.0;
        } else {
            self.lobbyImage.layer.borderColor = UIColor(rgba: colors.tealMain).CGColor
            self.lobbyImage.layer.borderWidth = 3.0;
        }
    }

    func checkUnread(amount: Int) {
        println("checking unread")
        if (data?.unreadMessageCount==0) {
            self.lobbyImage.layer.borderWidth = 0.0;
        } else {
            self.lobbyImage.layer.borderColor = UIColor(rgba: colors.tealMain).CGColor
            self.lobbyImage.layer.borderWidth = 3.0;
        }
    }

}