import Foundation
import JavaScriptCore

public class YText: YAbscractType {
  
  var length: UInt {
    guard let result = jsValue.objectForKeyedSubscript("length") else {
      fatalError("[DoYjs] YText.length fail")
    }
    return UInt(result.toInt32())
  }

  func insert(index: UInt, content: String, format: YPlainObject? = nil) {
    guard let jsFormat = (format?.jsValue ?? JSValue(undefinedIn: jsValue.context)) else {
      fatalError("[DoYjs] YText.insert fail")
    }
    jsValue.invokeMethod("insert", withArguments: [index, content, jsFormat])
  }
  
  func format(index: UInt, length: UInt, format: YPlainObject) {
    jsValue.invokeMethod("format", withArguments: [index, length, format.jsValue])
  }
  
  func delete(index: UInt, length: UInt) {
    jsValue.invokeMethod("delete", withArguments: [index, length])
  }
  
  func toString() -> String {
    guard let result = jsValue.invokeMethod("toString", withArguments: []) else {
      fatalError("[DoYjs] YText.toString fail")
    }
    return result.toString()
  }
  
  func clone() -> YText {
    guard let jsResult = jsValue.invokeMethod("clone", withArguments: []) else {
      fatalError("[DoYjs] YText.clone fail")
    }
    return YText(jsValue: jsResult)
  }
}
