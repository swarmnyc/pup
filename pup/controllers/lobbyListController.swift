//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class LobbyListController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    var listView: LobbyListView = LobbyListView()


    var table:UITableView!

    var filter: FilterViewController!

    var sideMenu: SideMenuController!

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

    override func loadView() {
        println("ahhh loading view!")
        self.view = listView
        listView.setDelegates(self,dataSource: self)

        println(listView.table)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        currentUser.setPage("Find A Game");
        self.title = "All Games";
        self.navigationController?.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName: UIColor(rgba: colors.tealMain)]
        self.navigationController?.navigationBar.tintColor = UIColor(rgba: colors.tealMain)
        let filterImage = UIImage(named: "filter")

        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: filterImage, style: UIBarButtonItemStyle.Plain, target: self, action: "openFilter")

        let menuImage = UIImage(named: "hamburgerMenu")

        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: menuImage, style: UIBarButtonItemStyle.Plain, target: self, action: "openMenu")



        filter = FilterViewController(parentController: self);
        sideMenu = SideMenuController(parentController: self)
        self.listView.swipeDelegate = sideMenu;



    }


    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }




    func updateData() {
        listView.table.reloadData()
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


        println(self.listOfGames.games[indexPath.row])
        if (self.listOfGames.games[indexPath.row].isBreakdown == false) {
            let lobbyView = SingleLobbyController(info: self.listOfGames.games[indexPath.row])
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

    func openMenu() {
        println("openMenu");
        sideMenu.toggleState()
    }




}



