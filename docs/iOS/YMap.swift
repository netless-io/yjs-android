import Foundation
import JavaScriptCore

public enum YMapChange {
  case add(key: String)
  case update(key: String, old: JSValue)
  case delete(key: String, old: JSValue)
}

public class YMapEvent: YJSEvent {
  
  public let changes: [YMapChange]
  
  public required init(jsValue: JSValue) {
    var _changes = [YMapChange]()
    guard let context = jsValue.context,
          let jsKeys = jsValue.objectForKeyedSubscript("changes")
      .objectForKeyedSubscript("keys")
      .invokeMethod("keys", withArguments: []),
          let JsArray = context.objectForKeyedSubscript("Array"),
    let keys = JsArray.invokeMethod("from", withArguments: [jsKeys]).toArray() as? [String] else {
      fatalError("[DoYjs] YMapEvent.init fail")
    }
    
    for key in keys {
      guard let jsChange = jsValue.objectForKeyedSubscript("changes")
                                  .objectForKeyedSubscript("keys")
                                  .invokeMethod("get", withArguments: [key]),
            let action = jsChange.objectForKeyedSubscript("action")
      else {
        fatalError("[DoYjs] YMapEvent.init fail")
      }
      
      if action.toString() == "add" {
        _changes.append(.add(key: key))
      } else if action.toString() == "delete", let old = jsChange.objectForKeyedSubscript("oldValue") {
        _changes.append(.delete(key: key, old: old))
      } else if action.toString() == "update", let old = jsChange.objectForKeyedSubscript("oldValue") {
        _changes.append(.update(key: key, old: old))
      }
    }
    
    changes = _changes
  }
}

public class YMap: YAbscractType {
  
  public func set<T: YConvertible>(key: String, value: T) {
    jsValue.invokeMethod("set", withArguments: [key, value.toJsValue(context: jsValue.context)])
  }
  
  public func get<T: YConvertible>(key: String, type: T.Type) -> T? {
    guard let result = jsValue.invokeMethod("get", withArguments: [key]) else {
      fatalError("[DoYjs] YMap.get fail")
    }
    if !result.isUndefined {
      return type.init(jsValue: result)
    }
    return nil
  }
  
  public func get(key: String) -> JSValue? {
    jsValue.invokeMethod("get", withArguments: [key])
  }
  
  public func delete(key: String) {
    jsValue.invokeMethod("delete", withArguments: [key])
  }
  
  public func has(key: String) -> Bool {
    guard let result = jsValue.invokeMethod("has", withArguments: [key]) else {
      fatalError("[DoYjs] YMap.has fail")
    }
    if result.isBoolean {
      return result.toBool()
    }
    return false
  }
  
  public func size() -> Int {
    guard let result = jsValue.objectForKeyedSubscript("size") else {
      fatalError("[DoYjs] YMap.size fail")
    }
    return Int(result.toInt32())
  }
  
  public func keys() -> [String] {
    guard let result = jsValue.invokeMethod("keys", withArguments: []), let JsArray = jsValue.context.objectForKeyedSubscript("Array") else {
      fatalError("[DoYjs] YMap.keys fail")
    }
    return (JsArray.invokeMethod("from", withArguments: [result]).toArray() as? [String]) ?? []
  }
  
  public func observe(_ handler: @escaping (YMapEvent) -> Void) -> YJSObserver<YMapEvent> {
    let observer = YJSObserver<YMapEvent>(handler: handler, context: jsValue.context)
    jsValue.invokeMethod("observe", withArguments: [observer.jsCallback])
    return observer
  }
  
  public func unobserve(_ observer: YJSObserver<YMapEvent>) {
    jsValue.invokeMethod("unobserve", withArguments: [observer.jsCallback])
  }
}

public extension YMap {
  func set(key: String, value: Double) {
    set(key: key, value: YDouble(jsValue: JSValue(double: value, in: jsValue.context)))
  }
  
  func set(key: String, value: Int) {
    set(key: key, value: YDouble(jsValue: JSValue(int32: Int32(value), in: jsValue.context)))
  }
  
  func set(key: String, value: String) {
    set(key: key, value: YDouble(jsValue: JSValue(object: value, in: jsValue.context)))
  }
}

public extension YMap {
  func getDouble(key: String) -> Double? {
    let value = get(key: key, type: YDouble.self)
    return value?.double
  }
  
  func getInt(key: String) -> Int? {
    let value = get(key: key, type: YInt.self)
    return value?.int
  }
  
  func getString(key: String) -> String? {
    let value = get(key: key, type: YString.self)
    return value?.string
  }
}
