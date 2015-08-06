//
// Created by Alex Hartwell on 6/17/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import JLToast

class MyChatsController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, MainScreenAnimationDelegate {

    var myChatsView: MyChatsView?
    var data = MyChatsData();
    var pullToRefresh = UIRefreshControl();

    var empty = false;

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties
        data.removeCellAtIndex = self.removeCellAtIndex;
        data.getMyLobbies(false, success: {
            println("success!!!!!")
        }, failure: {
            println("couldn't get lobbies")
        })

        data.passOffAmount = self.changeTitleUnreadAmount;
    }

    override func loadView() {
        println("loading my chats view view!")
        myChatsView = MyChatsView()
        self.view = myChatsView!
        myChatsView?.setUpView(self)
        self.myChatsView?.chatsCollection!.performBatchUpdates({
            self.myChatsView?.chatsCollection!.reloadSections(NSIndexSet(index: 0));
        }, completion: nil);


//        self.myChatsView?.chatsCollection?.reloadData();


//        data.getMyLobbies({
//            println("success!!!!!")
//            self.myChatsView?.chatsCollection?.reloadData();
//        }, failure: {
//            Error(alertTitle: "Couldn't get your chats", alertText: "Sorry, something went wrong. Try again!!!!!!");
//        })

    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated);

        data.getMyLobbies(true, success: {
            println("success!!!!!")

            self.myChatsView?.chatsCollection?.reloadData();
        }, failure: {
            Error(alertTitle: "Couldn't get your chats", alertText: "Sorry, something went wrong. Try again!!!!!!");
        })

        self.title = "My Games";

        self.navigationController?.navigationBar.translucent = false
        self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
        self.navigationController?.navigationBar.shadowImage = nil
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // currentUser.setPage("Find A Game");
        //self.title = "My Games";


        let menuImage = UIImage(named: "hamburgerMenu")

        self.navigationController?.navigationBar.translucent = false
        self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
        self.navigationController?.navigationBar.shadowImage = nil

