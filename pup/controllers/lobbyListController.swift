//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore


class LobbyListController: UIViewController, UITableViewDelegate, UITableViewDataSource, FABDelegate, MainScreenAnimationDelegate {

    var listView: LobbyListView? //custom view for lobby list

    var filter: FilterViewController! //controller for the filter

    var pullToRefresh = UIRefreshControl();

    var parentController: UIViewController?

    lazy var model: LobbyList = LobbyList(parentView: self);  //model

    var tutorial: TutorialController?;

    var activityIndicator: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .Gray)


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


    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(animated);

        println("willDisappear!!!!");
        println(self.navigationController?.viewControllers);
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated);
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

        var barButton = UIBarButtonItem(customView: activityIndicator);
        self.navigationItem.leftBarButtonItem = barButton;
        self.activityIndicator.startAnimating();

        let filterImage = UIImage(named: "filter")
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(image: filterImage, style: UIBarButtonItemStyle.Plain, target: self, action: "openFilter")

        //makes it so that inside of a lobby their is no title on the back button
        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .Plain, target: nil, action: nil)



        //register the cell classes so we can reuse them
        self.listView?.table.registerClass(gameCell.self, forCellReuseIdentifier: "gamecell")
        self.listView?.table.registerClass(headerCell.self, forCellReuseIdentifier: "headercell")

        model.getLobbies("", platforms: [], applyChange: true, success: self.updateData, failure: {
            println("failed...")
        })

        self.pullToRefresh.backgroundColor = UIColor(rgba: colors.tealMain);
        self.pullToRefresh.tintColor = UIColor.whiteColor()
        self.pullToRefresh.addTarget(self, action: "refreshTable", forControlEvents: UIControlEvents.ValueChanged);
        self.listView?.table.addSubview(self.pullToRefresh);
        self.listView?.table.setContentOffset(CGPointMake(0, -self.pullToRefresh.frame.size.height), animated: false);


        filter = FilterViewController(parentController: self);
        self.listView?.bringSubviewToFront(filter.filterView);

        if (currentUser.loggedIn() != true) {
            tutorial = TutorialController(transitionStyle: UIPageViewControllerTransitionStyle.Scroll, navigationOrientation:UIPageViewControllerNavigationOrientation.Horizontal, options:nil);
            tutorial?.delegate = tutorial;
            tutorial?.dataSource = tutorial;
            self.navigationController?.presentViewController(tutorial!, animated: true, completion: nil)
        }


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
           model.getMoreResults({
                   self.updateData();
           });
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



    //on a filter change/search  //new results
    func loadNewLobbies(search: String, platforms: Array<String>) {

//        println(search)
//        println(platforms)
        model.getLobbies(search, platforms: platforms, applyChange: true, success: {

            self.animateLobbyCellsAway({
                self.updateData();

            });
        }, failure: {
            println("failed...")
        })

    }

    func updateData() {
        self.pullToRefresh.endRefreshing();
        activityIndicator.stopAnimating();
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
        title.backgroundColor = UIColor(rgba: colors.lightGray)

        title.text = "    " + self.model.gamesKey[section] + " (\(self.model.gamesOrganized[self.model.gamesKey[section]]!.count))";
        title.font = UIFont(name: "AvenirNext-Medium", size: 11.0)
        title.textColor = UIColor.blackColor().lighterColor(0.6);
        title.layer.frame.size = CGSizeMake(UIScreen.mainScreen().bounds.width * 1.5, 35);
        title.layer.bounds.size = CGSizeMake(UIScreen.mainScreen().bounds.width * 1.5, 25);
        title.layer.bounds.origin = CGPointMake(0.5 * UIScreen.mainScreen().bounds.width, 12.5);
        title.layer.frame.origin = CGPointMake(0.5 * UIScreen.mainScreen().bounds.width, 12.5)
        title.layer.borderWidth = 0.5;
        title.layer.borderColor = UIColor.clearColor().CGColor;

        return title
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

        return self.model.gamesOrganized[self.model.gamesKey[section]]!.count;

    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {


            let cell = tableView.dequeueReusableCellWithIdentifier("gamecell", forIndexPath:indexPath) as! gameCell
            if (cell.isNew) {

                cell.setCell(self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.item])
                cell.setUpConstraints()
            }

            cell.setUpViews(self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.item])
            cell.removeOffset(0);
            return cell



    }

    func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        var visibleRowsIndexPath = tableView.indexPathsForVisibleRows()!;
        var lastIndex = visibleRowsIndexPath[visibleRowsIndexPath.count-1] as! NSIndexPath;

        if (indexPath.row == lastIndex.row) {
            self.model.removeAnimate();
            self.activityIndicator.stopAnimating();
        }

    }



    func tableView(tlableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {

            return 119.0;

    }

  func tableView(tlableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
            if (self.model.gamesOrganized[self.model.gamesKey[section]]!.count == 0) {
                return 0;
            } else {
                return 35;
            }

    }



    func bringLobbiesBack() {
        var cells = self.listView?.table.visibleCells();

        var speed  = 1.1;
        for (var i = 0; i<cells!.count; i++) {
            if ((cells![i] as? gameCell) != nil) {
                var theCell = cells![i] as! gameCell;
                theCell.removeOffset(speed - 0.2 * Double(i));
            }
        }
    }

    func animateLobbyCellsAway(success: (() -> Void)?) {
        var cells = self.listView?.table.visibleCells();

        var speed  = 0.45;
        for (var i = 0; i<cells!.count; i++) {
            println(speed);
            if ((cells![i] as? UITableViewCell) != nil) {
                var theCell = cells![i] as! gameCell;

                if (i == cells!.count-1) {
                    theCell.moveLeft(speed, success: {
                        success?();
                        //self.navigationController?.pushViewController(lobbyView, animated: true);
                    });
                } else {
                    theCell.moveLeft(speed, success: nil);
                }
                speed += 0.2;
                println(theCell);
            }
        }

    }

    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
        var selectedCell = tableView.cellForRowAtIndexPath(indexPath) as? gameCell;
        //self.hidesBottomBarWhenPushed = true;
        let lobbyView = SingleLobbyController(info: self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.row])
        lobbyView.setAnimationDelegate(self as! MainScreenAnimationDelegate);
//        var timer = NSTimer.scheduledTimerWithTimeInterval(0.02, target: self, selector: Selector("pushLobby:"), userInfo: lobbyView, repeats: false);



        self.definesPresentationContext = true;
        self.definesPresentationContext = true;
        var overlayNav = UINavigationController(rootViewController: lobbyView);
        overlayNav.modalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;
        dispatch_async(dispatch_get_main_queue()) {
            self.navigationController?.presentViewController(overlayNav, animated: false, completion: nil);

        };
        //self.navigationController?.pushViewController(lobbyView, animated: true);

//
        // self.navigationController?.pushViewController(lobbyView, animated: true);

    }

    func pushLobby(timer: NSTimer) {
        //self.navigationController?.pushViewController(timer.userInfo as! SingleLobbyController, animated: false);
//        self.navigationController?.modalPresentationStyle = UIModalPresentationStyle.CurrentContext;
        self.definesPresentationContext = true;
        self.definesPresentationContext = true;
        var overlayNav = UINavigationController(rootViewController: timer.userInfo as! SingleLobbyController);
        overlayNav.modalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;
        self.navigationController?.presentViewController(overlayNav, animated: false, completion: nil);
        timer.invalidate();
    }

    func openFilter() {
        println("opening it")
        filter.toggleState()

    }






}



