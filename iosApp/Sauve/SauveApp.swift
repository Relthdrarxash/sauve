import SwiftUI
import UIKit
import Shared

// Coque iPhone : l'appli elle-même (écrans, navigation, contenus) est dans le module shared.
@main
struct SauveApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.keyboard)
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
