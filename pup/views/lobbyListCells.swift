//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore
import Haneke

class gameCell: UITableViewCell {




    var imageAndPlatform: UIView = UIView()
    var textRightTitleAndDesc: UIView = UIView()
    var textRightTagsAndDate: UIView = UIView()

    var title:UILabel = UILabel()
    var desc: SelectableTextView = SelectableTextView()
    var imgView: UIImageView = UIImageView()
    var backImageView: UIImageView = UIImageView()
    var img: UIImage = UIImage()
    var tags: UILabel = UILabel()
    var time: UILabel = UILabel()
    var platform: UILabel = UILabel()
    var divider = UIView()
    var isNew = true;

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



    func setCell(item: LobbyData) {


        imageAndPlatform.addSubview(backImageView)
        imageAndPlatform.addSubview(imgView)
        imageAndPlatform.addSubview(platform)

        self.contentView.addSubview(imageAndPlatform)

        textRightTitleAndDesc.addSubview(title)
        textRightTitleAndDesc.addSubview(desc)

        self.contentView.addSubview(textRightTitleAndDesc)

        textRightTagsAndDate.addSubview(tags)
        textRightTagsAndDate.addSubview(time)
        textRightTagsAndDate.addSubview(divider)

        self.contentView.addSubview(textRightTagsAndDate)

        //setUpConstraints();


    }


    func moveLeft(speed: Double, success: (() -> Void)?) {
        UIView.animateWithDuration(speed, animations: {
            var trans = CGAffineTransformMakeTranslation(-UIScreen.mainScreen().bounds.width, 0.0);
            self.contentView.transform = trans;
        }, completion: {
            finished in
            success?();

        });
    }


    func removeOffset() {
        var trans = CGAffineTransformMakeTranslation(0.0, 0.0);
        self.contentView.transform = trans;
    }

    func setUpConstraints() {

        isNew = false;
        var bottomSectionHeight = 26;
        var topSectionHeight = 93;

        var viewsDict = Dictionary <String, UIView>()
        viewsDict["imageAndPlatform"] = imageAndPlatform
        viewsDict["imgView"] = imgView
        viewsDict["platform"] = platform

        self.contentView.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(self).offset(UIConstants.halfHorizontalPadding)
            make.right.equalTo(self).offset(-UIConstants.halfHorizontalPadding)
            make.top.equalTo(self).offset(UIConstants.halfHorizontalPadding / 2)
            make.bottom.equalTo(self).offset(-UIConstants.halfHorizontalPadding / 2)
        }


        imageAndPlatform.snp_remakeConstraints { (make) -> Void in
            make.width.equalTo(topSectionHeight)
            make.top.equalTo(self.contentView).offset(0)
            make.left.equalTo(self.contentView).offset(0)
            make.bottom.equalTo(self.contentView).offset(0)


        }


