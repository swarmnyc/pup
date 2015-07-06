//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class LobbyListController: UIViewController, UITableViewDelegate, UITableViewDataSource, FABDelegate {

    var listView: LobbyListView? //custom view for lobby list

    var filter: FilterViewController! //controller for the filter

    var pullToRefresh = UIRefreshControl();

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




    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)


        self.navigationController?.navigationBar.translucent = false;
        self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
        self.navigationController?.navigationBar.shadowImage = nil


    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBar.translucent = false

        currentUser.setPage("Find A Game");
        self.title = "All Games";



        let filterImage = UIImage(named: "filter")
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: filterImage, style: UIBarButtonItemStyle.Plain, target: self, action: "openFilter")

        //makes it so that inside of a lobby their is no title on the back button
        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .Plain, target: nil, action: nil)



        //register the cell classes so we can reuse them
        self.listView?.table.registerClass(gameCell.self, forCellReuseIdentifier: "gamecell")
        self.listView?.table.registerClass(headerCell.self, forCellReuseIdentifier: "headercell")

        //load the filter and the side menu
        filter = FilterViewController(parentController: self);

        model.getLobbies("", platforms: [], applyChange: true, success: self.updateData, failure: {
            println("failed...")
        })



        self.pullToRefresh.backgroundColor = UIColor(rgba: colors.tealMain);
        self.pullToRefresh.tintColor = UIColor.whiteColor()
        self.pullToRefresh.addTarget(self, action: "refreshTable", forControlEvents: UIControlEvents.ValueChanged);
        self.listView?.table.addSubview(self.pullToRefresh);
        self.listView?.table.setContentOffset(CGPointMake(0, -self.pullToRefresh.frame.size.height), animated: false);


    }

    func refreshTable() {
        println("refresh tabled!!!!")

        self.model.refreshRequest({
            self.updateData();
            self.pullToRefresh.endRefreshing();
        }, failure: {
            Error(alertTitle: "Couldn't Refresh The List", alertText: "Sorry about that...");
            self.pullToRefresh.endRefreshing();
        })
    }



    //onscroll
    //model.getMoreResults(self.updateData

    func scrollViewDidScroll(scrollView: UIScrollView!) {
        if (scrollView.contentOffset.y > (scrollView.contentSize.height - scrollView.frame.height - 100)) {
           model.getMoreResults(self.updateData);
        }



    }

    func fabTouchDown() {

//        listView?.pushFab()

    }
    func fabTouchUp() {

//        listView?.releaseFab()
//        let createLobby = CreateLobbyController()
//        self.navigationController?.pushViewController(createLobby, animated: true)
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
        self.pullToRefresh.endRefreshing();

        model.loadMore = true;
        listView?.table.reloadData()
    }


    func lobbyCount() -> Int {
        return self.model.gameCount
    }

    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 5;
    }


    func tableView(tableView: UITableView, titleForHeaderInSection section: Int) -> String {
        return self.model.gamesKey[section] + " (\(self.model.gamesOrganized[self.model.gamesKey[section]]!.count))";

    }

    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        var title: UILabel = UILabel()
        title.backgroundColor = UIColor.whiteColor();

        title.text = "    " + self.model.gamesKey[section] + " (\(self.model.gamesOrganized[self.model.gamesKey[section]]!.count))";
        title.font = title.font.fontWithSize(11)
        title.textColor = UIColor(rgba: colors.midGray)
        return title

    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

        return self.model.gamesOrganized[self.model.gamesKey[section]]!.count;

    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        //var cell:UITableViewCell = self.tableV.dequeueReusableCellWithIdentifier("cell") as UITableViewCell
        //cell.textLabel.text = self.items[indexPath.row]

            let cell = tableView.dequeueReusableCellWithIdentifier("gamecell", forIndexPath:indexPath) as! gameCell
            if (cell.isNew) {
                cell.setCell(self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.item])
                cell.setUpConstraints()
            }

            cell.setUpViews(self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.item])

            return cell



    }

    func tableView(tlableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {

            return 119.0;

    }

  func tableView(tlableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
            if (self.model.gamesOrganized[self.model.gamesKey[section]]!.count == 0) {
                return 0;
            } else {
                return 25;
            }

    }


    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
        var selectedCell = tableView.cellForRowAtIndexPath(indexPath) as? gameCell;

        let lobbyView = SingleLobbyController(info: self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.row])
        self.navigationController?.pushViewController(lobbyView, animated: true)

    }


    func openFilter() {
        println("opening it")
        filter.toggleState()

    }






}



