import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @StateObject private var systemBars = SystemBarsState()

    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
            .statusBarHidden(systemBars.isHidden)
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        OrientationManager.shared.onOrientationChange = { maskRaw in
            DispatchQueue.main.async {
                let mask = UIInterfaceOrientationMask(
                    rawValue: UInt(truncating: maskRaw as? NSNumber ?? 0)
                )
                
                Self.forceOrientationUpdate(mask)
            }
        }

        return true
    }

    func application(
        _ application: UIApplication,
        supportedInterfaceOrientationsFor window: UIWindow?
    ) -> UIInterfaceOrientationMask {
        let mask = OrientationManager.shared.currentMask
        return UIInterfaceOrientationMask(rawValue: UInt(mask))
    }

    private static func forceOrientationUpdate(_ mask: UIInterfaceOrientationMask) {
        guard let windowScene = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .first else {
            return
        }

        if #available(iOS 16.0, *) {
            updateOrientation(windowScene: windowScene, mask: mask)
        } else {
            updateOrientation(mask)
        }
    }

    @available(iOS 16.0, *)
    private static func updateOrientation(
        windowScene: UIWindowScene,
        mask: UIInterfaceOrientationMask
    ) {
        windowScene.windows
            .first(where: { $0.isKeyWindow })?
            .rootViewController?
            .setNeedsUpdateOfSupportedInterfaceOrientations()

        let preferences = UIWindowScene.GeometryPreferences.iOS(
            interfaceOrientations: mask
        )

        windowScene.requestGeometryUpdate(preferences) { error in
            print(error.localizedDescription)
        }
    }

    private static func updateOrientation(_ mask: UIInterfaceOrientationMask) {
        let orientation: UIInterfaceOrientation

        if mask.contains(.portrait) {
            orientation = .portrait
        } else if mask.contains(.landscapeRight) {
            orientation = .landscapeRight
        } else if mask.contains(.landscapeLeft) {
            orientation = .landscapeLeft
        } else {
            orientation = .portrait
        }

        UIDevice.current.setValue(orientation.rawValue, forKey: "orientation")
    }
}

class SystemBarsState: ObservableObject {
    @Published var isHidden: Bool = false

    init() {
        SystemBarsManager.shared.onVisibilityChanged = { [weak self] hidden in
            DispatchQueue.main.async {
                self?.isHidden = hidden == true
            }
        }
    }
}
