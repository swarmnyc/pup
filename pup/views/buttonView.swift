//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class Button: UIButton {

    var checked: Bool = false

    var buttonDelegate: SimpleButtonDelegate? = nil

    override init(frame: CGRect) {
        super.init(frame: frame)


    }




    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    func setUpButton(text: String, delegate: SimpleButtonDelegate) {
        buttonDelegate = delegate

        setTitle(text, forState: .Normal);
        setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        setTitleColor(UIColor.whiteColor(), forState: .Selected)
        layer.borderWidth = 0.5
        layer.borderColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2).CGColor


        titleLabel!.font = titleLabel!.font.fontWithSize(11)
        addTarget(self, action: "buttonAction:", forControlEvents: UIControlEvents.TouchUpInside)
        addTarget(self, action: "buttonPressed:", forControlEvents: UIControlEvents.TouchDown)
    }


    func buttonAction(sender: Button!) {
        toggleSelected(sender)

    }

    func buttonPressed(sender: UIButton!) {
        println("hello")
    }

    func toggleSelected(sender: Button!) {
        buttonDelegate?.touchUp(sender, type: "platform")

        if (checked) {
            checked = false


        } else {
            checked = true


        }
        setSelectionStyle()





    }

    func dimIfInactive() {
        if (checked==false) {
            println("dimmed")
            setTitleColor(UIColor(rgba: colors.mainGrey).lighterColor(0.9), forState: .Normal)

        }
    }

    func makeItDarkerAgain() {
        println("make it darker again")
        setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)

    }

    func uncheck() {
        checked = false;
        backgroundColor = UIColor.whiteColor()
        setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
    }

    func setSelectionStyle() {
        if (checked) {
        backgroundColor = UIColor(rgba: colors.orange)
            setTitleColor(UIColor.whiteColor(), forState: .Normal)

        } else {
            backgroundColor = UIColor.whiteColor()
            setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)

        }


    }

}