//
// Created by Alex Hartwell on 6/4/15.
// Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation


class HorizontalData {
    var options: Array<String> = []

    var numberOfOptions: Int {
        get {
            return self.options.count;
        }
    }

    var title: String = ""
    var _currentSelection: Int = 0;
    var currentSelection: Int {
        get {
            return self._currentSelection;
        }
        set (newVal) {
            println(newVal)
            if (newVal<0) {
                self._currentSelection = 0;
            } else if (newVal>=self.numberOfOptions) {
                self._currentSelection = self.numberOfOptions - 1;
            } else {
                self._currentSelection = newVal
            }

        }
    }


    func getIndexFromString(name: String) -> Int {
        for (var i=0; i<options.count; i++) {
            println(options[i])
            if (options[i] == name) {
                return i;
            }
        }

        return self.currentSelection;
    }


}