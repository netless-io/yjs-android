import XCTest
@testable import DoYjs

class YUndoManagerTests: XCTestCase {
  
  let yContext = YContext()
  
  func testManipulateArray() {
    let arr: YArray<YInt> = yContext.createDoc().getArray(id: "arr")
    let undoManager = yContext.createUndoManager(scope: arr)
    arr.insert(index: 0, content: [1,2,3])
    XCTAssert(arr.length == 3)
    undoManager.undo()
    XCTAssert(arr.length == 0)
    undoManager.redo()
    XCTAssert(arr.length == 3)
  }
  
  func testManipulateComplexMap() {
    let map: YMap<YArray<YInt>> = yContext.createDoc().getMap(id: "map")
    let undoManager = yContext.createUndoManager(scope: map)
    let arr: YArray<YInt> = yContext.createArray()
    map["arr"] = arr
    arr.insert(index: 0, content: [1,2,3])
    XCTAssert(arr.length == 3)
    undoManager.undo()
    XCTAssert(arr.length == 0)
    undoManager.redo()
    XCTAssert(arr.length == 3)
  }
}
