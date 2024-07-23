import Foundation
import JavaScriptCore

public protocol YConvertible {
  init?(jsValue: JSValue)
  func toJsValue(context: JSContext) -> JSValue
}

public class YAbscractType: YConvertible {
  
  public let jsValue: JSValue
  
  public required init(jsValue: JSValue) {
    self.jsValue = jsValue
  }
  
  public func toJsValue(context: JSContext) -> JSValue {
    jsValue
  }
}

public class YPlainObject: YAbscractType {
  
  @discardableResult
  public func setValue(key: String, value: Int) -> Self {
    jsValue.setValue(value, forProperty: key)
    return self
  }
  
  @discardableResult
  public func setValue(key: String, value: Double) -> Self {
    jsValue.setValue(value, forProperty: key)
    return self
  }
  
  @discardableResult
  public func setValue(key: String, value: String) -> Self {
    jsValue.setValue(value, forProperty: key)
    return self
  }
  
  public func getInt(key: String) -> Int? {
    if let result = jsValue.objectForKeyedSubscript(key), result.isNumber {
      return Int(result.toInt32())
    }
    return nil
  }
  
  public func getString(key: String) -> String? {
    if let result = jsValue.objectForKeyedSubscript(key), result.isString {
      return result.toString()
    }
    return nil
  }
  
  public func getDouble(key: String) -> Double? {
    if let result = jsValue.objectForKeyedSubscript(key), result.isNumber {
      return result.toDouble()
    }
    return nil
  }
}

public class YInt: YAbscractType {
  
  public var int: Int? {
    if jsValue.isNumber {
      return Int(jsValue.toInt32())
    }
    return nil
  }
  
}

public class YDouble: YAbscractType {
  
  public var double: Double? {
    if jsValue.isNumber {
      return jsValue.toDouble()
    }
    return nil
  }
}

public class YBool: YAbscractType {
  public var bool: Bool? {
    if jsValue.isBoolean {
      return jsValue.toBool()
    }
    return nil
  }
}

public class YString: YAbscractType {
  
  public var string: String? {
    if jsValue.isString {
      return jsValue.toString()
    }
    return nil
  }
  
}

extension Array: YConvertible where Element: YConvertible {
  public init?(jsValue: JSValue) {
    if jsValue.isArray {
      var arr: [Element] = []
      let length = Int(jsValue.objectForKeyedSubscript("length").toInt32())
      for i in 0..<length {
        guard let jsItem = jsValue.objectAtIndexedSubscript(i), let item = Element.init(jsValue: jsItem) else {
          fatalError("[DoYjs] YAbscrateType js array to swift array fail")
        }
        arr.append(item)
      }
      self = arr
    } else {
      return nil
    }
  }
  
  public func toJsValue(context: JSContext) -> JSValue {
    guard let jsArray = JSValue(newArrayIn: context) else {
      fatalError("")
    }
    
    for item in self {
      jsArray.invokeMethod("push", withArguments: [item.toJsValue(context: context)])
    }
    
    return jsArray
  }
}

extension Date: YConvertible {
  public init?(jsValue: JSValue) {
    let yDouble = YDouble(jsValue: jsValue)
    if let interval = yDouble.double {
      self.init(timeIntervalSince1970: interval)
    } else {
      return nil
    }
  }
  
  public func toJsValue(context: JSContext) -> JSValue {
    return YDouble(jsValue: JSValue(double: self.timeIntervalSince1970, in: context)).jsValue
  }
}
