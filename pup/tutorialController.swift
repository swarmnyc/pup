//
//  tutorialController.swift
//  pup
//
//  Created by Alex Hartwell on 8/3/15.
//  Copyright (c) 2015 SWARM NYC. All rights reserved.
//

import Foundation

class TutorialController: UIPageViewController, UIPageViewControllerDelegate, UIPageViewControllerDataSource {
    var controllers: [PageController] = [];
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor(rgba: colors.orange);
        var controllers: [PageController] = [];
        controllers.append(PageController());
              controllers[0].setImageAndIndex(0);
                 setViewControllers(controllers, direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil);
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
    }
    
  
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerBeforeViewController viewController: UIViewController) -> UIViewController? {
        var currentController = viewController as! PageController;
        if (currentController.index! > 0) {
            var newController = PageController();
            newController.setImageAndIndex(currentController.index! - 1);
            return newController;
        }
        return nil;
        
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerAfterViewController viewController: UIViewController) -> UIViewController? {
        
        var currentController = viewController as! PageController;
        if (currentController.index! < 2) {
            var newController = PageController();
            newController.setImageAndIndex(currentController.index! + 1);
            return newController;
        }
        return nil;
        
    }
    
 
    
    func presentationCountForPageViewController(pageViewController: UIPageViewController) -> Int {
        return 3
    }
    
    func presentationIndexForPageViewController(pageViewController: UIPageViewController) -> Int {
        return 0
    }
    
   
    
}


class PageController: UIViewController {
    var pageImage: UIImageView?;
    var index: Int?;
    var continueButton: UIButton?
    
    
    func setImageAndIndex(index: Int) {
        self.index = index;
        pageImage = UIImageView();
        if (self.index == 0) {
            pageImage?.image = UIImage(named: "stepOne");
        } else if (self.index == 1) {
            pageImage?.image = UIImage(named: "stepTwo");
        } else {
            continueButton = UIButton();
            continueButton?.setTitle("Continue To PUP", forState: .Normal);
            continueButton?.setTitleColor(UIColor(rgba: colors.orange), forState: .Normal);
            continueButton?.backgroundColor = UIColor.whiteColor();
            continueButton?.addTarget(self, action: "endTut", forControlEvents: UIControlEvents.TouchUpInside);
            pageImage?.image = UIImage(named: "stepThree");
        }
        
        
    }
    
    func endTut() {
        println(self.parentViewController);
        self.parentViewController?.dismissViewControllerAnimated(true, completion: nil);
    }
    
    override func viewDidLoad() {

        self.view.addSubview(pageImage!);
        
        self.pageImage?.snp_remakeConstraints({
            (make) -> Void in
            make.top.equalTo(self.view);
            make.left.equalTo(self.view);
            make.right.equalTo(self.view);
            make.bottom.equalTo(self.view);
        })
        
        if (continueButton != nil) {
            self.view.addSubview(continueButton!);
            continueButton?.snp_remakeConstraints({
                (make) -> Void in
                make.bottom.equalTo(self.view).offset(-15);
                make.left.equalTo(self.view).offset(UIConstants.horizontalPadding * 2);
                make.right.equalTo(self.view).offset(UIConstants.horizontalPadding * -2);
                make.height.equalTo(45);
                
            })
        }
        
    }
    
}


