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
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        let kotlinMask = OrientationManager.shared.currentMask
        return UIInterfaceOrientationMask(rawValue: UInt(kotlinMask))
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
