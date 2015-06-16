//
// Created by Alex Hartwell on 6/2/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class SearchResultsViewCell: UICollectionViewCell {

    var gameName: UILabel = UILabel();
    var gameImg: UIImageView = UIImageView();


    override init(frame: CGRect) {
        super.init(frame: frame)


        contentView.addSubview(gameImg)


        contentView.addSubview(gameName)
    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }


    func setUpCell(data: gameData) {
        self.backgroundColor = UIColor(rgba: "#f1f1f1")
        gameName.text = data.Name
        gameName.textColor = UIColor(rgba: colors.mainGrey);
        println(data.Name)
        gameName.font = gameName.font.fontWithSize(12)

        var url = NSURL(string: data.ThumbnailPictureUrl)
        gameImg.backgroundColor = UIColor.blackColor();
        gameImg.frame.size = CGSizeMake(45, 45);
        self.gameImg.hnk_setImageFromURL(url!)
        self.gameImg.clipsToBounds = true;

//        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
//            var img = UIImage(data: data) as UIImage!
//            self.gameImg.image = img;
//            self.gameImg.contentMode = UIViewContentMode.ScaleAspectFill;
//            self.gameImg.clipsToBounds = true;
//        })


        gameImg.snp_makeConstraints{ (make) -> Void in
            make.left.equalTo(self.contentView).offset(0)
            make.top.equalTo(self.contentView).offset(0)
            make.bottom.equalTo(self.contentView).offset(0)
            make.width.equalTo(45)
        }
        gameName.snp_makeConstraints{(make) -> Void in
            make.left.equalTo(self.gameImg.snp_right).offset(UIConstants.horizontalPadding);
            make.top.equalTo(self.contentView).offset(0);
            make.bottom.equalTo(self.contentView).offset(0);
            make.right.equalTo(self.contentView).offset(-UIConstants.horizontalPadding);

        }
//        imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height*2/3))
//        imageView.contentMode = UIViewContentMode.ScaleAspectFit
//
//        let textFrame = CGRect(x: 0, y: imageView.frame.size.height, width: frame.size.width, height: frame.size.height/3)
//        textLabel = UILabel(frame: textFrame)
//        textLabel.font = UIFont.systemFontOfSize(UIFont.smallSystemFontSize())
//        textLabel.textAlignment = .Center
    }

}