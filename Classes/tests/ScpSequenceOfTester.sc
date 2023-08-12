/*
[general]
title = "ScpSequenceOfTester"
summary = "unit tests for ScpSequenceOf"
categories = "Parsing Tools"
related = "Classes/ScpSequenceOf"
description = '''unit tests for ScpSequenceOf'''
*/
ScpSequenceOfTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_sequenceof_pass {
		var fullText = "hello world!goodbye world!";
		var lookFor = ["hello world!", "goodbye world!"];
		var p = ScpSequenceOf(lookFor.collect({|txt| ScpStrParser(txt) }));
		var result = p.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		lookFor.do {
			| txt, idx |
			this.assert(lookFor[idx].compare(result.result[idx]) == 0,
				onFailure:{lookFor[idx] + "!=" + result.result[idx]},
				message:"check if parse result is ok");
		}
	}

	test_sequenceof_pass_with_map {
		var fullText = "hello world!goodbye world!";
		var lookFor = ["hello world!", "goodbye world!"];
		var p = ScpSequenceOf([
			ScpStrParser(lookFor[0]),
			ScpStrParser(lookFor[1]).map({|txt| txt.toUpper; });
		]);
		var result = p.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		this.assert(lookFor[0].compare(result.result[0]) == 0,
			onFailure:{lookFor[0] + "!=" + result.result[0]},
			message:"check if parse result is ok");
		this.assert(lookFor[1].toUpper.compare(result.result[1]) == 0,
			onFailure:{lookFor[1].toUpper + "!=" + result.result[1]},
			message:"check if parse result is ok");
	}

	test_sequenceof_fail {
		var fullText = "hello world!goodbye world!slukes";
		var lookFor = ["hello world!", "goodbye worl!", "slukes"];
		var p = ScpSequenceOf(lookFor.collect({|txt| ScpStrParser(txt) }));
		var result = p.run(fullText);
		result.prettyprint;
		this.assertEquals(result.isError, true, message:"isError");
	}

    init {
    }
}