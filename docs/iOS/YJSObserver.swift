import Foundation
import JavaScriptCore

public protocol YJSEvent {
  init(jsValue: JSValue)
}

public class YJSObserver<Event: YJSEvent> {
  
  let handler: (Event) -> Void
  let jsObserverObject: JSValue
  let jsCallback: JSValue

  init(handler: @escaping (Event) -> Void, context: JSContext) {
    self.handler = handler
    jsObserverObject = JSValue.init(newObjectIn: context)
    
    let observerHandler: @convention(block) (JSValue) -> Void = { jsOutput in
      handler(Event.init(jsValue: jsOutput))
    }
    jsObserverObject.setObject(unsafeBitCast(observerHandler, to: AnyObject.self), forKeyedSubscript: "callback")
    jsCallback = jsObserverObject.objectForKeyedSubscript("callback")!
  }
  
}
