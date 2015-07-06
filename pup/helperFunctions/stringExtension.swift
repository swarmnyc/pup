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

    func getPUPUrl() -> String {
        return self.stringByReplacingOccurrencesOfString("~", withString: urls.siteBase, options: NSStringCompareOptions.LiteralSearch, range: nil).replace("\"",replacement: "");
    }


    func URLEncodedString() -> String? {
        var customAllowedSet =  NSCharacterSet.URLQueryAllowedCharacterSet()
        var escapedString = self.stringByAddingPercentEncodingWithAllowedCharacters(customAllowedSet)
        return escapedString
    }
    static func queryStringFromParameters(parameters: Dictionary<String,String>) -> String? {
        if (parameters.count == 0)
        {
            return nil
        }
        var queryString : String? = nil
        for (key, value) in parameters {
            if let encodedKey = key.URLEncodedString() {
                if let encodedValue = value.URLEncodedString() {
                    if queryString == nil
                    {
                        queryString = "?"
                    }
                    else
                    {
                        queryString! += "&"
                    }
                    queryString! += encodedKey + "=" + encodedValue
                }
            }
        }
        return queryString
    }


}

