//
//  pupTests2.swift
//  pupTests2
//
//  Created by Alex Hartwell on 6/8/15.
//  Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import UIKit
import XCTest
import pup
import Alamofire

class pupTests2: XCTestCase {
    
   
    
    func testExample() {
        // This is an example of a functional test case.
        XCTAssert(true, "Pass")
    }
    
    
    func jsonResponse() {
        
        XCTAssert(true, "Pass")
        
        
    }

    func testIt2() {

        var image:UIImage = UIImage(named: "fortest")!
        println(image);
       

        let imageToSave:NSData = NSData(data: UIImageJPEGRepresentation(image, 0.9));
        println(imageToSave)
        let parameters = [
            "email": "test@testing.com",
            "password": "swarmnyc",
            "username": "asasdasd"
        ]
        
        
        let imageData:NSData = NSData(data: UIImageJPEGRepresentation(image, 1.0))
        
        SRWebClient.POST(appURLS().register)
            .data(imageToSave, fieldName:"portriat", data:parameters)
            .send({(response:AnyObject!, status:Int) -> Void in
                println("success");
                //process success response
                },failure:{(error:NSError!) -> Void in
                    println("failure")
                    //process failure response
            })
        

    }
    
    func testRegister() {
    let parameters = [
    "email": "hello@hello.com",
    "password": "swarmnyc",
    "username": "what what"
    ]
    
//    Alamofire.request(.POST, "\(appURLS().register)", parameters: parameters).responseJSON { (request, response, JSON, error) in
//
//
//    println(JSON)
//    println("----")
//    println(response)
//
//    };
        XCTAssert(true, "Pass")

    }
    
  
    
    
    
}
