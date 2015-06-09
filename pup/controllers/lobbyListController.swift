//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class LobbyListController: UIViewController, UITableViewDelegate, UITableViewDataSource, FABDelegate {

    var listView: LobbyListView? //custom view for lobby list

    var filter: FilterViewController! //controller for the filter



    var parentController: UIViewController?

    lazy var model: LobbyList = LobbyList(parentView: self);  //model

   // var transitionManager = TransitionManager()


    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }

    override func loadView() {
        println("loading lobby list view!")
        listView = LobbyListView()
        self.view = listView
        listView?.setDelegates(self,dataSource: self, fabDelegate: self)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        currentUser.setPage("Find A Game");
        self.title = "All Games";



        let filterImage = UIImage(named: "filter")
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: filterImage, style: UIBarButtonItemStyle.Plain, target: self, action: "openFilter")


        let menuImage = UIImage(named: "hamburgerMenu")
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: menuImage, style: UIBarButtonItemStyle.Plain, target: navigationController, action: "toggleSideMenu")


        //register the cell classes so we can reuse them
        self.listView?.table.registerClass(gameCell.self, forCellReuseIdentifier: "gamecell")
        self.listView?.table.registerClass(headerCell.self, forCellReuseIdentifier: "headercell")

        //load the filter and the side menu
        filter = FilterViewController(parentController: self);

        model.getLobbies("", platforms: [], applyChange: true, success: self.updateData, failure: {
            println("failed...")
        })




    }



    func fabTouchDown() {

        listView?.pushFab()

    }
    func fabTouchUp() {

        listView?.releaseFab()
        let createLobby = CreateLobbyController()
        self.navigationController?.pushViewController(createLobby, animated: true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }




    func loadNewLobbies(search: String, platforms: Array<String>) {

//        println(search)
//        println(platforms)
        model.getLobbies(search, platforms: platforms, applyChange: true, success: self.updateData, failure: {
            println("failed...")
        })

    }

    func updateData() {
        listView?.table.reloadData()
    }


    func lobbyCount() -> Int {
        return self.model.games.count
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        println("count \(lobbyCount())")
        return lobbyCount();
    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        //var cell:UITableViewCell = self.tableV.dequeueReusableCellWithIdentifier("cell") as UITableViewCell
        //cell.textLabel.text = self.items[indexPath.row]
        if !self.model.games[indexPath.row].isBreakdown {

            let cell = tableView.dequeueReusableCellWithIdentifier("gamecell", forIndexPath:indexPath) as! gameCell

            cell.setCell(self.model.games[indexPath.row])
            return cell
        } else {

            var cell:headerCell = tableView.dequeueReusableCellWithIdentifier("headercell") as! headerCell
            cell.setCell(self.model.games[indexPath.row])
            return cell

        }


    }

    func tableView(tlableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        if !self.model.games[indexPath.row].isBreakdown {
            return 119.0;
        } else {
            return 26.0;
        }


    }


    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
        var selectedCell = tableView.cellForRowAtIndexPath(indexPath) as? gameCell;


        println(self.model.games[indexPath.row])
        if (self.model.games[indexPath.row].isBreakdown == false) {
            let lobbyView = SingleLobbyController(info: self.model.games[indexPath.row])

            self.navigationController?.pushViewController(lobbyView, animated: true)
        }
    }


    func openFilter() {
        println("opening it")
        filter.toggleState()

    }






}



