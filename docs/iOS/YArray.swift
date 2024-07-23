import Foundation
import JavaScriptCore
import Combine

public class YArrayEvent: YJSEvent {
  let jsValue: JSValue
  
  public required init(jsValue: JSValue) {
    self.jsValue = jsValue
  }
}

public class YArray<Element>: YAbscractType where Element: YConvertible {
  
  public func observe(handler: @escaping (YArrayEvent) -> Void) -> YJSObserver<YArrayEvent> {
    let observer = YJSObserver(handler: handler, context: jsValue.context)
    jsValue.invokeMethod("observe", withArguments: [observer.jsCallback])
    return observer
  }
  
  public func unobserve(_ observer: YJSObserver<YArrayEvent>) {
    jsValue.invokeMethod("unobserve", withArguments: [observer.jsCallback])
  }
  
  public var length: Int {
    Int(jsValue.objectForKeyedSubscript("length").toInt32())
  }
  
  @discardableResult
  public func insert(index: UInt, content: [Element]) -> Self {
    guard let jsIndex = JSValue(int32: Int32(index), in: jsValue.context), let jsArray = JSValue(newArrayIn: jsValue.context) else {
      fatalError("[DoYjs] YArray.insert fail")
    }
    for item in content {
      jsArray.invokeMethod("push", withArguments: [item.toJsValue(context: jsValue.context)])
    }
    jsValue.invokeMethod("insert", withArguments: [jsIndex, jsArray])
    return self
  }
  
  @discardableResult
  public func delete(index: UInt, length: UInt) -> Self {
    if index + length <= self.length {
      jsValue.invokeMethod("delete", withArguments: [index, length])
    }
    return self
  }
  
  @discardableResult
  public func push(content: [Element]) -> Self {
    guard let jsArray = JSValue(newArrayIn: jsValue.context) else {
      fatalError("[DoYjs] YArray.push fail")
    }
    for item in content {
      jsArray.invokeMethod("push", withArguments: [item.toJsValue(context: jsValue.context)])
    }
    jsValue.invokeMethod("push", withArguments: [jsArray])
    return self
  }
  
  @discardableResult
  public func unshift(content: [Element]) -> Self {
    guard let jsArray = JSValue(newArrayIn: jsValue.context) else {
      fatalError("[DoYjs] YArray.push fail")
    }
    for item in content {
      jsArray.invokeMethod("push", withArguments: [item.toJsValue(context: jsValue.context)])
    }
    jsValue.invokeMethod("unshift", withArguments: [jsArray])
    return self
  }
  
  public func slice(start: UInt, end: UInt? = nil) -> YArray<Element> {
    var jsEnd = -1
    if let end {
      jsEnd = Int(end)
    }
    guard let result = jsValue.invokeMethod("slice", withArguments: [start, jsEnd]) else {
      fatalError("[DoYjs] YArray.slice fail")
    }
    return YArray(jsValue: result)
  }
  
  public func get(index: UInt) -> Element? {
    if let result = jsValue.invokeMethod("get", withArguments: [index]) {
      if result.isUndefined {
        return nil
      }
      return .init(jsValue: result)
    }
    return nil
  }
}

public class YArrayIterator<E>: IteratorProtocol where E: YConvertible {
  
  let array: YArray<E>
  var index: UInt
  
  init(array: YArray<E>) {
    self.array = array
    index = 0
  }
  
  public func next() -> E? {
    if index < array.length {
      let value = array.get(index: index)
      index += 1;
      return value
    }
    return nil
  }
  
}

extension YArray: Sequence {
  
  public func makeIterator() -> YArrayIterator<Element> {
    return YArrayIterator(array: self)
  }
  
}

extension YArray where Element == YInt {
  
  @discardableResult
  func insert(index: UInt, content: [Int]) -> Self {
    self.insert(index: index, content: content.map({ YInt(jsValue: JSValue(int32: Int32($0), in: jsValue.context)) }))
    return self
  }
}
