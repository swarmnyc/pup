//
// Created by Alex Hartwell on 6/18/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit


class MembersDrawerController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource {

    var users: Array<SingleLobbyUser>?
    var membersView: MembersDrawerView = MembersDrawerView()
    var navBar: UINavigationController?

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        // Here you can init your properties

    }

    func setNavigationBar(viewcontroller: UIViewController) {
        self.navBar = viewcontroller.navigationController!;
    }

    func populateUserList(users: Array<SingleLobbyUser>, owner: SingleLobbyUser) {
        self.users = users;
        self.users?.insert(owner, atIndex: 0);
        membersView.membersList!.reloadData();
    }

    func setUpSideBar() {
        membersView.setUpView(self, navBarController: self.navBar!);

    }

    func toggleDrawer() {
        self.membersView.toggle();
    }


    func removeViews() {
        self.membersView.removeViews();
    }


    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        println(users?.count)
        return users!.count
    }

    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {

        let cell = collectionView.dequeueReusableCellWithReuseIdentifier("MemberCell", forIndexPath: indexPath) as! MembersListCell
        cell.frame.size = CGSize(width: UIScreen.mainScreen().bounds.width * 0.9, height: 45)
        cell.frame.origin.x = 0;
        cell.setUpCell(self.users![indexPath.row])
        cell.layer.shouldRasterize = true;
        cell.layer.rasterizationScale = UIScreen.mainScreen().scale;
        //cell.frame = CGRect(x: 0, y: 0, width: self.view.frame.size.width, height: 45);
        return cell

    }
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {

//        println(data.lobbies[indexPath.row])
//        let lobbyView = SingleLobbyController(info: self.data.lobbies[indexPath.row])
//        self.navigationController?.pushViewController(lobbyView, animated: true)


    }




}