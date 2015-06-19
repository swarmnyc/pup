//
// Created by Alex Hartwell on 6/19/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit



enum SocialConnect {
    case Facebook, Twitter, Reddit, Tumblr;

}

enum useFacebookAPI {
    case No, Yes;
}

class SocialButtonController: UIViewController {

    var type = SocialConnect.Facebook;


    convenience init(type: SocialConnect) {
        self.type = type;

        switch (type) {
            case .Facebook:
                setUpFacebookSDK();
            default:
                setUpOurOwnApi();

        }


    }

    func setUpFacebookSDK() {

    }

    func setUpOurOwnApi() {

    }

    override func loadView() {



    }

    override func viewDidLoad() {
        super.viewDidLoad()


    }
    
}