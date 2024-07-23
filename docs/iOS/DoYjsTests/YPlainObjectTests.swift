import XCTest
@testable import DoYjs

class YPlainObjectTests: XCTestCase {
  
  let context = YContext()
  
  func testPlainObject() {
    let obj = context.createPlainObject()
    
    obj.setValue(key: "number", value: 123)
    XCTAssert(obj.getInt(key: "number") == 123)
    
    obj.setValue(key: "double", value: 0.123)
    XCTAssert(obj.getDouble(key: "double") == 0.123)
    
    obj.setValue(key: "string", value: "123")
    XCTAssert(obj.getString(key: "string") == "123")
    
    obj.setValue(key: "string", value: 123)
    XCTAssert(obj.getString(key: "string") == nil)
    XCTAssert(obj.getInt(key: "string") == 123)
  }
  
}
