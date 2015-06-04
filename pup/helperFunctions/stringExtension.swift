//
// Created by Alex Hartwell on 6/2/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


extension String
{
    func replace(target: String, withString: String) -> String
    {
        return self.stringByReplacingOccurrencesOfString(target, withString: withString, options: NSStringCompareOptions.LiteralSearch, range: nil)
    }
}