//
//  viewController.swift
//  pup
//
//  Created by Alex Hartwell on 5/18/15.
//  Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
   
    var findAGame: UIViewController!
    
    var table:UITableView!
    var textV:UITextView!
    
    var listOfGames = GameList();
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor=UIColor.whiteColor()
        table = UITableView();
        table.separatorInset = UIEdgeInsetsZero
        self.title = "All Games"
//        textV = UITextView();

//        textV.text = listOfGames.games[0].Description
//        self.view.addSubview(textV)


        table.delegate = self;
        table.dataSource = self;
        
        self.view.addSubview(table);






//        textV.snp_makeConstraints { (make) -> Void in
//            make.right.equalTo(self.view).offset(0)
//            make.left.equalTo(self.view).offset(0)
//            make.top.equalTo(self.view).offset(0)
//            make.bottom.equalTo(self.view).offset(0)
//        }

        table.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self.view).offset(0)
            make.left.equalTo(self.view).offset(0)
            make.top.equalTo(self.view).offset(0)
            make.bottom.equalTo(self.view).offset(0)
        }
        
    }
   
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.listOfGames.games.count;
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        //var cell:UITableViewCell = self.tableV.dequeueReusableCellWithIdentifier("cell") as UITableViewCell
        //cell.textLabel.text = self.items[indexPath.row]
        if !self.listOfGames.games[indexPath.row].isBreakdown {
            let cell: gameCell = gameCell();
            cell.setCell(self.listOfGames.games[indexPath.row])
            return cell
        } else {
            let cell: headerCell = headerCell();
            cell.setCell(self.listOfGames.games[indexPath.row])
            return cell

        }


    }
    
    func tableView(tlableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        if !self.listOfGames.games[indexPath.row].isBreakdown {
            return 119.0;
        } else {
            return 26.0;
        }


}
   func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
       println(tableView.cellForRowAtIndexPath(indexPath));
    }
    
    
    

    
    
}


class gameCell: UITableViewCell {




    var imageAndPlatform: UIView = UIView()
    var textRightTop: UIView = UIView()
    var textRightBottom: UIView = UIView()

    var title:UILabel = UILabel()
    var desc: UITextView = UITextView()
    var imgView: UIImageView = UIImageView()
    var img: UIImage = UIImage()
    var tags: UILabel = UILabel()
    var time: UILabel = UILabel()
    var platform: UILabel = UILabel()
    var divider = UIView()

