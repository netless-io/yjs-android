import XCTest
@testable import DoYjs

class YDocTests: XCTestCase {
  
  let yContext = YContext()
  
  func testTransact() {
    let doc = yContext.createDoc()
    let array: YArray<YInt> = doc.getArray(id: "array")
    let map: YMap<YInt> = doc.getMap(id: "map")
    
    var arrayModifyCount = 0
    let _ = array.observe { _ in
      arrayModifyCount += 1
    }
    
    let _ = map.observe { evt in
      XCTAssert(evt.changes.count == 1)
    }
    
    doc.transact {
      array.insert(index: 0, content: [1,2,3])
      array.insert(index: 1, content: [1,2,3])
      map.set(key: "key", value: 1)
      map.set(key: "kkeeyy", value: 2)
      map["key"] = 3
      map.delete(key: "kkeeyy")
    }
    
    XCTAssert(array.length == 6)
    XCTAssert(arrayModifyCount == 1)
  }
  
}
