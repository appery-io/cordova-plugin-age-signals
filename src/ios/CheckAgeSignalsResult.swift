import Foundation

@objc class CheckAgeSignalsResult: NSObject {
    let userStatus: UserStatus
    let ageLower: Int?
    let ageUpper: Int?
    let mostRecentApprovalDate: String?
    let installId: String?
    let ageRangeDeclaration: AgeRangeDeclaration?

    init(
        userStatus: UserStatus,
        ageLower: Int? = nil,
        ageUpper: Int? = nil,
        mostRecentApprovalDate: String? = nil,
        installId: String? = nil,
        ageRangeDeclaration: AgeRangeDeclaration? = nil
    ) {
        self.userStatus = userStatus
        self.ageLower = ageLower
        self.ageUpper = ageUpper
        self.mostRecentApprovalDate = mostRecentApprovalDate
        self.installId = installId
        self.ageRangeDeclaration = ageRangeDeclaration
    }

    func toDictionary() -> [String: Any] {
        var result: [String: Any] = [
            "userStatus": userStatus.rawValue
        ]

        if let ageLower = ageLower {
            result["ageLower"] = ageLower
        }
        if let ageUpper = ageUpper {
            result["ageUpper"] = ageUpper
        }
        if let mostRecentApprovalDate = mostRecentApprovalDate {
            result["mostRecentApprovalDate"] = mostRecentApprovalDate
        }
        if let installId = installId {
            result["installId"] = installId
        }
        if let ageRangeDeclaration = ageRangeDeclaration {
            result["ageRangeDeclaration"] = ageRangeDeclaration.rawValue
        }

        return result
    }
}
