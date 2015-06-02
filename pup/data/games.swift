//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


struct gameData {
    var State = "activated"
    var Tags = ""
    var Name = "Battlefield 4"
    var ThumbnailPictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var PictureUrl = "http://upload.wikimedia.org/wikipedia/en/7/75/Battlefield_4_cover_art.jpg"
    var Description = "Battlefield 4 is a 2013 first-person shooter video game developed by Swedish video game developer EA Digital Illusions CE (DICE) and published by Electronic Arts. It is a sequel to 2011's Battlefield 3 and was released on October 29, 2013 in North America, October 31, 2013 in Australia, November 1, 2013 in Europe and New Zealand and November 7, 2013 in Japan for Microsoft Windows, PlayStation 3, PlayStation 4, Xbox 360 and Xbox One."
    var ReleaseDateUtc = ""
    var GameTypes = ""
    var Rank = 0
    var Platforms = ["PS4", "PS3","XBOX360", "XBOXONE"];


}



class Searcher {

    var data: Array<gameData> = [];

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
        var suffix = "?search=\(name)";

        if (platforms.count>0) {
            for (var i = 0; i<platforms.count; i++) {
                suffix+="&platform=\(platforms[i])";
            }
        }

        let requestUrl = NSURL(string: "\(urls.games)\(suffix)")

            let task = NSURLSession.sharedSession().dataTaskWithURL(requestUrl!) {(data, response, error) in
                println(error)
                let jsonResponse = JSON(data: data)
                println(jsonResponse)

            }

            task.resume();
            println(suffix);




    }

}


