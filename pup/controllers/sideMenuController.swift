//
// Created by Alex Hartwell on 5/29/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SideMenuController: UIViewController, PanGestureDelegate, UIGestureRecognizerDelegate, MenuItemDelegate {

   var sideMenuView: SideMenuView?

    var data: singleLobby = singleLobby();

    var parent: LobbyListController?

    var logInButton: JoinButton? = nil

    var isOpen: Bool = false


    convenience init(parentController: LobbyListController, overlayDelegate: OverlayDelegate) {

        self.init();
       parent = parentController

        self.sideMenuView = SideMenuView();
        self.view = self.sideMenuView;
        sideMenuView?.setUpView(parent!.view, parent: self)
        sideMenuView?.setUpDelegates(self, overlayDelegate: overlayDelegate)

    }





    func toggleState() {
        if isOpen {
            isOpen = false
            sideMenuView?.closeMenu()
        } else {
            isOpen = true
            sideMenuView?.openMenu()
        }

    }

    func closeMenu() {
        isOpen = false
        sideMenuView?.closeMenu()
    }

    func swiped(sender: UIPanGestureRecognizer) {

        self.isOpen = self.sideMenuView!.setTranslation(sender, opened: isOpen);
        println(self.isOpen);

    }

    func touchUpInside(menuItem: MenuItems) {
        var name = menuItem.name;
        if (name == currentUser.currentPage) {
            toggleState();
        } else {
            println(menuItem.name);
            toggleState();
            if (name=="Settings") {
                let lobbyView = UIViewController();
                self.parent?.navigationController?.pushViewController(lobbyView, animated: true)
            }
        }
    }






}