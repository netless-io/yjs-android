import XCTest
@testable import DoYjs

final class DoYjsTests: XCTestCase {
  
  let yContext = YContext()
  
  func testArrayManipulator() throws {
    var modified = false
    let yArray: YArray<YInt> = yContext.createDoc().getArray(id: "manipulator")
    
    let observer = yArray.observe { _ in
      modified = true
    }
    
    yArray.insert(index: 0, content: [yContext.createInt(1), yContext.createInt(2)])
    XCTAssertEqual(yArray.length, 2)
    XCTAssertEqual(modified, true)
    XCTAssertEqual(yArray.get(index: 0)?.int, 1)
    
    modified = false
    yArray.unobserve(observer)
    
    yArray.delete(index: 0, length: 1)
    XCTAssertEqual(yArray.length, 1)
    XCTAssertEqual(modified, false)
    
    yArray.push(content: [yContext.createInt(123)])
    XCTAssertEqual(yArray.length, 2)
    
    yArray.unshift(content: [yContext.createInt(1)])
    XCTAssertEqual(yArray.length, 3)
    
    let pice = yArray.slice(start: 0, end: 1)
    XCTAssertEqual(pice.length, 1)
  }
  
  func testYArrayAsSequence() {
    
    let yArray: YArray<YInt> = yContext.createDoc().getArray(id: "sequence")
    yArray.insert(index: 0, content: [1,2,3,4,5])
    
    var sum = 0
    for item in yArray {
      sum += item.int
    }
    
    XCTAssert(sum == 15)
  }

}
