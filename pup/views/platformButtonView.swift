//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit



class PlatformButtonToggle: UIButton {

    var checked: Bool = false

    var buttonDelegate: SimpleButtonDelegate? = nil

    var active: Bool = true

    var returnType: String = "platform";

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


    func hideIfNotNeeded(possiblePlatforms: Array<String>) {
        var hidden = true;

        for (var i = 0; i<possiblePlatforms.count; i++) {
            println(self.currentTitle!)

                if (appData.platformDict[self.currentTitle!] == possiblePlatforms[i]) {
                    hidden = false;
                    println("it's good")
                    println(self.currentTitle!)
                }

//                if ((appData.platformDict[appData.platforms[p]] == possiblePlatforms[i]) && (appData.platformDict[appData.platforms[p]] == self.currentTitle!)) {
//                    hidden = false;
//                    println("it's good")
//                    println(self.currentTitle!)
//                }

        }

        if (hidden) {
            makeInactive()
        } else {
            makeActive()
        }


    }


    func buttonAction(sender: PlatformButtonToggle!) {
        toggleSelected(sender)

    }

    func buttonPressed(sender: UIButton!) {
       // println("hello")
    }

    func toggleSelected(sender: PlatformButtonToggle!) {

        if (active) {
            buttonDelegate?.touchUp(sender, type: returnType)

            if (checked) {
                checked = false


            } else {
                checked = true


            }
            setSelectionStyle()


        }


    }


    func makeInactive() {

        active=true;
        uncheck();

        active = false;
        setTitleColor(UIColor(rgba: colors.lightGray), forState: .Normal)
    }

    func makeActive() {
        active = true
        uncheck();
        setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
    }

    func dimIfUnchecked() {
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
        if (active) {
            checked = false;
            backgroundColor = UIColor.whiteColor()
            setTitleColor(UIColor(rgba: colors.mainGrey), forState: .Normal)
        }
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