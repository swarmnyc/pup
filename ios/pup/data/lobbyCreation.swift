//
// Created by Alex Hartwell on 6/3/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire

class LobbyCreationModel {

//Example code from alamo fire for how to make a request with custom authorization header

//    let URL = NSURL(string: "http://httpbin.org/post")!
//    let mutableURLRequest = NSMutableURLRequest(URL: URL)
//mutableURLRequest.HTTPMethod = "POST"
//
//    let parameters = ["foo": "bar"]
//    var JSONSerializationError: NSError? = nil
//mutableURLRequest.HTTPBody = NSJSONSerialization.dataWithJSONObject(parameters, options: nil, error: &JSONSerializationError)
//mutableURLRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
//
//Alamofire.request(mutableURLRequest)

    var gameId: String? {
        get {
            return _gameId;
        }
        set (value) {
            _gameId = value;
            selectedPlatform = nil;
            controller?.modifiedInputFields(false);
        }
    }
    var _gameId: String?
    var selectedPlatform: String? {
        get {
            return _selectedPlatform;
        }
        set (value) {
            _selectedPlatform = value;
            controller?.modifiedInputFields(false);
        }
    }
    var _selectedPlatform: String?

    var startTime: NSDate {
        get {
            return _startTime!;
        }
        set (value) {
            _startTime = value;
            controller?.modifiedInputFields(false);
        }
    }
    var _startTime: NSDate?
    var startTimeString: String?

    var possiblePlatforms: [String] {
        get {
            return _possiblePlatforms!;
        }
        set (value) {
            _possiblePlatforms = value;
            controller?.modifiedInputFields(false);
        }
    }
    var _possiblePlatforms: [String]?

    var PlayStyle: String?

    var GamerSkill: String?

    var description: String? {
        get {
            return _description;
        }
        set (value) {
            _description = value;
            controller?.modifiedInputFields(false);
        }
    }
    var _description: String?

    var controller: CreateLobbyController?

    init() {

        self.startTime = NSDate().dateByAddingMinutes(20);
        self.possiblePlatforms = [""];
    }



    func clearData() {
        selectedPlatform = nil;
        possiblePlatforms = [""];

    }

    func resetData() {
        self.possiblePlatforms = [""];
        self.selectedPlatform = nil;
        self.gameId = nil;
    }


    func changeDateDay(newDate: NSDate) -> NSDate {
        println(startTime)
        println("old start ^")
        var oldCalendar = NSCalendar.currentCalendar()
        var newCalendar = NSCalendar.currentCalendar()
        var oldComponents = oldCalendar.components(NSCalendarUnit.CalendarUnitYear | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.HourCalendarUnit | NSCalendarUnit.CalendarUnitMinute,  fromDate: startTime)
        var newComponents = newCalendar.components(NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.HourCalendarUnit | NSCalendarUnit.CalendarUnitMinute,  fromDate: newDate)

        oldComponents.month = newComponents.month
        oldComponents.day = newComponents.day
        oldComponents.hour = newComponents.hour
        oldComponents.minute = newComponents.minute

        startTime = oldCalendar.dateFromComponents(oldComponents)!
        println(startTime)
        println("new start ^")
        return startTime
    }

    func changeDateTime(newDate: NSDate) -> NSDate  {
        println(startTime)
        println("old start ^")
        var oldCalendar = NSCalendar.currentCalendar()
        var newCalendar = NSCalendar.currentCalendar()
        var oldComponents = oldCalendar.components(NSCalendarUnit.CalendarUnitYear | NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.HourCalendarUnit | NSCalendarUnit.CalendarUnitMinute,  fromDate: startTime)
        var newComponents = newCalendar.components(NSCalendarUnit.CalendarUnitMonth | NSCalendarUnit.CalendarUnitDay | NSCalendarUnit.HourCalendarUnit | NSCalendarUnit.CalendarUnitMinute,  fromDate: newDate)

        oldComponents.hour = newComponents.hour
        oldComponents.minute = newComponents.minute

        startTime = oldCalendar.dateFromComponents(oldComponents)!
        println(startTime)
        println("new start ^")
        return startTime;

    }


    func checkData() -> Bool {

        if (_gameId != nil && _selectedPlatform != nil && PlayStyle != nil && GamerSkill != nil) {
            return true
        }
        

        makeSureTimeIsInFuture()


        return false
    }

    func makeSureTimeIsInFuture() {
        if (_startTime!.minutesAfterDate(NSDate())<20) {
            _startTime = NSDate().dateByAddingMinutes(20);
        }
    }

    func encodeRequest() ->String {


            var urlEnd = ""
            urlEnd += "GameId=" + gameId!
            urlEnd += "&Platform=" + appData.platformDict[selectedPlatform!]!
            urlEnd += "&PlayStyles=" + PlayStyle!
            urlEnd += "&SkillLevels=" + GamerSkill!

            var dateFormatter = NSDateFormatter();
            var timeZone = NSTimeZone(name: "UTC")
            dateFormatter.timeZone = timeZone;
            dateFormatter.dateFormat = "yyy-MM-dd HH:mm:ss";
            var dateString = dateFormatter.stringFromDate(startTime) as! String

//            urlEnd += "&StartTimeUtc=" + startTime.toString(format: .Custom("yyyy-MM-dd")) + "T" + startTime.toString(format: .Custom("hh:mm:ss")) + "Z"
            urlEnd += "&StartTimeUtc=" + dateString.stringByReplacingOccurrencesOfString(" ", withString: "T", options: NSStringCompareOptions.LiteralSearch, range: nil) + "Z"
        if (description != nil) {
            urlEnd += "&description=" + description!
        }
            println(urlEnd);
            println(currentUser.data.accessToken)
            return urlEnd.stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLQueryAllowedCharacterSet())!;
    }



    func createRequest(success: (newLobby: LobbyData) -> Void, failure: () -> Void) {

            var urlEnd = encodeRequest()

        let URL = NSURL(string: urls.lobbies)!


        let mutableURLRequest = NSMutableURLRequest(URL: URL)
        mutableURLRequest.HTTPMethod = "POST"

        var JSONSerializationError: NSError? = nil
        mutableURLRequest.HTTPBody = urlEnd.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false);
        mutableURLRequest.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        mutableURLRequest.setValue("Bearer \(currentUser.data.accessToken)", forHTTPHeaderField: "Authorization")

        Alamofire.request(mutableURLRequest).responseJSON { (request, response, JSON, error) in
            var newLobby = LobbyData(data: JSON as! NSDictionary)
            success(newLobby: newLobby);
            println(error)
            println("^error")
        }



    }



}