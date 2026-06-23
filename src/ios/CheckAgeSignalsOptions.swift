import Foundation

@objc class CheckAgeSignalsOptions: NSObject {
    var ageGates = [13, 15, 18]

    init(_ dictionary: [AnyHashable: Any]?) {
        super.init()
        if let ageGates = dictionary?["ageGates"] as? [Int] {
            self.ageGates = ageGates
        } else if let ageGates = dictionary?["ageGates"] as? [NSNumber] {
            self.ageGates = ageGates.map { $0.intValue }
        }
    }
}