//        var rightSwipe = UISwipeGestureRecognizer(target: self, action: "swipedRight");
//        rightSwipe.direction = UISwipeGestureRecognizerDirection.Right;
//        self.myChatsView!.addGestureRecognizer(rightSwipe);

        self.pullToRefresh.backgroundColor = UIColor(rgba: colors.tealMain);
        self.pullToRefresh.tintColor = UIColor.whiteColor()
        self.pullToRefresh.addTarget(self, action: "refreshTable", forControlEvents: UIControlEvents.ValueChanged);
        self.myChatsView?.chatsCollection?.addSubview(self.pullToRefresh);

    }


    func removeCellAtIndex(ind: Int) {
        println(ind);
        var indexPath = NSIndexPath(forItem: ind, inSection: 0);

        self.myChatsView?.chatsCollection?.deleteItemsAtIndexPaths([indexPath]);

    }

    func changeTitleUnreadAmount(amount: Int) {
      println("alert about new messages")
        if (amount == 0) {
            //nav!.tabBar.items[1].
        }
    }

    func refreshTable() {
        println("refresh tabled!!!!")

        self.data.getMyLobbies(true, success: {
            self.myChatsView?.chatsCollection?.reloadData();
            self.myChatsView?.chatsCollection!.performBatchUpdates({
                self.myChatsView?.chatsCollection!.reloadSections(NSIndexSet(index: 0));

            }, completion: nil);
            self.pullToRefresh.endRefreshing();

            //self.pullToRefresh.endRefreshing();
        }, failure: {
            Error(alertTitle: "Couldn't Refresh The List", alertText: "Sorry about that...");
            self.pullToRefresh.endRefreshing();
        })
    }

    func swipedRight() {
        var currentPageIndex = nav!.selectedIndex;

        currentPageIndex--;
        nav!.selectedIndex = currentPageIndex;
        nav!.selectedViewController!.viewDidAppear(true)

    }

    func leaveLobby(id: String?, name: String?) {
        if (id != nil) {
            self.data.removeLobby(id!, success: {
                () -> Void in
//                println("left lobby");
                JLToast.makeText("Left " + name!)
               // self.myChatsView?.chatsCollection?.reloadData();
                self.myChatsView?.chatsCollection!.performBatchUpdates({
                    self.myChatsView?.chatsCollection!.reloadSections(NSIndexSet(index: 0));
                }, completion: nil);
            }, failure: {
                Error(alertTitle: "Couldn't leave game", alertText: "You will never get out!")
            })
        }
    }

    func scrollViewDidScroll(scrollView: UIScrollView!) {

        if (scrollView.contentOffset.y > (scrollView.contentSize.height - scrollView.frame.height - 100)) {
            data.getMoreLobbies({
                self.myChatsView?.chatsCollection!.reloadData()
            }, failure: {
                println("failed to get more lobbies")
            })
        }

    }



    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        var numberOfCells = 0;
        if (data.lobbies.count == 0) {
            empty = true;
            numberOfCells = 1;
        } else {
            empty = false;
            numberOfCells = data.lobbies.count;
        }
        return numberOfCells;
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        if (empty) {

            //replcae with empty cell;emptyCell
            let cell = collectionView.dequeueReusableCellWithReuseIdentifier("emptyCell", forIndexPath: indexPath) as! EmptyCell
            cell.frame.size = CGSize(width: self.myChatsView!.frame.size.width, height: 500)
            cell.frame.origin.x = 0;

            cell.layer.shouldRasterize = true;
            cell.layer.rasterizationScale = UIScreen.mainScreen().scale;
            //cell.frame = CGRect(x: 0, y: 0, width: self.view.frame.size.width, height: 45);
            return cell
        } else {
            let cell = collectionView.dequeueReusableCellWithReuseIdentifier("myChatCell", forIndexPath: indexPath) as! MyChatsCell
            cell.frame.size = CGSize(width: self.myChatsView!.frame.size.width, height: 90)
            cell.frame.origin.x = 0;
            cell.setUpCell(data.lobbies[indexPath.row])
            cell.layer.shouldRasterize = true;
            cell.layer.rasterizationScale = UIScreen.mainScreen().scale;
            cell.removeCellAction = self.leaveLobby;
            //cell.frame = CGRect(x: 0, y: 0, width: self.view.frame.size.width, height: 45);
            return cell
        }
    }
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        if (empty == false) {
            println(data.lobbies[indexPath.row])
            var selectedCell = collectionView.cellForItemAtIndexPath(indexPath) as? MyChatsCell;
            if (selectedCell!.movedLeft == false) {
                self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .Plain, target: nil, action: nil)
                let lobbyView = SingleLobbyController(info: self.data.lobbies[indexPath.row])
                lobbyView.setAnimationDelegate(self as MainScreenAnimationDelegate)
//              var timer = NSTimer.scheduledTimerWithTimeInterval(0.098, target: self, selector: Selector("pushLobby:"), userInfo: lobbyView, repeats: false);


                self.definesPresentationContext = true;
                var overlayNav = UINavigationController(rootViewController: lobbyView);
                overlayNav.modalPresentationStyle = UIModalPresentationStyle.OverCurrentContext;
                self.navigationController?.presentViewController(overlayNav, animated: false, completion: nil);

               // animateLobbyCellsAway(nil)


            }
        }

    }



    func animateLobbyCellsAway(success: (() -> Void)?) {
        var cells = self.myChatsView!.chatsCollection!.indexPathsForVisibleItems() as! [NSIndexPath];
        cells = cells.sorted {$0.row < $1.row};
        var collection = self.myChatsView!.chatsCollection!;
        var speed  = 0.45;
        for (var i = 0; i<cells.count; i++) {
            if ((collection.cellForItemAtIndexPath(cells[i] as! NSIndexPath) as? MyChatsCell) != nil) {
                var theCell = collection.cellForItemAtIndexPath(cells[i] as! NSIndexPath) as! MyChatsCell;

                theCell.moveLeft(speed, success: nil);

                speed += 0.2;
                println(theCell);
            }
        }

    }

    func bringLobbiesBack() {
        var cells = self.myChatsView!.chatsCollection!.indexPathsForVisibleItems() as! [NSIndexPath];
        cells = cells.sorted {$0.row < $1.row};
        var collection = self.myChatsView!.chatsCollection!;
        var speed  = 1.2;
        for (var i = 0; i<cells.count; i++) {
            if ((collection.cellForItemAtIndexPath(cells[i] as! NSIndexPath) as? MyChatsCell) != nil) {
                var theCell = collection.cellForItemAtIndexPath(cells[i] as! NSIndexPath) as! MyChatsCell;

                theCell.moveRight(speed, success: nil);

                speed -= 0.2;
                println(theCell);
            }
        }
    }


    func pushLobby(timer: NSTimer) {
        self.navigationController?.pushViewController(timer.userInfo as! SingleLobbyController, animated: true);

    }


}