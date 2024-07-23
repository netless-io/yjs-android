import JavaScriptCore
import DoExtensions

public class YContext {
  
  public let jsContext: JSContext
  
  var jsRoot: JSValue {
    jsContext.objectForKeyedSubscript("doY").objectForKeyedSubscript("doY")
  }
  
  public init() {
    jsContext = JSContext()
//    jsContext.isInspectable = true
    let yjsUrl = Bundle.module.path(forResource: "doY", ofType: "js")
    let yjsScripts = try! String(contentsOfFile: yjsUrl!, encoding: .utf8)
    jsContext.evaluateScript("var console = { log: function(message) { _consoleLog(message) } }")
    let consoleLog: @convention(block) (String) -> Void = { message in
        print("JSCore console.log: " + message)
    }
    jsContext.setObject(unsafeBitCast(consoleLog, to: AnyObject.self), forKeyedSubscript: "_consoleLog" as (NSCopying & NSObjectProtocol)?)
    
    // yjs 依赖 crypto.getRandomValues, JavascriptCore 环境不支持
    jsContext.evaluateScript("var crypto = { getRandomValues: function() { return [_getRandomValues()]; } }")
    let getRandomValues: @convention(block) () -> UInt32 = {
      return arc4random_uniform(UInt32.max)
    }
    jsContext.setObject(unsafeBitCast(getRandomValues, to: AnyObject.self), forKeyedSubscript: "_getRandomValues" as (NSCopying & NSObjectProtocol)?)
    
    jsContext.exceptionHandler = { _, err in
      if let msg = err?.objectForKeyedSubscript("message").toString(), let stack = err?.objectForKeyedSubscript("stack").toString() {
        print("JSCore error: ", msg, stack)
      }
    }
    jsContext.evaluateScript(yjsScripts)
  }
  
  public func createInt(_ value: Int) -> YInt {
    YInt(jsValue: JSValue(int32: Int32(value), in: jsContext))
  }
  
  public func createDouble(_ value: Double) -> YDouble {
    YDouble(jsValue: JSValue(double: value, in: jsContext))
  }
  
  public func createBool(_ value: Bool) -> YBool {
    YBool(jsValue: JSValue(bool: value, in: jsContext))
  }
  
  public func createString(_ value: String) -> YString {
    YString(jsValue: JSValue(object: value, in: jsContext))
  }
  
  public func createPlainObject() -> YPlainObject {
    YPlainObject(jsValue: JSValue(newObjectIn: jsContext))
  }
  
  public func createDoc() -> YDoc {
    guard let jsValue = jsRoot.objectForKeyedSubscript("Doc").construct(withArguments: []) else {
      fatalError("[DoYjs] YContext.createDoc: create doc fail")
    }
    return YDoc(jsValue: jsValue, context: jsContext)
  }
  
  public func createMap() -> YMap {
    guard let jsValue = jsRoot.objectForKeyedSubscript("Map").construct(withArguments: []) else {
      fatalError("[DoYjs] YContext.createMap: create map fail")
    }
    return YMap(jsValue: jsValue)
  }
  
  public func createArray<Element>() -> YArray<Element> {
    guard let jsValue = jsRoot.objectForKeyedSubscript("Array").construct(withArguments: []) else {
      fatalError("[DoYjs] YContext.createArray: create array fail")
    }
    return YArray(jsValue: jsValue)
  }
  
  public func createUndoManager(scope: YAbscractType, trackOrgins: Set<String> = [], captureTimeout: Int = 0) -> YUndoManager {
    guard let options = JSValue(newObjectIn: jsContext) else {
      fatalError("[DoYjs] YContext.createUndoManager fail")
    }
    options.setObject(trackOrgins, forKeyedSubscript: "trackOrgins")
    options.setObject(captureTimeout, forKeyedSubscript: "captureTimeout")
    guard let jsValue = jsRoot.objectForKeyedSubscript("UndoManager")
                              .construct(withArguments: [scope.jsValue, options]) else {
      fatalError("[DoYjs] YContext.createUndoManager fail")
    }
    return YUndoManager(jsValue: jsValue)
  }
  
  public func encodeStateAsUpdate(doc: YDoc) -> Data {
    guard let jsBuf = jsRoot.invokeMethod("encodeStateAsUpdate", withArguments: [doc.jsValue]),
          let buf = jsBuf.toArray() as? [UInt8] else {
      fatalError("[DoYjs] YContext.encodeStateAsUpdate fail")
    }
    return Data(buf)
  }
  
  public func encodeStateAsUpdate(doc: YDoc, vector: Data) -> Data {
    guard let jsArray = jsContext.objectForKeyedSubscript("Uint8").construct(withArguments: [vector.bytes]),
          let jsBuf = jsRoot.invokeMethod("encodeStateAsUpdate", withArguments: [doc.jsValue, jsArray]),
          let buf = jsBuf.toArray() as? [UInt8] else {
      fatalError("[DoYjs] YContext.encodeStateAsUpdate fail")
    }
    return Data(buf)
  }
  
  public func encodeStateVector(doc: YDoc) -> Data {
    guard let jsBuf = jsRoot.invokeMethod("encodeStateVector", withArguments: [doc.jsValue]),
          let buf = jsBuf.toArray() as? [UInt8] else {
      fatalError("[DoYjs] YContext.encodeStateVector fail")
    }
    return Data(buf)
  }
  
  public func applyUpdate(doc: YDoc, buf: Data) {
    guard let jsByteArray = jsContext.objectForKeyedSubscript("Uint8Array").construct(withArguments: [buf.bytes]) else {
      fatalError("[DoYjs] YContext.applyUpdate fail")
    }
    jsRoot.invokeMethod("applyUpdate", withArguments: [doc.jsValue, jsByteArray])
  }
  
  public func merge(_ a: Data, _ b: Data) -> Data {
    let doc = createDoc()
    applyUpdate(doc: doc, buf: a)
    applyUpdate(doc: doc, buf: b)
    
    return encodeStateAsUpdate(doc: doc)
  }
}

public extension JSContext {
  
    s/elf.objectForKeyedSubscript("doY").objectForKeyedSubscript("doY")
  var yRoot: JSValue {
  }
  
  func createYMap() -> YMap {
    guard let jsValue = yRoot.objectForKeyedSubscript("Map").construct(withArguments: []) else {
      fatalError("[DoYjs] YContext.createMap: create map fail")
    }
    return YMap(jsValue: jsValue)
  }
  
  func createPlainObject() -> YPlainObject {
    return YPlainObject(jsValue: JSValue(newObjectIn: self))
  }
  
}
