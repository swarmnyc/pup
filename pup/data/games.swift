//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation






class Searcher {

    var data: Array<gameData> = [];
    var delegate: SearcherDelegate?
    var platforms: Array<String> = [];

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

    func search(name: String) {
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

            let task = NSURLSession.sharedSession().dataTaskWithURL(requestUrl!) {
                (data, response, error) in
                println(error)
                let jsonResponse = JSON(data: data)
                println(jsonResponse)
                self.createGamesArray(jsonResponse)

            }

            task.resume();
            println(suffix);


        }
    }


    func createGamesArray(data: JSON) {
        self.data = [];

        for (index: String, subJson: JSON) in data {
            var Name: String = subJson["name"].stringValue;
            var ThumbnailPictureUrl: String = subJson["thumbnailPictureUrl"].stringValue;
            var PictureUrl: String = subJson["pictureUrl"].stringValue;
            var Description: String = subJson["description"].stringValue;
            var ReleaseDateUtc: String = subJson["releaseDateUtc"].stringValue;
            var Rank: Int = subJson["rank"].intValue;

            var Platforms: Array<String> = [];
            for (platformIndex: String, platformJson: JSON) in subJson["platforms"] {
                Platforms.append(platformJson.stringValue)
            }
            var Tags: Array<String> = [];
            for (platformIndex: String, tagsJson: JSON) in subJson["tags"] {
                Tags.append(tagsJson.stringValue)
            }

            var State: String = subJson["state"].stringValue;
            self.data.append(gameData(State: State, Tags: Tags, Name: Name, ThumbnailPictureUrl: ThumbnailPictureUrl, PictureUrl: PictureUrl, Description: Description, ReleaseDateUtc: ReleaseDateUtc, Rank: Rank, Platforms: Platforms))
        }

        self.delegate?.handOffResults(self.data);
    }


}

struct gameData {
    var State = "activated"
    var Tags = []
    var Name = "Battlefield 4"
    var ThumbnailPictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var PictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var Description = "Battlefield 4 is a 2013 first-person shooter video game developed by Swedish video game developer EA Digital Illusions CE (DICE) and published by Electronic Arts. It is a sequel to 2011's Battlefield 3 and was released on October 29, 2013 in North America, October 31, 2013 in Australia, November 1, 2013 in Europe and New Zealand and November 7, 2013 in Japan for Microsoft Windows, PlayStation 3, PlayStation 4, Xbox 360 and Xbox One."
    var ReleaseDateUtc = ""
    var Rank = 0
    var Platforms = ["PS4", "PS3","XBOX360", "XBOXONE"];


}


