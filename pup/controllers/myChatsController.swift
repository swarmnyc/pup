//
// Created by Alex Hartwell on 6/17/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class MyChatsController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource {

    var myChatsView: MyChatsView?
    var data = MyChatsData();
    var pullToRefresh = UIRefreshControl();

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

        data.getMyLobbies({
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
        self.myChatsView?.chatsCollection?.reloadData();
//        data.getMyLobbies({
//            println("success!!!!!")
//            self.myChatsView?.chatsCollection?.reloadData();
//        }, failure: {
//            Error(alertTitle: "Couldn't get your chats", alertText: "Sorry, something went wrong. Try again!!!!!!");
//        })

    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated);

        data.getMyLobbies({
            println("success!!!!!")
            self.myChatsView?.chatsCollection?.reloadData();
        }, failure: {
            Error(alertTitle: "Couldn't get your chats", alertText: "Sorry, something went wrong. Try again!!!!!!");
        })

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

    func changeTitleUnreadAmount(amount: Int) {
        println("change title!!!!")
        self.title = "My Games (" + String(amount) + ")";
    }

    func refreshTable() {
        println("refresh tabled!!!!")

        self.data.getMyLobbies({
            self.myChatsView?.chatsCollection?.reloadData();
            self.pullToRefresh.endRefreshing();
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
        println(data.lobbies.count)
        return data.lobbies.count
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier("myChatCell", forIndexPath: indexPath) as! MyChatsCell
        cell.frame.size = CGSize(width: self.myChatsView!.frame.size.width, height: 90)
        cell.frame.origin.x = 0;
        cell.setUpCell(data.lobbies[indexPath.row])
        cell.layer.shouldRasterize = true;
        cell.layer.rasterizationScale = UIScreen.mainScreen().scale;

        //cell.frame = CGRect(x: 0, y: 0, width: self.view.frame.size.width, height: 45);
        return cell
    }
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {

        println(data.lobbies[indexPath.row])

        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .Plain, target: nil, action: nil)

        let lobbyView = SingleLobbyController(info: self.data.lobbies[indexPath.row])
        self.navigationController?.pushViewController(lobbyView, animated: true)


    }

}