    var data = lobbyData()
    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
       // println(style);
        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }



    func setCell(item: lobbyData) {
        data = item;

        setUpViews()

        imageAndPlatform.addSubview(imgView)
        imageAndPlatform.addSubview(platform)

        self.contentView.addSubview(imageAndPlatform)

        textRightTop.addSubview(title)
        textRightTop.addSubview(desc)

        self.contentView.addSubview(textRightTop)

        textRightBottom.addSubview(tags)
        textRightBottom.addSubview(time)
        textRightBottom.addSubview(divider)

        self.contentView.addSubview(textRightBottom)

        setUpConstraints();

//        label1.setTranslatesAutoresizingMaskIntoConstraints(false)
//        textV.setTranslatesAutoresizingMaskIntoConstraints(false)
//        textV.editable = false
//        var viewsDict = Dictionary <String, UIView>()
//        viewsDict["label1"] = label1
//        viewsDict["textV"] = textV
        
        

      //  textV.backgroundColor = UIColor.blackColor()

//        subView.snp_makeConstraints { (make) -> Void in
//                make.left.equalTo(contentView).offset(0)
//                make.right.equalTo(contentView).offset(0)
//        }

//        label1.snp_makeConstraints { (make) -> Void in
//                make.left.equalTo(subView).offset(0)
//
//        }
        //textV.snp_makeConstraints { (make) -> Void in
//                make.right.equalTo(self.contentView).offset(0)
//                make.top.equalTo(self.contentView).offset(0)
//                make.left.equalTo(self.contentView).offset(0)
//                make.bottom.equalTo(self.contentView).offset(0)
//
//
//        }
        
    
    }




    func setUpConstraints() {

        var viewsDict = Dictionary <String, UIView>()
        viewsDict["imageAndPlatform"] = imageAndPlatform
        viewsDict["imgView"] = imgView
        viewsDict["platform"] = platform
        imageAndPlatform.snp_makeConstraints { (make) -> Void in
                make.width.equalTo(93)
                make.top.equalTo(self.contentView).offset(0)
                make.left.equalTo(self.contentView).offset(0)
                make.bottom.equalTo(self.contentView).offset(0)


        }


        textRightTop.snp_makeConstraints { (make) -> Void in
                make.left.equalTo(imageAndPlatform.snp_right).offset(0);
                make.right.equalTo(self.contentView).offset(0)
                make.top.equalTo(self.contentView).offset(0)
                make.bottom.equalTo(self.contentView).offset(26)
        }
        textRightBottom.snp_makeConstraints { (make) -> Void in
                make.left.equalTo(imageAndPlatform.snp_right).offset(0);
                make.right.equalTo(self.contentView).offset(0)
                make.height.equalTo(26)
                make.bottom.equalTo(self.contentView).offset(0)
        }

        imgView.snp_makeConstraints { (make) -> Void in
                make.right.equalTo(imageAndPlatform).offset(0)
                make.top.equalTo(imageAndPlatform).offset(0)
                make.left.equalTo(imageAndPlatform).offset(0)
                make.height.equalTo(93)


        }

        platform.snp_makeConstraints { (make) -> Void in
                make.top.equalTo(imgView.snp_bottom).offset(0)
                make.right.equalTo(imageAndPlatform).offset(0)
                make.left.equalTo(imageAndPlatform).offset(0)
                make.height.equalTo(26)
                //make.bottom.equalTo(imageAndPlatform).offset(0)
        }

        title.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(textRightTop).offset(13)
            make.right.equalTo(textRightTop).offset(-16)
            make.top.equalTo(textRightTop).offset(11)
            make.height.equalTo(14)

        }
        desc.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(textRightTop).offset(13)
            make.right.equalTo(textRightTop).offset(-16)
            make.top.equalTo(title.snp_bottom).offset(11)
            make.bottom.equalTo(textRightTop).offset(-11)

        }

        tags.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(textRightBottom).offset(13);
            make.top.equalTo(textRightBottom).offset(0)
            make.bottom.equalTo(textRightBottom).offset(0)
            make.width.greaterThanOrEqualTo(100)
        }

        time.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(textRightBottom).offset(-13)
            make.top.equalTo(textRightBottom).offset(0)
            make.bottom.equalTo(textRightBottom).offset(0)
        }

        divider.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(textRightBottom).offset(0)
            make.left.equalTo(textRightBottom).offset(0)
            make.right.equalTo(textRightBottom).offset(0)
            make.height.equalTo(0.5)
        }


    }



    func setUpViews() {
        var url = NSURL(string: data.PictureUrl)
        var imgFromUrl = NSData(contentsOfURL : url!)
        img = UIImage(data: imgFromUrl!)!
        imgView.image = img;

        platform.text = data.Platform;
        platform.textAlignment = NSTextAlignment.Center
        platform.font = platform.font.fontWithSize(10)
        platform.textColor = UIColor.whiteColor()
        platform.backgroundColor = UIColor(rgba: "#04a4ef")


        title.text = data.Name
        title.setTranslatesAutoresizingMaskIntoConstraints(false)
        title.font = title.font.fontWithSize(11)
        title.layoutMargins = UIEdgeInsetsZero


        desc.text = data.Description
        desc.font = desc.font.fontWithSize(11)
        desc.editable = false
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.setTranslatesAutoresizingMaskIntoConstraints(false)

        tags.text = "\(data.PlayStyle) > \(data.SkillLevel)"
        tags.font = tags.font.fontWithSize(11)

        time.text = "3:30 PM"
        time.font = time.font.fontWithSize(11)

        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.5)

    }


    override var layoutMargins: UIEdgeInsets {
        get { return UIEdgeInsetsZero }
        set(newVal) {}
    }
    
    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    
}

