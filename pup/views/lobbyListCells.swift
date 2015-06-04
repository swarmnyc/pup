//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class gameCell: UITableViewCell {




    var imageAndPlatform: UIView = UIView()
    var textRightTop: UIView = UIView()
    var textRightBottom: UIView = UIView()

    var title:UILabel = UILabel()
    var desc: SelectableTextView = SelectableTextView()
    var imgView: UIImageView = UIImageView()
    var img: UIImage = UIImage()
    var tags: UILabel = UILabel()
    var time: UILabel = UILabel()
    var platform: UILabel = UILabel()
    var divider = UIView()


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



    func setCell(item: lobbyData) {

        setUpViews(item)

        imageAndPlatform.addSubview(imgView)
        imageAndPlatform.addSubview(platform)

        self.contentView.addSubview(imageAndPlatform)

        textRightTop.addSubview(title)
        textRightTop.addSubview(desc)

        self.contentView.addSubview(textRightTop)

        textRightBottom.addSubview(tags)
        textRightBottom.addSubview(time)
        textRightBottom.addSubview(divider)

        self.contentView.addSubview(textRightBottom)

        setUpConstraints();



    }




    func setUpConstraints() {

        var bottomSectionHeight = 26;
        var topSectionHeight = 93;

        var viewsDict = Dictionary <String, UIView>()
        viewsDict["imageAndPlatform"] = imageAndPlatform
        viewsDict["imgView"] = imgView
        viewsDict["platform"] = platform
        imageAndPlatform.snp_remakeConstraints { (make) -> Void in
            make.width.equalTo(topSectionHeight)
            make.top.equalTo(self.contentView).offset(0)
            make.left.equalTo(self.contentView).offset(0)
            make.bottom.equalTo(self.contentView).offset(0)


        }


        textRightTop.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(imageAndPlatform.snp_right).offset(0);
            make.right.equalTo(self.contentView).offset(0)
            make.top.equalTo(self.contentView).offset(0)
            make.bottom.equalTo(self.contentView).offset(26)
        }
        textRightBottom.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(imageAndPlatform.snp_right).offset(0);
            make.right.equalTo(self.contentView).offset(0)
            make.height.equalTo(bottomSectionHeight)
            make.bottom.equalTo(self.contentView).offset(0)
        }

        imgView.snp_remakeConstraints { (make) -> Void in
            make.right.equalTo(imageAndPlatform).offset(0)
            make.top.equalTo(imageAndPlatform).offset(0)
            make.left.equalTo(imageAndPlatform).offset(0)
            make.height.equalTo(topSectionHeight)


        }

        platform.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(imgView.snp_bottom).offset(0)
            make.right.equalTo(imageAndPlatform).offset(0)
            make.left.equalTo(imageAndPlatform).offset(0)
            make.height.equalTo(bottomSectionHeight)
            //make.bottom.equalTo(imageAndPlatform).offset(0)
        }

        title.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(textRightTop).offset(UIConstants.horizontalPadding)
            make.right.equalTo(textRightTop).offset(-UIConstants.horizontalPadding)
            make.top.equalTo(textRightTop).offset(UIConstants.verticalPadding)
            make.height.equalTo(18)

        }
        desc.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(textRightTop).offset(UIConstants.horizontalPadding)
            make.right.equalTo(textRightTop).offset(-UIConstants.horizontalPadding)
            make.top.equalTo(title.snp_bottom).offset(UIConstants.halfHorizontalPadding)
            make.bottom.equalTo(textRightTop).offset(-UIConstants.verticalPadding)

        }

        tags.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(textRightBottom).offset(UIConstants.horizontalPadding);
            make.top.equalTo(textRightBottom).offset(0)
            make.bottom.equalTo(textRightBottom).offset(0)
            make.width.greaterThanOrEqualTo(100)
        }

        time.snp_remakeConstraints { (make) -> Void in
            make.right.equalTo(textRightBottom).offset(-UIConstants.horizontalPadding)
            make.top.equalTo(textRightBottom).offset(0)
            make.bottom.equalTo(textRightBottom).offset(0)
        }

        divider.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(textRightBottom).offset(0)
            make.left.equalTo(textRightBottom).offset(0)
            make.right.equalTo(textRightBottom).offset(0)
            make.height.equalTo(UIConstants.dividerWidth)
        }


    }





    override func setHighlighted(highlighted: Bool, animated: Bool) {
        if (!highlighted) {
            self.contentView.backgroundColor = UIColor.whiteColor()
        } else {
            self.contentView.backgroundColor = UIColor(white: 0.0, alpha: 0.5)
        }


    }

    func setUpViews(data: lobbyData) {

        selectionStyle = UITableViewCellSelectionStyle.None

        var url = NSURL(string: data.PictureUrl)

        var request:NSURLRequest = NSURLRequest(URL: url!)
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            self.img = UIImage(data: data) as UIImage!
            self.imgView.image = self.img;
            self.imgView.contentMode = UIViewContentMode.ScaleAspectFill;
            self.imgView.clipsToBounds = true;
        })


        platform.text = data.Platform;
        platform.textAlignment = NSTextAlignment.Center
        platform.font = platform.font.fontWithSize(9)
        platform.textColor = UIColor.whiteColor();
        platform.backgroundColor = UIColor(rgba: colorFromSystem(data.Platform))


        title.text = data.Name
        title.setTranslatesAutoresizingMaskIntoConstraints(false)
        title.font = title.font.fontWithSize(16)
        title.layoutMargins = UIEdgeInsetsZero


        desc.text = data.Description
        self.desc.font = UIFont.systemFontOfSize(11.0)
        desc.editable = false
        desc.userInteractionEnabled = false
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.setTranslatesAutoresizingMaskIntoConstraints(false)
        desc.backgroundColor = UIColor.clearColor()

        tags.text = data.getTagText
        tags.font = tags.font.fontWithSize(9)

        time.text = data.timeInHuman
        time.font = time.font.fontWithSize(9)

        time.textColor = UIColor(rgba: colors.tealMain)


        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2)

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


    var data = lobbyData()

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



    func setCell(item: lobbyData) {


        self.selectionStyle = UITableViewCellSelectionStyle.None
        setUpViews(item)


        self.contentView.addSubview(title)


        setUpConstraints();




    }




    func setUpConstraints() {

        var viewsDict = Dictionary <String, UIView>()
        viewsDict["title"] = title
        title.snp_remakeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView).offset(0)
            make.left.equalTo(self.contentView).offset(16)
            make.bottom.equalTo(self.contentView).offset(0)
            make.right.equalTo(self.contentView).offset(0)


        }


    }



    func setUpViews(data: lobbyData) {
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


