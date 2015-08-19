//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore
//import WebImage

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
    var divider: UIView = UIView()
    var isNew: Bool = true;
    var justStarted: Bool = false;

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


    func removeOffset(delay: Double) {
        UIView.animateWithDuration(delay, animations: {
            var trans = CGAffineTransformMakeTranslation(0.0, 0.0);
            self.contentView.transform = trans;
        })

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
            make.top.equalTo(self).offset(UIConstants.halfHorizontalPadding)
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
            make.top.equalTo(textRightTagsAndDate.snp_bottom).offset(0)
            make.left.equalTo(imageAndPlatform).offset(0)
            make.bottom.equalTo(self.contentView)


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
            make.height.equalTo(15)

        }
        desc.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(textRightTitleAndDesc).offset(UIConstants.halfHorizontalPadding)
            make.right.equalTo(textRightTitleAndDesc).offset(-UIConstants.halfHorizontalPadding)
            make.top.equalTo(title.snp_bottom).offset(UIConstants.halfHorizontalPadding - 3)
            make.bottom.equalTo(textRightTitleAndDesc).offset(-UIConstants.halfVerticalPadding)

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

        if (data.animateIn) {
            var trans = CGAffineTransformMakeTranslation(UIScreen.mainScreen().bounds.width, 0.0);
            self.contentView.transform = trans;
            println(data.animateIn);
            UIView.animateWithDuration(0.1 + (Double(data.index) * 0.08), animations: {
                trans = CGAffineTransformMakeTranslation(0.0, 0.0);
                self.contentView.transform = trans;
            });

        } else {
            var trans = CGAffineTransformMakeTranslation(0.0, 0.0);
            self.contentView.transform = trans;
        }
        
//        self.contentView.layer.borderWidth = 0.5;
//        self.contentView.layer.borderColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor;


        self.backgroundColor = UIColor(rgba: colors.lightGray);

        selectionStyle = UITableViewCellSelectionStyle.None

        var url = NSURL(string: data.pictureUrl.getPUPUrl())

        backImageView.clipsToBounds = true;
        backImageView.contentMode = UIViewContentMode.ScaleAspectFill;
        backImageView.image = getImageWithColor(UIColor(rgba: colorFromSystem(data.platform)), CGSizeMake(93,98))

        imgView.clipsToBounds = true;
        imgView.contentMode = UIViewContentMode.ScaleAspectFill;
        imgView.backgroundColor = UIColor(rgba: colors.orange)
        imgView.alpha = 0;
        self.imgView.sd_setImageWithURL(url!, placeholderImage: nil, options: SDWebImageOptions.RefreshCached, progress: {
            (recievedSize, exprectedSize) -> Void in
            
           // println(recievedSize);
            
            }, completed: {
                (image, error, cacheType, imageUrl) -> Void in
                self.imgView.image = image;
                UIView.animateWithDuration(0.3, animations: {
                    () -> Void in
                    self.imgView.alpha = 1;
                });
        });
        
//        self.imgView.hnk_setImageFromURL(url!, placeholder:nil, format: nil, failure: nil, success: {
//            (image) -> Void in
//            self.imgView.image = image;
//            UIView.animateWithDuration(0.3, animations: {
//                () -> Void in
//                self.imgView.alpha = 1;
//            });
//
//        })
       

        platform.text = data.platform.replacePCWithSteam();
        platform.textAlignment = NSTextAlignment.Center
        platform.font = UIConstants.tagType;
        platform.textColor = UIColor.whiteColor();
        platform.backgroundColor = UIColor(rgba: colorFromSystem(data.platform))


        title.text = data.name
        title.font = UIConstants.titleFont;
        title.layoutMargins = UIEdgeInsetsZero


        desc.text = data.description.shorten(138)
        self.desc.font = UIConstants.paragraphType;
        desc.editable = false
        desc.userInteractionEnabled = false
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.backgroundColor = UIColor.clearColor()

        tags.text = data.getTagText
        tags.font = UIConstants.tagType;
        time.text = data.timeInHuman
        time.font = UIConstants.tagType;

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





class headerCell: UITableViewHeaderFooterView {

    var title:UILabel = UILabel()

    var containerView: UIView = UIView();


  
    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder);
        self.containerView.addSubview(self.title);
        self.contentView.addSubview(containerView)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame);
        self.containerView.addSubview(self.title);
        self.contentView.addSubview(containerView)
    }


    func setCell(title: String) {
        println("setupCell");
    

        setUpViews(title)


    }







    func setUpViews(title: String) {
        
       
        //self.frame = CGRectMake(0,0,UIScreen.mainScreen().bounds.width, 35);
        self.title.backgroundColor = UIColor(rgba: colors.lightGray)
        self.contentView.backgroundColor = UIColor.clearColor();
        containerView.backgroundColor = UIColor.clearColor()
        containerView.clipsToBounds = true;
        self.backgroundView = UIView();
        self.backgroundView?.backgroundColor = UIColor.clearColor();
        
        self.title.text = title;
        self.title.font = UIConstants.titleFont;
        self.title.textColor = UIColor.blackColor().lighterColor(0.6);
        containerView.layer.frame = CGRectMake(0,0,UIScreen.mainScreen().bounds.width, 35);
       
        self.title.layer.borderWidth = 0.5;
        self.title.layer.borderColor = UIColor.clearColor().CGColor;
        self.title.layer.masksToBounds = false;
        self.title.clipsToBounds = false;
        self.title.layer.shadowRadius = 0;
        self.title.layer.shadowColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.2).CGColor
        self.title.layer.shadowOpacity = 1;
        self.title.layer.shadowOffset = CGSizeMake(0, 2);
        self.title.frame = CGRectMake(0,0,containerView.frame.width, containerView.frame.height - 2);
        
        
        var trans = CGAffineTransformMakeTranslation(0.0, 0.0);
        self.title.transform = trans;

    }


    override var layoutMargins: UIEdgeInsets {
        get { return UIEdgeInsetsZero }
        set(newVal) {}
    }

 
    
    func moveLeft(speed: Double, success: (() -> Void)?) {
        println("move left" + self.title.text!);
        var trans = CGAffineTransformMakeTranslation(-self.contentView.frame.width, 0.0);
        UIView.animateWithDuration(speed, animations: {
            self.title.transform = trans;
            }, completion: {
                finished in
                success?();
                
        });

    }
    
    func removeOffset(speed: Double) {
        println("offset removed!!!! " + self.title.text!);
        var trans = CGAffineTransformMakeTranslation(0.0, 0.0);
        UIView.animateWithDuration(speed, animations: {
            self.title.transform = trans;
        })

    }

}


