//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class HorizontalSelectView: UIView {

    var options: Array<HorizontalButtons> = []

    override init(frame: CGRect) {
        super.init(frame: frame)

    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func addOptions(options: Array<String>, buttonDelegate: SimpleButtonDelegate) {

        for (var i = 0; i<options.count; i++) {
            self.options.append(HorizontalButtons())
            println(options[i]);
            self.options[i].setUpButton(options[i], buttonDelegate: buttonDelegate);
            self.addSubview(self.options[i])
        }


    }

    func setUpView(parentView: UIView, topOffset: Double) {
        self.backgroundColor = UIColor(rgba: colors.orange);
        self.snp_remakeConstraints { (make) -> Void in
            make.left.equalTo(parentView).offset(0)
            make.top.equalTo(parentView).offset(topOffset)
            make.right.equalTo(parentView).offset(0)
            make.height.equalTo(200)
        }


    }




}




class HorizontalButtons: UILabel {


    var delegate: SimpleButtonDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)


    }


    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setUpButton(text: String, buttonDelegate: SimpleButtonDelegate) {
            self.userInteractionEnabled = true;
            self.text = text;
            self.delegate = buttonDelegate;
    }


    override func touchesBegan( touches: Set<NSObject>, withEvent event: UIEvent) {
        println("wooh")
        self.backgroundColor = self.backgroundColor?.darkerColor(0.3);

    }

    override func touchesEnded( touches: Set<NSObject>, withEvent event: UIEvent) {
        self.backgroundColor = UIColor(rgba: colors.orange)
        delegate?.touchUp(self, type: "horizontalScrollButton")


    }



}
