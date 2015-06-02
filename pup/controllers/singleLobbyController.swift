//
// Created by Alex Hartwell on 5/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore

class SingleLobbyController: UIViewController {

    var lobbyView: SingleLobbyView?;

    var data: singleLobby = singleLobby();



    var logInButton: JoinButton? = nil

    convenience init(info: lobbyData) {

        self.init();
        println(info);
        println(info.Name);
        data.data = info;






    }




    override func loadView()
    {
        self.lobbyView = SingleLobbyView();
        self.view = self.lobbyView;
    }


    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.whiteColor()
        self.title = data.data.Name;
        println("view did load")
        currentUser.setPage("Single Lobby")

        let requestUrl = NSURL(string: "\(urls.lobbies)\(data.data.id)")

        let task = NSURLSession.sharedSession().dataTaskWithURL(requestUrl!) {(data, response, error) in
            println(error)
            let jsonResponse = JSON(data: data)
            self.data.addDetailed(jsonResponse)
            // println(jsonResponse)

            dispatch_async(dispatch_get_main_queue(),{

                self.lobbyView?.setUpViews(self.data)

            })
        }

        task.resume()



        if (currentUser.loggedIn() == false) {
           logInButton = JoinButton(parentController: self)
        }




    }







    override func viewDidLayoutSubviews() {  //set up layer properties that don't get set by constraints
            super.viewDidLayoutSubviews();
        println("view did layout subviews")


        // gradient.frame = gradientBox.frame;



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


