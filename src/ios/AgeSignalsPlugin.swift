import Foundation

private func ageSignalsOkPluginResult(_ dictionary: [String: Any]) -> CDVPluginResult {
    let nsDictionary = dictionary as NSDictionary
    let selector = NSSelectorFromString("resultWithStatus:messageAsDictionary:")
    guard let method = class_getClassMethod(CDVPluginResult.self, selector) else {
        return CDVPluginResult(status: CDVCommandStatus_OK)
    }

    typealias ResultFactory = @convention(c) (AnyClass, Selector, CDVCommandStatus, NSDictionary) -> CDVPluginResult
    let factory = unsafeBitCast(method_getImplementation(method), to: ResultFactory.self)
    return factory(CDVPluginResult.self, selector, CDVCommandStatus_OK, nsDictionary)
}

@objc(AgeSignalsPlugin)
class AgeSignalsPlugin: CDVPlugin {
    private var implementation: AgeSignals?

    override func pluginInitialize() {
        implementation = AgeSignals(plugin: self)
    }

    @objc(checkAgeSignals:)
    func checkAgeSignals(command: CDVInvokedUrlCommand) {
        let options = CheckAgeSignalsOptions(command.argument(at: 0) as? [AnyHashable: Any])

        implementation?.checkAgeSignals(options) { result, error in
            if let error = error {
                self.sendError(error.localizedDescription, for: command)
                return
            }

            guard let result = result else {
                self.sendError("An unknown error occurred.", for: command)
                return
            }

            let pluginResult = ageSignalsOkPluginResult(result.toDictionary())
            self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
        }
    }

    @objc(checkEligibility:)
    func checkEligibility(command: CDVInvokedUrlCommand) {
        implementation?.checkEligibility { result, error in
            if let error = error {
                self.sendError(error.localizedDescription, for: command)
                return
            }

            guard let result = result else {
                self.sendError("An unknown error occurred.", for: command)
                return
            }

            let pluginResult = ageSignalsOkPluginResult(result.toDictionary())
            self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
        }
    }

    @objc(setUseFakeManager:)
    func setUseFakeManager(command: CDVInvokedUrlCommand) {
        sendError("This method is not available on this platform.", for: command)
    }

    @objc(setNextAgeSignalsResult:)
    func setNextAgeSignalsResult(command: CDVInvokedUrlCommand) {
        sendError("This method is not available on this platform.", for: command)
    }

    @objc(setNextAgeSignalsException:)
    func setNextAgeSignalsException(command: CDVInvokedUrlCommand) {
        sendError("This method is not available on this platform.", for: command)
    }

    private func sendError(_ message: String, for command: CDVInvokedUrlCommand) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: message)
        commandDelegate.send(pluginResult, callbackId: command.callbackId)
    }
}
