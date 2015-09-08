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

    var pullToRefresh: UIRefreshControl = UIRefreshControl();

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

        model.getLobbies("", platforms: [], applyChange: true, success: self.updateData, failure: {
            println("failed...")
            self.model.justStarted = false;
            self.listView?.table.reloadData();
            self.activityIndicator.stopAnimating();

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
            SNYError(alertTitle: "Couldn't Refresh The List", alertText: "Sorry about that...", networkRequest: true);
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
        if (scrollView.contentOffset.y > 20) {
        if (self.pullToRefresh.refreshing) {
            self.pullToRefresh.endRefreshing();
        }
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


    func setTitleOfPage(search: String, platforms: Array<String>) {
        var title = "";
        
        if (search != "") {
            title = search;
            
            println(title);
        } else if (platforms.count == 0) {
            title = "All Games";
            println(title);

        } else {
            if (platforms.count == 1) {
                title = platforms[0];
            println(title);

            } else {
                for (var i = 0; i<platforms.count; i++) {
                    if (i == platforms.count - 1) {
                        title += platforms[i];
                    } else {
                        title += platforms[i] + ", ";
                    }
                    println(title);

                }
                
                
            }
            
        }
        self.title = title;
    }
    

    //on a filter change/search  //new results
    func loadNewLobbies(search: String, platforms: Array<String>) {
//        println(search)
//        println(platforms)
        println("LOAD NEW LOBBIES");
        model.getLobbies(search, platforms: platforms, applyChange: true, success: {

            self.setTitleOfPage(search, platforms: platforms);
            println("^^^ PAGE TITLE ^^^^^");


            self.animateLobbyCellsAway({
                var trans = CGAffineTransformMakeTranslation(0, 0);
                self.navigationController?.navigationBar.transform = trans;

            self.setTitleOfPage(search, platforms: platforms);

                self.updateData();

            }, animateNavBar: false);


        }, failure: {
            println("failed...")
            self.activityIndicator.stopAnimating();

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
        if (lobbyCount() > 0 || self.model.justStarted == true) {
            return 5;
        } else {
            return 1;
        }
    }


   

    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        
        let cell = headerCell(frame: CGRectMake(0,0,UIScreen.mainScreen().bounds.width, 0));


            cell.setCell("    " + self.model.gamesKey[section] + " (\(self.model.gamesOrganized[self.model.gamesKey[section]]!.count))");
        
        return cell
    }
    
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        
        return 130.0;
        
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (self.lobbyCount() > 0 || self.model.justStarted == true) {
            return self.model.gamesOrganized[self.model.gamesKey[section]]!.count;
        } else {
            return 1;
        }

    }
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {

        if (self.lobbyCount() > 0 || self.model.justStarted == true) {

            let cell = tableView.dequeueReusableCellWithIdentifier("gamecell", forIndexPath: indexPath) as! gameCell
            if (cell.isNew) {

                cell.setCell(self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.item])
                cell.setUpConstraints()
            }

            cell.setUpViews(self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.item])
            cell.removeOffset(0);
            return cell
        } else {

            let cell = NoLobbiesCell();
            cell.setUpCell();
            return cell;

        }



    }

    func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        var visibleRowsIndexPath = tableView.indexPathsForVisibleRows()!;
        var lastIndex = visibleRowsIndexPath[visibleRowsIndexPath.count-1] as! NSIndexPath;

        if (indexPath.row == lastIndex.row) {
            self.model.removeAnimate();
            self.activityIndicator.stopAnimating();
        }
        else if (indexPath.row == (visibleRowsIndexPath[0] as! NSIndexPath).row) {
            println("first one!!!!");
            
            self.model.setAnimationDelayOrder(self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.item].index - 1);
        }

    }



 

  func tableView(tlableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
      if (self.lobbyCount() > 0 || self.model.justStarted == true) {
          if (self.model.gamesOrganized[self.model.gamesKey[section]]!.count == 0) {
              return 0;
          } else {
              return 35;
          }
      } else {
          return 0;
      }

    }


    
    func getAllCellsAndHeaders() -> [AnyObject] {
        var visibleRows = self.listView?.table.indexPathsForVisibleRows();
        var indexCount: Int = 0;
        var allCells: [AnyObject] = [];
        for (var i = 0; i<visibleRows!.count; i++) {
            var row = visibleRows![i] as! NSIndexPath;
            println(row);
            println(row.section);
            println(row.row);
            if (row.row == 0 || indexCount == 0) {
                allCells.append(self.listView!.table.headerViewForSection(row.section)!);
                indexCount++;
            }
            
            var cell = self.listView!.table.cellForRowAtIndexPath(row) as? gameCell
            if (cell != nil) {
                allCells.append(cell!);
                indexCount++;
            }
        }
        
        return allCells;
    }
    

    func bringLobbiesBack() {
        
        var trans = CGAffineTransformMakeTranslation(0, 0);
        UIView.animateWithDuration(0.75, animations: {
            self.navigationController?.navigationBar.transform = trans;
            
        });
        var cells = self.listView?.table.visibleCells();
        var allCells = getAllCellsAndHeaders();
        var speed  = Double(allCells.count) * 0.08 + 0.15;
        for (var i = 0; i<allCells.count; i++) {
            if ((allCells[i] as? gameCell) != nil) {
                var theCell = allCells[i] as! gameCell;
                theCell.removeOffset(speed - 0.08 * Double(i));
            } else {
                var theCell = allCells[i] as! headerCell;
                theCell.removeOffset(speed - 0.08*Double(i));
            }
        }
    }

    func animateLobbyCellsAway(success: (() -> Void)?, animateNavBar: Bool) -> Double {
        var cells = self.listView?.table.visibleCells();

        if (animateNavBar) {
            var trans = CGAffineTransformMakeTranslation(-UIScreen.mainScreen().bounds.width, 0);
            UIView.animateWithDuration(0.2, animations: {
                self.navigationController?.navigationBar.transform = trans;
            });
        }
        
        var allCells = getAllCellsAndHeaders();
        var speed  = 0.25;
        for (var i = 0; i<allCells.count; i++) {
            if ((allCells[i] as? UITableViewCell) != nil) {
                var theCell = allCells[i] as! gameCell;

                if (i == allCells.count-1) {
                    theCell.moveLeft(speed, success: {
                        success?();
                    });
                } else {
                    theCell.moveLeft(speed, success: nil);
                }
                speed += 0.1;
                println(theCell);
            } else {
                
                var theCell = allCells[i] as! headerCell;

                if (i == allCells.count-1) {
                    theCell.moveLeft(speed, success: {
                        success?();
                    });
                } else {
                    theCell.moveLeft(speed, success: nil);
                }
            }
        }

        return speed;

    }

    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        println("You selected cell #\(indexPath.row)!")
        var selectedCell = tableView.cellForRowAtIndexPath(indexPath) as? gameCell;

        let lobbyView = SingleLobbyController(info: self.model.gamesOrganized[self.model.gamesKey[indexPath.section]]![indexPath.row])
        lobbyView.setAnimationDelegate(self as! MainScreenAnimationDelegate);

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



