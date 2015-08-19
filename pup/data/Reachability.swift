//
// Created by Alex Hartwell on 8/18/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

public class Reachability {

    class func isConnectedToNetwork()->Bool{

        var Status:Bool = false
        let url = NSURL(string: "http://www.google.com");
        let request = NSMutableURLRequest(URL: url!)
        request.HTTPMethod = "HEAD"
        request.cachePolicy = NSURLRequestCachePolicy.ReloadIgnoringLocalAndRemoteCacheData
        request.timeoutInterval = 1500.0

        var response: NSURLResponse?

        var data = NSURLConnection.sendSynchronousRequest(request, returningResponse: &response, error: nil) as NSData?
        
        if let httpResponse = response as? NSHTTPURLResponse {
            println(httpResponse);
            if httpResponse.statusCode == 200 {
                Status = true
            }
        }

        return Status
    }
}