class headerCell: UITableViewCell {

    var title:UILabel = UILabel()


    var data = lobbyData()

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {

        super.init(style: style, reuseIdentifier: reuseIdentifier)
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }



    func setCell(item: lobbyData) {
        data = item;
        self.selectionStyle = UITableViewCellSelectionStyle.None
        setUpViews()



        self.contentView.addSubview(title)


        setUpConstraints();

//        label1.setTranslatesAutoresizingMaskIntoConstraints(false)
//        textV.setTranslatesAutoresizingMaskIntoConstraints(false)
//        textV.editable = false
//        var viewsDict = Dictionary <String, UIView>()
//        viewsDict["label1"] = label1
//        viewsDict["textV"] = textV



        //  textV.backgroundColor = UIColor.blackColor()

//        subView.snp_makeConstraints { (make) -> Void in
//                make.left.equalTo(contentView).offset(0)
//                make.right.equalTo(contentView).offset(0)
//        }

//        label1.snp_makeConstraints { (make) -> Void in
//                make.left.equalTo(subView).offset(0)
//
//        }
        //textV.snp_makeConstraints { (make) -> Void in
//                make.right.equalTo(self.contentView).offset(0)
//                make.top.equalTo(self.contentView).offset(0)
//                make.left.equalTo(self.contentView).offset(0)
//                make.bottom.equalTo(self.contentView).offset(0)
//
//
//        }


    }




    func setUpConstraints() {

        var viewsDict = Dictionary <String, UIView>()
        viewsDict["title"] = title
        title.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView).offset(0)
            make.left.equalTo(self.contentView).offset(16)
            make.bottom.equalTo(self.contentView).offset(0)
            make.right.equalTo(self.contentView).offset(0)


        }


    }



    func setUpViews() {
       title.text = data.breakdownTitle
        title.font = title.font.fontWithSize(10)

    }


    override var layoutMargins: UIEdgeInsets {
        get { return UIEdgeInsetsZero }
        set(newVal) {}
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}



class userData {
    
    var name, email: String;
    var joinedLobbies = Array<Lobbies>();
    var facebookData: NSObject;
    var twitterData: NSObject;
    var systems = Array<String>();
    init() {
        name = "Alex";
        email = "hartwellalex@gmail.com";
        facebookData = NSObject();
        twitterData = NSObject();
        systems = ["PS4", "PC", "XBOX360"];
        
    }
    
    
    
}





class GameList {  //collection of all the current games
    var games: Array<lobbyData>;




    init() {

        games = Array<lobbyData>();
        for i in 0...15 {

            games.append(lobbyData());
            if (i==0 || i==4 || i==10) {
                games[i].isBreakdown = true;
                if (i==4) {
                    games[i].breakdownTitle = "Tomorrow (5)"
                } else if (i==10) {
                    games[i].breakdownTitle = "Later This Week (50)"
                }

            }

        }


    }


    func sortByTime() {


    }
    
    
}


class Lobbies {
    
}

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


struct lobbyData {
    var GameId = "5553c9f460635b5368e5a1d8"
    var Name = "Destiny"
    var PictureUrl = "http://upload.wikimedia.org/wikipedia/en/thumb/b/be/Destiny_box_art.png/220px-Destiny_box_art.png"
    var ThumbnailPictureUrl = "http://upload.wikimedia.org/wikipedia/en/thumb/b/be/Destiny_box_art.png/220px-Destiny_box_art.png"
    var Description = "The combat in this game was the one time in my life I ever became legitimately engraged playing a videogame. Need help!"
    var Platform = "PS3"
    var PlayStyle = "Normal"
    var SkillLevel = "Intermediate"
    var startTimeUtc = "2015-05-13T22:24:41.000Z"
    var isBreakdown = false
    var breakdownTitle = "Happening Soon (2)"
}

struct breakdownData {

    var isBreakdown = true
    var breakdownTitle = "Happening Soon (2)"

}