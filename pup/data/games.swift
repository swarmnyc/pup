//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import Alamofire





class SearchResultsModel {

    var data: Array<gameData> = [];
    var delegate: SearchResultsDelegate?
    var platforms: Array<String> = [];
    var searchTerm: String = ""

    var selectedImage = ""

    init() {


    }

    func addPlatformToSearch(addedPlatform: String) {
        println(platforms);
        platforms.append(appData.platformDict[addedPlatform]!);
        println(platforms);

    }

    func removePlatformFromSearch(removedPlatform: String) {
        println(platforms);

        for (var i = 0; i<platforms.count; i++) {
            if (platforms[i] == appData.platformDict[removedPlatform]) {
                platforms.removeAtIndex(i);
            }
        }
        println(platforms);
    }

    func setSearchTerm(name: String) {
        searchTerm = name;
    }

    func search(name: String, success: (newdata: Array<gameData>) -> Void, failure: () -> Void) {
        println("searching!!!!")
        if (name == "") {
            println("sorry no searching")
        } else {
            var suffix = "?search=\(name)";

            if (platforms.count > 0) {
                for (var i = 0; i < platforms.count; i++) {
                    suffix += "&platform=\(platforms[i])";
                }
            }

            let requestUrl = NSURL(string: "\(urls.games)\(suffix)")
            if (requestUrl != nil) {


                Alamofire.request(.GET, requestUrl!).responseJSON { (request, response, responseJSON, error) in
                    var resp = responseJSON as! NSArray
                   // println(resp)
                    self.createGamesArray(resp);

                    success(newdata: self.data);

                };


                println(suffix);
            }


        }
    }


    func createGamesArray(data: NSArray) {
        self.data = [];

        for (var i = 0; i<data.count; i++) {
            println("Creating Games")
            self.data.append(gameData(data: data[i] as! NSDictionary));
        }


//        self.delegate?.handOffResults(self.data);
    }


}

class gameData {
    var State = "activated"
    var Tags = []
    var Name = "Battlefield 4"
    var ThumbnailPictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var PictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var Description = "Battlefield 4 is a 2013 first-person shooter video game developed by Swedish video game developer EA Digital Illusions CE (DICE) and published by Electronic Arts. It is a sequel to 2011's Battlefield 3 and was released on October 29, 2013 in North America, October 31, 2013 in Australia, November 1, 2013 in Europe and New Zealand and November 7, 2013 in Japan for Microsoft Windows, PlayStation 3, PlayStation 4, Xbox 360 and Xbox One."
    var ReleaseDateUtc = ""
    var Rank = 0
    var Platforms = ["PS4", "PS3","XBOX360", "XBOXONE"];
    var id = ""

    init(data: NSDictionary) {

        State = data["state"] as! String
        Tags = data["tags"] as! NSArray
        Name = data["name"] as! String
        ThumbnailPictureUrl = data["thumbnailPictureUrl"] as! String
        PictureUrl = data["pictureUrl"] as! String
        Platforms = data["platforms"] as! Array<String>
        Rank = data["rank"] as! Int
        id = data["id"] as! String

    }

    init() {

    }

}


