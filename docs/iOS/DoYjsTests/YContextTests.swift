import XCTest
@testable import DoYjs

class YContextTests: XCTestCase {
  
  func testEncodeStateAsUpdate() {
    let context = YContext()
    let doc1 = context.createDoc()
    let array: YArray<YInt> = doc1.getArray(id: "array")
    array.insert(index: 0, content: [1,2,3,4])
    
    let data = context.encodeStateAsUpdate(doc: doc1)
    
    let doc2 = context.createDoc()
    context.applyUpdate(doc: doc2, buf: data)
    let array2: YArray<YInt> = doc2.getArray(id: "array")
    XCTAssert(array2.length == 4)
  }
  
}