        textRightTitleAndDesc.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(imageAndPlatform.snp_right).offset(0);
            make.right.equalTo(self.contentView).offset(0)
            make.top.equalTo(self.textRightTagsAndDate.snp_bottom).offset(0)
            make.bottom.equalTo(self.contentView).offset(0)
        }
        textRightTagsAndDate.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(imageAndPlatform.snp_right).offset(0);
            make.right.equalTo(self.contentView).offset(0)
            make.height.equalTo(bottomSectionHeight)
            make.top.equalTo(self.contentView).offset(0)
        }

        backImageView.snp_remakeConstraints { (make) -> Void in
            make.right.equalTo(imageAndPlatform).offset(0)
            make.bottom.equalTo(imageAndPlatform).offset(0)
            make.left.equalTo(imageAndPlatform).offset(0)
            make.height.equalTo(topSectionHeight)


        }
        imgView.snp_remakeConstraints { (make) -> Void in
            make.right.equalTo(imageAndPlatform).offset(0)
            make.bottom.equalTo(imageAndPlatform).offset(0)
            make.left.equalTo(imageAndPlatform).offset(0)
            make.height.equalTo(topSectionHeight)


        }

        platform.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(imageAndPlatform).offset(0)
            make.right.equalTo(imageAndPlatform).offset(0)
            make.left.equalTo(imageAndPlatform).offset(0)
            make.height.equalTo(bottomSectionHeight)
            //make.bottom.equalTo(imageAndPlatform).offset(0)
        }

        title.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(textRightTitleAndDesc).offset(UIConstants.halfHorizontalPadding)
            make.right.equalTo(textRightTitleAndDesc).offset(-UIConstants.halfHorizontalPadding)
            make.top.equalTo(textRightTitleAndDesc).offset(UIConstants.halfHorizontalPadding)
            make.height.equalTo(18)

        }
        desc.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(textRightTitleAndDesc).offset(UIConstants.halfHorizontalPadding)
            make.right.equalTo(textRightTitleAndDesc).offset(-UIConstants.halfHorizontalPadding)
            make.top.equalTo(title.snp_bottom).offset(UIConstants.halfHorizontalPadding - 3)
            make.bottom.equalTo(textRightTitleAndDesc).offset(-UIConstants.verticalPadding)

        }

        tags.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(textRightTagsAndDate).offset(UIConstants.halfHorizontalPadding);
            make.top.equalTo(textRightTagsAndDate).offset(0)
            make.bottom.equalTo(textRightTagsAndDate).offset(0)
            make.width.greaterThanOrEqualTo(100)
        }

        time.snp_remakeConstraints { (make) -> Void in
            make.right.equalTo(textRightTagsAndDate).offset(-UIConstants.halfHorizontalPadding)
            make.top.equalTo(textRightTagsAndDate).offset(0)
            make.bottom.equalTo(textRightTagsAndDate).offset(0)
        }

        divider.snp_remakeConstraints { (make) -> Void in
            make.bottom.equalTo(textRightTagsAndDate).offset(0)
            make.left.equalTo(textRightTagsAndDate).offset(0)
            make.right.equalTo(textRightTagsAndDate).offset(0)
            make.height.equalTo(UIConstants.dividerWidth)
        }


        self.setNeedsDisplay()


    }





    override func setHighlighted(highlighted: Bool, animated: Bool) {
        if (!highlighted) {
            self.contentView.backgroundColor = UIColor.whiteColor()
        } else {
            self.contentView.backgroundColor = UIColor(white: 0.0, alpha: 0.5)
        }


    }

    func setUpViews(data: LobbyData) {

        self.contentView.clipsToBounds = true;
        self.contentView.layer.masksToBounds = true;
        self.contentView.layer.shadowRadius = 0;
        self.contentView.layer.shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor
        self.contentView.layer.shadowOpacity = 1;
        self.contentView.layer.shadowOffset = CGSizeMake(1.5, 1.5);
        
        
//        self.contentView.layer.borderWidth = 0.5;
//        self.contentView.layer.borderColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor;


        self.backgroundColor = UIColor(rgba: colors.lightGray);

        selectionStyle = UITableViewCellSelectionStyle.None

        var url = NSURL(string: data.pictureUrl.getPUPUrl())

        backImageView.clipsToBounds = true;
        backImageView.contentMode = UIViewContentMode.ScaleAspectFill;
        backImageView.frame.size = CGSizeMake(93, 93);
        backImageView.image = getImageWithColor(UIColor(rgba: colorFromSystem(data.platform)), CGSizeMake(93,93))

        imgView.clipsToBounds = true;
        imgView.contentMode = UIViewContentMode.ScaleAspectFill;
        imgView.frame.size = CGSizeMake(93, 93);
        imgView.backgroundColor = UIColor(rgba: colors.orange)
        imgView.alpha = 0;
        self.imgView.hnk_setImageFromURL(url!, placeholder:nil, format: nil, failure: nil, success: {
            (image) -> Void in
            self.imgView.image = image;
            UIView.animateWithDuration(0.3, animations: {
                () -> Void in
                self.imgView.alpha = 1;
            });

        })


        platform.text = data.platform.replacePCWithSteam();
        platform.textAlignment = NSTextAlignment.Center
        platform.font = UIFont(name: "AvenirNext-Medium", size: 9.0)
        platform.textColor = UIColor.whiteColor();
        platform.backgroundColor = UIColor(rgba: colorFromSystem(data.platform))


        title.text = data.name
        title.font = UIFont(name: "AvenirNext-Regular", size: 16.0)
        title.layoutMargins = UIEdgeInsetsZero


        desc.text = data.description.shorten(138)
        self.desc.font = UIFont(name: "AvenirNext-Regular", size: 11.0)
        desc.editable = false
        desc.userInteractionEnabled = false
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.backgroundColor = UIColor.clearColor()

        tags.text = data.getTagText
        tags.font = UIFont(name: "AvenirNext-Medium", size: 9.0)
        time.text = data.timeInHuman
        time.font = UIFont(name: "AvenirNext-Medium", size: 9.0)

        time.textColor = UIColor(rgba: colors.tealMain)


        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.35)

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


class headerCell: UITableViewCell {

    var title:UILabel = UILabel()


    var data = LobbyData()

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {

        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }



    func setCell(item: LobbyData) {


        self.selectionStyle = UITableViewCellSelectionStyle.None
        setUpViews(item)




        self.contentView.addSubview(title)


        setUpConstraints();

        self.backgroundColor = UIColor(rgba: colors.lightGray)
        self.contentView.backgroundColor = UIColor(rgba: colors.lightGray)
        self.title.backgroundColor = UIColor(rgba: colors.lightGray);


    }




    func setUpConstraints() {

        var viewsDict = Dictionary <String, UIView>()
        viewsDict["title"] = title
        title.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView).offset(0)
            make.left.equalTo(self.contentView).offset(UIConstants.halfHorizontalPadding)
            make.bottom.equalTo(self.contentView).offset(0)
            make.right.equalTo(self.contentView).offset(0)


        }


    }



    func setUpViews(data: LobbyData) {
        title.text = data.breakdownTitle
        title.font = title.font.fontWithSize(10)

    }


    override var layoutMargins: UIEdgeInsets {
        get { return UIEdgeInsetsZero }
        set(newVal) {}
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}


