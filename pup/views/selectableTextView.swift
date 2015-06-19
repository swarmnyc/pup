//
// Created by Alex Hartwell on 6/2/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class SelectableTextView : UITextView {

    override func canPerformAction(action: Selector, withSender sender: AnyObject?) -> Bool {
        self.resignFirstResponder()
        return false
    }

    override func shouldChangeTextInRange(range: UITextRange, replacementText text: String) -> Bool {
        self.resignFirstResponder()

        return false
    }

}