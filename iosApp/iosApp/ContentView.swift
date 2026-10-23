import UIKit
import ComposeApp

class ComposeViewController: UIViewController {
    override var preferredStatusBarStyle: UIStatusBarStyle {
        SystemBarsManager.shared.isDarkTheme ? .lightContent : .darkContent
    }

    override var prefersStatusBarHidden: Bool {
        SystemBarsManager.shared.isHidden
    }

    override var prefersHomeIndicatorAutoHidden: Bool {
        SystemBarsManager.shared.isHidden
    }

    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        UIInterfaceOrientationMask(rawValue: UInt(OrientationManager.shared.currentMask))
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        let controller = Main_iosKt.MainViewController()
        addChild(controller)
        controller.view.frame = view.bounds
        controller.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(controller.view)
        controller.didMove(toParent: self)

        SystemBarsManager.shared.onAppearanceChanged = { [weak self] in
            DispatchQueue.main.async {
                self?.setNeedsStatusBarAppearanceUpdate()
                self?.setNeedsUpdateOfHomeIndicatorAutoHidden()
            }
        }

        OrientationManager.shared.onOrientationChange = { [weak self] _ in
            DispatchQueue.main.async {
                self?.updateOrientation()
            }
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        setNeedsStatusBarAppearanceUpdate()
        setNeedsUpdateOfHomeIndicatorAutoHidden()
        updateOrientation()
    }

    private func updateOrientation() {
        guard let windowScene = viewIfLoaded?.window?.windowScene else {
            return
        }

        let mask = supportedInterfaceOrientations
        let current = UIInterfaceOrientationMask(rawValue: 1 << windowScene.interfaceOrientation.rawValue)

        if #available(iOS 16.0, *) {
            setNeedsUpdateOfSupportedInterfaceOrientations()

            if mask.contains(current) {
                return
            }

            let preferences = UIWindowScene.GeometryPreferences.iOS(interfaceOrientations: mask)
            windowScene.requestGeometryUpdate(preferences) { error in
                print(error.localizedDescription)
            }
        } else {
            if !mask.contains(current) {
                let orientation: UIInterfaceOrientation

                if mask.contains(.portrait) {
                    orientation = .portrait
                } else if mask.contains(.landscapeRight) {
                    orientation = .landscapeRight
                } else {
                    orientation = .landscapeLeft
                }

                UIDevice.current.setValue(orientation.rawValue, forKey: "orientation")
            }

            UIViewController.attemptRotationToDeviceOrientation()
        }
    }
}
