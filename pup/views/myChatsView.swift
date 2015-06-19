//
// Created by Alex Hartwell on 6/17/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import Haneke

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

        let layout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsetsZero;
        layout.itemSize = CGSize(width: UIScreen.mainScreen().bounds.width, height: 90);
        layout.minimumInteritemSpacing = 1.0;
        layout.minimumLineSpacing = 1.0;
        chatsCollection = UICollectionView(frame: self.frame, collectionViewLayout: layout)
        chatsCollection!.dataSource = parent as? UICollectionViewDataSource;
        chatsCollection!.delegate = parent as? UICollectionViewDelegate
        chatsCollection!.registerClass(MyChatsCell.self, forCellWithReuseIdentifier: "myChatCell")
        chatsCollection!.backgroundColor = UIColor(rgba: colors.lightGray)

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

class MyChatsCell: UICollectionViewCell {

    var lobbyName: UILabel = UILabel();
    var lobbyImage: UIImageView = UIImageView();
    var time: UILabel = UILabel();
    var desc: UILabel = UILabel();
    var platform: UILabel = UILabel();
    var divider: UIView = UIView();

    var hasBeenSetUp = false;

    override init(frame: CGRect) {
        super.init(frame: frame)


        contentView.addSubview(lobbyName)


        contentView.addSubview(lobbyImage)
        contentView.addSubview(time)
        contentView.addSubview(desc)
        contentView.addSubview(platform)
        contentView.addSubview(divider)
    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpCell(data: LobbyData) {
        self.backgroundColor = UIColor(rgba: "#f1f1f1")

        self.contentView.layer.borderColor = UIColor(rgba: colors.lightGray).CGColor
        self.contentView.layer.borderWidth = 4.0;

        lobbyName.text = data.name;
        lobbyName.font = lobbyName.font.fontWithSize(13)
        lobbyName.textColor = UIColor(rgba: colors.mainGrey)

        var url = NSURL(string: data.thumbnailPictureUrl.stringByReplacingOccurrencesOfString("~", withString: urls.siteBase, options: NSStringCompareOptions.LiteralSearch, range: nil))
        // var request:NSURLRequest = NSURLRequest(URL: url!)
        self.lobbyImage.clipsToBounds = true;
        self.lobbyImage.contentMode = UIViewContentMode.ScaleAspectFill;
        lobbyImage.frame.size = CGSizeMake(42, 42);
        self.lobbyImage.hnk_setImageFromURL(url!)



        time.text = data.timeInHuman
        time.font = time.font.fontWithSize(11)
        time.textColor = UIColor(rgba: colors.tealMain)

        desc.text = data.description;
        desc.font = desc.font.fontWithSize(13)
        desc.textColor = UIColor(rgba: colors.midGray)

        platform.text = data.platform
        platform.font = platform.font.fontWithSize(11)
        platform.textColor = UIColor(rgba: colors.midGray);
        platform.textAlignment = NSTextAlignment.Right

        divider.backgroundColor = UIColor(rgba: colors.mainGrey)


        lobbyImage.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self.contentView).offset(UIConstants.horizontalPadding)
            make.top.equalTo(self.contentView).offset(UIConstants.verticalPadding)
            make.height.equalTo(42)
            make.width.equalTo(42)
        }

        if (hasBeenSetUp == false) {

            lobbyName.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.lobbyImage.snp_right).offset(UIConstants.horizontalPadding);
                make.top.equalTo(self.contentView).offset(UIConstants.verticalPadding);
                make.height.equalTo(20);
                make.right.equalTo(self.contentView).offset(-UIConstants.horizontalPadding*6);

            }

            time.snp_makeConstraints {
                (make) -> Void in
                make.left.equalTo(self.contentView).offset(UIConstants.horizontalPadding)
                make.top.equalTo(self.lobbyImage.snp_bottom).offset(UIConstants.verticalPadding)
                make.width.equalTo(150)
                make.height.equalTo(16)
            }

            desc.snp_makeConstraints {
                (make) -> Void in
                 make.left.equalTo(self.lobbyImage.snp_right).offset(UIConstants.horizontalPadding);
                make.top.equalTo(self.lobbyName.snp_bottom).offset(UIConstants.halfHorizontalPadding / 2.0);
                make.right.equalTo(self.contentView).offset(-UIConstants.horizontalPadding)
                make.height.equalTo(16)
            }

            platform.snp_makeConstraints {
                (make) -> Void in
                make.right.equalTo(self.contentView).offset(-UIConstants.horizontalPadding)
                make.top.equalTo(self.contentView).offset(UIConstants.verticalPadding);
                make.height.equalTo(20);
                make.left.equalTo(lobbyName.snp_right);
            }

            divider.snp_makeConstraints {
                (make) -> Void in
                make.bottom.equalTo(self.contentView).offset(0)
                make.left.equalTo(self.contentView).offset(0)
                make.right.equalTo(self.contentView).offset(0)
                make.height.equalTo(2)
            }


            hasBeenSetUp = true;
        }

    }

}