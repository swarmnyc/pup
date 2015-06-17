//
// Created by Alex Hartwell on 6/2/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


extension String {
    func replace(string:String, replacement:String) -> String {
        return self.stringByReplacingOccurrencesOfString(string, withString: replacement, options: NSStringCompareOptions.LiteralSearch, range: nil)
    }

    func removeWhitespace() -> String {
        return self.replace(" ", replacement: "")
    }

    func shorten(maxLength: Int) -> String {
        if count(self) > maxLength {
            return self.substringToIndex(advance(self.startIndex, maxLength)) + "..."
        }

        return self;
    }

}