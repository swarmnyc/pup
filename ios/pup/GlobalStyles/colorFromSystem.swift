//
// Created by Alex Hartwell on 5/22/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


func colorFromSystem(system: String) -> String {
    switch(system.uppercaseString) {
        case "PS3":
            return colors.PS3
        case "PS4":
             return colors.PS4
        case "PC":
             return colors.PC
        case "XBOX360":
             return colors.X360
        case "XBOXONE":
            return colors.XONE
        default:

            return colors.PC


    }

}