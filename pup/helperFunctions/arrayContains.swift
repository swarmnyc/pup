//
// Created by Alex Hartwell on 6/16/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

extension Array {
    func contains<T where T : Equatable>(obj: T) -> Bool {
        return self.filter({$0 as? T == obj}).count > 0
    }
}