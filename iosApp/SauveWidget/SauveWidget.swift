import SwiftUI
import WidgetKit

// Widget SOS, sans donnée ni mise à jour : deux gestes vitaux ouverts directement
// et le numéro d'urgence prêt à composer. Équivalent du widget Android (SosWidget.kt).
// Chaque bouton ouvre l'appli par un lien sauve://, traité dans SauveApp.swift.

private let french = Locale.preferredLanguages.first?.hasPrefix("fr") ?? false

private func tr(_ fr: String, _ en: String) -> String { french ? fr : en }

private let ground = Color(red: 0x0F / 255, green: 0x12 / 255, blue: 0x16 / 255)
private let red = Color(red: 0xE5 / 255, green: 0x39 / 255, blue: 0x35 / 255)
private let orange = Color(red: 0xF5 / 255, green: 0x7C / 255, blue: 0x00 / 255)
private let blue = Color(red: 0x19 / 255, green: 0x76 / 255, blue: 0xD2 / 255)

struct SosEntry: TimelineEntry {
    let date: Date
}

struct SosProvider: TimelineProvider {
    func placeholder(in context: Context) -> SosEntry { SosEntry(date: Date()) }

    func getSnapshot(in context: Context, completion: @escaping (SosEntry) -> Void) {
        completion(SosEntry(date: Date()))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<SosEntry>) -> Void) {
        // Contenu fixe : aucune mise à jour à programmer.
        completion(Timeline(entries: [SosEntry(date: Date())], policy: .never))
    }
}

struct SosButton: View {
    let title: String
    let color: Color
    let target: String

    var body: some View {
        Link(destination: URL(string: "sauve://\(target)")!) {
            Text(title)
                .font(.system(size: 15, weight: .bold))
                .foregroundColor(.white)
                .multilineTextAlignment(.center)
                .minimumScaleFactor(0.7)
                .padding(6)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(color)
                .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        }
    }
}

struct SosWidgetView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Sauve · SOS")
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(.white.opacity(0.75))
            HStack(spacing: 8) {
                SosButton(title: tr("Arrêt cardiaque", "Cardiac arrest"), color: red, target: "cardiac")
                SosButton(title: tr("Étouffement", "Choking"), color: orange, target: "choking")
                SosButton(title: tr("Appeler le 15", "Call 112"), color: blue, target: "call")
            }
        }
        .sosBackground()
    }
}

extension View {
    // iOS 17 impose containerBackground et pose lui-même les marges ; avant, on les pose à la main.
    @ViewBuilder
    func sosBackground() -> some View {
        if #available(iOSApplicationExtension 17.0, *) {
            self.containerBackground(ground, for: .widget)
        } else {
            self.padding(12)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(ground)
        }
    }
}

@main
struct SauveWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "fr.sauve.app.sos", provider: SosProvider()) { _ in
            SosWidgetView()
        }
        .configurationDisplayName("Sauve · SOS")
        .description(tr("Les gestes vitaux et le 15 en un tap.", "Life-saving steps and emergency call in one tap."))
        // Format moyen uniquement : le petit format n'accepte qu'une seule zone cliquable.
        .supportedFamilies([.systemMedium])
    }
}
