import Foundation
import JavaScriptCore

public class YUndoManager {
  
  let jsValue: JSValue
  
  init(jsValue: JSValue) {
    self.jsValue = jsValue
  }
  
  public func undo() {
    jsValue.invokeMethod("undo", withArguments: [])
  }
  
  public func redo() {
    jsValue.invokeMethod("redo", withArguments: [])
  }
}
