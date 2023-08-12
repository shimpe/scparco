/*
[general]
title = "ScpBinaryTests"
summary = "unit tests for binary parsing classes"
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''unit tests for binary parsers'''
*/
ScpBinaryTests : UnitTest {
	*new {
		^super.new.init();
	}
	init
	{
	}

	test_binarybit {
		var p1 = ScpMany(ScpBinaryBit());
		var r1 = p1.run(Int8Array[2r11101011]);
		var r2 = p1.run(Int8Array[10,8]);
		this.assertEquals(r1.result, [1, 1, 1, 0, 1, 0, 1, 1]);
		this.assertEquals(r2.result, [0,0,0,0,1,0,1,0,0,0,0,0,1,0,0,0]);
	}

	test_binaryone {
		var p1 = ScpMany(ScpBinaryOne());
		var r1 = p1.run(Int8Array[2r11101011]);
		var r2 = p1.run(Int8Array[2r01010101]);
		this.assertEquals(r1.result, [1, 1, 1]);
		this.assertEquals(r2.result, []);
	}

	test_binaryzero {
		var p1 = ScpMany(ScpBinaryZero());
		var r1 = p1.run(Int8Array[2r00000000, 2r00001111]);
		this.assertEquals(r1.result, [0,0,0,0,0,0,0,0,0,0,0,0]);
	}

	test_sysex {
		var t = Int8Array[0xF7, 0x00, 0x01, 0x41, 0xF0]; // contrived example containing only manufacturer's id
		var startOfSysex = ScpParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7]);
		var manufacturerId = ScpParserFactory.makeBinaryUIntParser(8).chain({
				| result |
				// using chain we can influence the next parser:
				// if the first byte is 0x00, two more bytes will follow with the manufacturer id
				// otherwise, the byte value itself is already the manufacturer's id
				if (result == 0) {
					// extract extended ID and tag it as \manufacturerId in the parse result
					ScpParserFactory.makeBinaryUIntParser(16).map(ScpMapFactory.tag(\manufacturerId));
				} {
					// current byte value is already the ID; tag it as \manufacturerId in the parse result
					ScpSucceedParser(result).map(ScpMapFactory.tag(\manufacturerId));
				}
		});
		var sysexParser = ScpSequenceOf([startOfSysex, manufacturerId]).map({|result| result[1] }); // keep only manufacturer's id
		var result = sysexParser.run(t);
		this.assertEquals(result.result[\manufacturerId], 0x0141);
	}
}