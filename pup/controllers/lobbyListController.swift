//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class LobbyListController: UIViewController, UITableViewDelegate, UITableViewDataSource, OverlayDelegate, FABDelegate {

    var listView: LobbyListView? //custom view for lobby list
    var table:UITableView! //the table view (may be replaced by collection view)
    var filter: FilterViewController! //controller for the filter

    var sideMenu: SideMenuController! //control for the menu

    var updateTimer: NSTimer = NSTimer();  //timer to check for updated data

    lazy var model: lobbyList = lobbyList(parentView: self);  //model

    var transitionManager = TransitionManager()

    convenience init() {
        self.init();
    }

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
        listView = LobbyListView()
        self.view = listView
        listView?.setDelegates(self,dataSource: self, fabDelegate: self, overlayDelegate: self)

        println(listView?.table)
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



        filter = FilterViewController(parentController: self, overlayDelegate: self as OverlayDelegate);
        sideMenu = SideMenuController(parentController: self, overlayDelegate: self as OverlayDelegate)
        self.listView?.swipeDelegate = sideMenu;



    }


    func touchDown() {
        println("woooh")
        listView?.pushFab()

    }
    func touchUp() {
        print("hooooo")
        listView?.releaseFab()
        let createLobby = CreateLobbyController()
        self.navigationController?.pushViewController(createLobby, animated: true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


    func darkenOverlay() {
        self.listView?.darkenOverlay();
    }

    func hideOverlay() {
        self.listView?.hideOverlay();
    }

    func hideEverything() {
        filter?.closeFilter();
        sideMenu?.closeMenu();
        hideOverlay()
    }

    func loadNewLobbies(search: String, platforms: Array<String>) {

        println(search)
        println(platforms)
        model.makeNewRequest(search, platforms: platforms)

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
            let cell: gameCell = gameCell();
            cell.setCell(self.model.games[indexPath.row])
            return cell
        } else {
            let cell: headerCell = headerCell();
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



