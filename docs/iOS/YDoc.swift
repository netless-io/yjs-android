import Foundation
import JavaScriptCore

public class YDocUpdateEvent: YJSEvent {
  public required init(jsValue: JSValue) {
    
  }
}

public class YDoc {
  
  let jsValue: JSValue
  let context: JSContext
  
  YDoc(jsValue: JSValue, context: JSContext) {
    self.jsValue = jsValue
    self.context = context
  }
  
  deinit {
    jsValue.invokeMethod("destroy", withArguments: [])
  }
  
  public func observeUpdate(_ handler: @escaping (YDocUpdateEvent) -> Void) -> YJSObserver<YDocUpdateEvent> {
    let observer = YJSObserver(handler: handler, context: jsValue.context)
    jsValue.invokeMethod("on", withArguments: ["update", observer.jsCallback])
    return observer
  }
  
  public func unobserveUpdate(_ observer: YJSObserver<YDocUpdateEvent>) {
    jsValue.invokeMethod("off", withArguments: ["update", observer.jsCallback])
  }
  
  public func transact(origin: String = "default", _ act: @escaping () -> Void) {
    let jsObject = JSValue.init(newObjectIn: context)!
    let swiftAct: @convention(block) () -> Void = act
    jsObject.setObject(unsafeBitCast(swiftAct, to: AnyObject.self), forKeyedSubscript: "act")
    let jsAct = jsObject.objectForKeyedSubscript("act")!
    jsValue.invokeMethod("transact", withArguments: [jsAct, origin])
  }
  
  public func getArray<Element>(id: String) -> YArray<Element> where Element: YAbscractType {
    guard let v = jsValue.invokeMethod("getArray", withArguments: [id]) else {
      fatalError("[DoYjs] get array fail")
    }
    return YArray(jsValue: v)
  }
  
  public func getMap(id: String) -> YMap {
    guard let v = jsValue.invokeMethod("getMap", withArguments: [id]) else {
      fatalError("[DoYjs] get map fail")
    }
    return YMap(jsValue: v)
  }
}
