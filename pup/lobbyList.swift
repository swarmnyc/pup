//
//  lobbyList.swift
//  pup
//
//  Created by Alex Hartwell on 5/18/15.
//  Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class lobbyListController: UIViewController, UITableViewDelegate, UITableViewDataSource {
   
    var findAGame: UIViewController!
    
    var table:UITableView!

    var filter: filterView!

    var updateTimer: NSTimer = NSTimer();
    lazy var listOfGames: lobbyList = lobbyList(parentView: self);


    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }


    override func viewDidLoad() {
        super.viewDidLoad()


        self.view.backgroundColor=UIColor.whiteColor()
        self.view.clipsToBounds = true;
        setUpTableStyle();
        setUpTableConstraints();

        filter = filterView(parentview: self.view);





    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }



    func setUpTableStyle() {
        table = UITableView();
        table.separatorInset = UIEdgeInsetsZero
        self.title = "All Games";
        self.navigationController?.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName: UIColor(rgba: colors.tealMain)]
        self.navigationController?.navigationBar.tintColor = UIColor(rgba: colors.tealMain)
        let image = UIImage(named: "filter")
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: image, style: UIBarButtonItemStyle.Plain, target: self, action: "openFilter")




        table.delegate = self;
        table.dataSource = self;

        self.view.addSubview(table);


    }





    func setUpTableConstraints() {
        table.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self.view).offset(0)
            make.left.equalTo(self.view).offset(0)
            make.top.equalTo(self.view).offset(0)
            make.bottom.equalTo(self.view).offset(0)
        }
    }


    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        println("count \(self.listOfGames.games.count)")
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
       var selectedCell = tableView.cellForRowAtIndexPath(indexPath) as? gameCell;


       println(selectedCell?.data)
       if (selectedCell?.data.isBreakdown == false) {
           let lobbyView = SingleLobbyController(info: selectedCell!.data)
           self.navigationController?.pushViewController(lobbyView, animated: true)
       }
    }

//    func tableView(tableView: UITableView, didUnhighlightRowAtIndexPath indexPath: NSIndexPath) {
//        var cell:gameCell = tableView.cellForRowAtIndexPath(indexPath) as! gameCell;
//        cell.highlightCell()
//
//    }
    
    
    func openFilter() {
        println("opening it")
        filter.toggleState()



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
            make.height.equalTo(18)

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





    override func setHighlighted(highlighted: Bool, animated: Bool) {
      if (!highlighted) {
          self.contentView.backgroundColor = UIColor.whiteColor()
      } else {
          self.contentView.backgroundColor = UIColor(white: 0.0, alpha: 0.5)
      }


    }

    func setUpViews() {

        selectionStyle = UITableViewCellSelectionStyle.None

        var url = NSURL(string: data.PictureUrl)

        var request:NSURLRequest = NSURLRequest(URL: url!)
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue(), completionHandler: {(response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            self.img = UIImage(data: data) as UIImage!
            self.imgView.image = self.img;
            self.imgView.contentMode = UIViewContentMode.ScaleAspectFill;
            self.imgView.clipsToBounds = true;
        })


        platform.text = data.Platform;
        platform.textAlignment = NSTextAlignment.Center
        platform.font = platform.font.fontWithSize(9)
        platform.backgroundColor = UIColor.clearColor()


        title.text = data.Name
        title.setTranslatesAutoresizingMaskIntoConstraints(false)
        title.font = title.font.fontWithSize(16)
        title.layoutMargins = UIEdgeInsetsZero


        desc.text = data.Description
        self.desc.font = UIFont.systemFontOfSize(11.0)
        desc.editable = false
        desc.scrollEnabled = false
        desc.textContainerInset = UIEdgeInsetsZero
        desc.textContainer.lineFragmentPadding = 0
        desc.setTranslatesAutoresizingMaskIntoConstraints(false)
        desc.backgroundColor = UIColor.clearColor()

        tags.text = "\(data.PlayStyle) > \(data.SkillLevel)"
        tags.font = tags.font.fontWithSize(9)

        time.text = data.timeInHuman
        time.font = time.font.fontWithSize(9)

        time.textColor = UIColor(rgba: colors.tealMain)


        divider.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.2)

    }


    override var layoutMargins: UIEdgeInsets {  //make cell dividers extend full width
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





