import SwiftUI
import UIKit
import Shared

// Coque iPhone : l'appli elle-même (écrans, navigation, contenus) est dans le module shared.
@main
struct SauveApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate

    var body: some Scene {
        WindowGroup {
            // Compose dessine jusqu'aux bords et applique lui-même les marges de l'encoche et de la barre du bas.
            ContentView()
                .ignoresSafeArea()
                // Liens sauve://cardiac, sauve://choking, sauve://call... envoyés par le widget.
                .onOpenURL { url in
                    if let target = url.host {
                        MainViewControllerKt.openTarget(target: target)
                    }
                }
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// Raccourcis de l'icône (appui long) : un geste vital en un tap, comme sur Android.
final class AppDelegate: NSObject, UIApplicationDelegate {
    private static let targets = ["cardiac", "choking", "bleeding", "recovery"]

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        application.shortcutItems = AppDelegate.targets.map { target in
            UIApplicationShortcutItem(
                type: target,
                localizedTitle: MainViewControllerKt.shortcutTitle(target: target),
                localizedSubtitle: nil,
                icon: UIApplicationShortcutIcon(systemImageName: "cross.case.fill"),
                userInfo: nil
            )
        }
        return true
    }

    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        // Appli fermée : le raccourci choisi arrive ici.
        if let item = options.shortcutItem {
            MainViewControllerKt.openTarget(target: item.type)
        }
        let configuration = UISceneConfiguration(name: nil, sessionRole: connectingSceneSession.role)
        configuration.delegateClass = SceneDelegate.self
        return configuration
    }
}

final class SceneDelegate: NSObject, UIWindowSceneDelegate {
    // Appli déjà ouverte en arrière-plan : le raccourci choisi arrive ici.
    func windowScene(
        _ windowScene: UIWindowScene,
        performActionFor shortcutItem: UIApplicationShortcutItem,
        completionHandler: @escaping (Bool) -> Void
    ) {
        MainViewControllerKt.openTarget(target: shortcutItem.type)
        completionHandler(true)
    }
}
