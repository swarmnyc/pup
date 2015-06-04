//
// Created by Alex Hartwell on 6/2/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

protocol SearcherDelegate: class {
    func handOffResults(data: Array<gameData>)
    func retreiveData(data: gameData)

}