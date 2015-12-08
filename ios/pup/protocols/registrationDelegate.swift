//
// Created by Alex Hartwell on 6/5/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

protocol RegistrationDelegate: class {
    func registerClicked()
    func closeClicked()

}

protocol RegistrationImageDelegate: class {
    func touched(image: ImageViewButton)
    func bringOutCamera()
    func bringOutPhotoLibrary()

}