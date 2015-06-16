//
// Created by Alex Hartwell on 6/15/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class MessageCell: UITableViewCell {


    var profilePicture: UIImageView = UIImageView();
    var userName: UILabel = UILabel();
    var message:UITextView = UITextView();

    var isSystemMessage = false;

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


    func setUpCell(item: Message) {
        var image = UIImage(named: "iconWithText");
        self.profilePicture.clipsToBounds = true;
        self.profilePicture.image = image;
        self.profilePicture.layer.cornerRadius = 20;
        if (item.picture != "") {
            var url = NSURL(string: item.picture)
            self.profilePicture.frame = CGRectMake(0,0,50,50);

            self.profilePicture.hnk_setImageFromURL(url!)
            println("HAS URL!!!!!!!!")
        }

        if (item.username == "system message") {
            isSystemMessage = true;
        } else {
            userName.text = item.username;
            userName.font = userName.font.fontWithSize(12)
            userName.backgroundColor = UIColor.clearColor();
            userName.textColor = UIColor(rgba: colors.mainGrey);
            if (item.username.removeWhitespace()==currentUser.data.name.removeWhitespace()) {
                self.backgroundColor = UIColor(rgba: colors.lightGray)
            }
        }

        message.text = item.message;
        message.font = message.font.fontWithSize(10);
        message.userInteractionEnabled = false;
        message.textContainerInset = UIEdgeInsetsZero
        message.textContainer.lineFragmentPadding = 0;
        message.backgroundColor = UIColor.clearColor();
        message.textColor = UIColor(rgba: colors.midGray).darkerColor(0.3)


        addViews();
        addConstraints();

    }

    func addViews() {
        addSubview(profilePicture)
        if (isSystemMessage == false) {
            addSubview(userName)
        }
        addSubview(message)

    }

    func addConstraints() {
        profilePicture.snp_makeConstraints {
            (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.horizontalPadding * 2)
            make.centerY.equalTo(self.snp_centerY)
            make.height.equalTo(40)
            make.width.equalTo(40);

        }
        if (isSystemMessage == false) {
            userName.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicture.snp_right).offset(UIConstants.horizontalPadding * 2)
                make.top.equalTo(self).offset(UIConstants.halfVerticalPadding)
                make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 2);
                make.height.equalTo(20)
            }

            message.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicture.snp_right).offset(UIConstants.horizontalPadding * 2)
                make.bottom.equalTo(self).offset(-UIConstants.halfVerticalPadding * 2)
                make.right.equalTo(self).offset(-UIConstants.horizontalPadding);
                make.top.equalTo(self.userName.snp_bottom).offset(-UIConstants.halfVerticalPadding * 0.5)
            }
        } else {
            message.snp_remakeConstraints {
                (make) -> Void in
                make.left.equalTo(self.profilePicture.snp_right).offset(UIConstants.horizontalPadding * 2.0)
                make.top.equalTo(self).offset(UIConstants.verticalPadding)
                make.right.equalTo(self).offset(-UIConstants.horizontalPadding * 2.0);
                make.bottom.equalTo(self).offset(-UIConstants.verticalPadding)
            }
        }
    }


    override var layoutMargins: UIEdgeInsets {  //make cell dividers extend full width
        get { return UIEdgeInsetsZero }
        set(newVal) {}
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }


}