//
// Created by Alex Hartwell on 8/10/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation
import UIKit

class FormTextField: UITextField {

    @IBInspectable var inset: CGFloat = CGFloat(UIConstants.horizontalPadding);

    override func textRectForBounds(bounds: CGRect) -> CGRect {
        return CGRectInset(bounds, inset, inset)
    }

    override func editingRectForBounds(bounds: CGRect) -> CGRect {
        return textRectForBounds(bounds)
    }

}