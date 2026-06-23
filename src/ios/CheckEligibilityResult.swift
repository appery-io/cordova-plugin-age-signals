import Foundation

@objc class CheckEligibilityResult: NSObject {
    let isEligible: Bool

    init(isEligible: Bool) {
        self.isEligible = isEligible
    }

    func toDictionary() -> [String: Any] {
        return ["isEligible": isEligible]
    }
}
