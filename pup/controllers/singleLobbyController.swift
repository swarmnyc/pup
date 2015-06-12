//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore
import SwiftLoader

class SingleLobbyController: UIViewController {

    var lobbyView: SingleLobbyView?;

    var data: singleLobby = singleLobby();

    var submitting = false;


    var logInButton: JoinButton? = nil

    convenience init(info: LobbyData) {

        self.init();
        println(info);
        println(info.name);
        data.data = info;






    }




    override func loadView()
    {
        self.lobbyView = SingleLobbyView();
        lobbyView?.setUpDelegates(self)
        self.view = self.lobbyView;
    }


    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.whiteColor()
        self.title = data.data.name;
        println("view did load")
        currentUser.setPage("Single Lobby")

        self.data.getDetailed({
            self.lobbyView?.setUpViews(self.data)

            if (currentUser.loggedIn() == false) {
                self.logInButton = JoinButton(parentController: self)
                self.logInButton?.onSuccessJoin = self.joinLobby
            }

        }, failure: {

        })







    }







    override func viewDidLayoutSubviews() {  //set up layer properties that don't get set by constraints
            super.viewDidLayoutSubviews();
        println("view did layout subviews")


        // gradient.frame = gradientBox.frame;



    }


    func joinLobby() {
        println("joining this lobby")
        var config = SwiftLoader.Config()
        config.size = 150
        config.spinnerColor = UIColor(rgba: colors.orange)
        config.backgroundColor = UIColor(rgba: colors.mainGrey)

        SwiftLoader.setConfig(config);
        SwiftLoader.show(title: "Loading...", animated: true)
        if (submitting==false) {
            submitting = true;
            data.joinLobby({
                self.submitting = false;
                println("successfully joined")
                self.lobbyView?.joinLobbyButton.removeFromSuperview();
                SwiftLoader.hide()
            }, failure: {
                println("failed")
                SwiftLoader.hide()
                self.submitting = false;
            })

        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }




}





class NoOnesHere {

    var topText: UIView = UIView();
    var facebookBlocks: Array<UITextView> = []
    var inviteButton: UIButton = UIButton()
    var parent: SingleLobbyController?
    init(parentController: SingleLobbyController) {
        parent = parentController;

        setUpViews()
    }

    func setUpViews() {


    }

    func addViews() {

    }

    func setUpConstraints() {


    }


}


