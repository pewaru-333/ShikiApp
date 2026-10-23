import UIKit
import ComposeApp

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        supportedInterfaceOrientationsFor window: UIWindow?
    ) -> UIInterfaceOrientationMask {
        UIInterfaceOrientationMask(rawValue: UInt(OrientationManager.shared.currentMask))
    }
}

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = scene as? UIWindowScene else {
            return
        }

        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = ComposeViewController()
        self.window = window
        window.makeKeyAndVisible()

        for context in connectionOptions.urlContexts {
            ExternalUriHandler.shared.onNewUri(uri: context.url.absoluteString)
        }
        for activity in connectionOptions.userActivities {
            if let url = activity.webpageURL {
                ExternalUriHandler.shared.onNewUri(uri: url.absoluteString)
            }
        }
    }

    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        for context in URLContexts {
            ExternalUriHandler.shared.onNewUri(uri: context.url.absoluteString)
        }
    }

    func scene(_ scene: UIScene, continue userActivity: NSUserActivity) {
        if let url = userActivity.webpageURL {
            ExternalUriHandler.shared.onNewUri(uri: url.absoluteString)
        }
    }

    func sceneDidDisconnect(_ scene: UIScene) {
        OrientationManager.shared.onOrientationChange = nil
        SystemBarsManager.shared.onAppearanceChanged = nil
        window = nil
    }
}
