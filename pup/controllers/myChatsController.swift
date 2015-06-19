//
// Created by Alex Hartwell on 6/17/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class MyChatsController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource {

    var myChatsView: MyChatsView?
    var data = MyChatsData();

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }

    override func loadView() {
        println("loading my chats view view!")
        myChatsView = MyChatsView()
        self.view = myChatsView!
        myChatsView?.setUpView(self)

        data.getMyLobbyies({
            println("success!!!!!")
            self.myChatsView?.chatsCollection?.reloadData();
        }, failure: {
            Error(alertTitle: "Couldn't get your chats", alertText: "Sorry, something went wrong. Try again!!!!!!");
        })

    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // currentUser.setPage("Find A Game");
        self.title = "My Chats";


        let menuImage = UIImage(named: "hamburgerMenu")
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: menuImage, style: UIBarButtonItemStyle.Plain, target: navigationController, action: "toggleSideMenu")

        self.navigationController?.navigationBar.translucent = false
        self.navigationController?.navigationBar.setBackgroundImage(nil, forBarMetrics: UIBarMetrics.Default)
        self.navigationController?.navigationBar.shadowImage = nil

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