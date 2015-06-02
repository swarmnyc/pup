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
        gameName.text = data.Name
        self.contentView.addSubview(gameName);

        gameName.snp_makeConstraints{(make) -> Void in
            make.left.equalTo(self.contentView).offset(0);
            make.top.equalTo(self.contentView).offset(0);
            make.bottom.equalTo(self.contentView).offset(0);
            make.width.equalTo(300);

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