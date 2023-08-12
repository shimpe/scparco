/*
[general]
title = "ScpOptionalTester"
summary = "unit tests for ScpOptional"
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''unit tests for ScpOptional'''
*/
ScpOptionalTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_1 {
		var s1 = ScpStrParser("hello!");
		var s2 = ScpOptional(ScpStrParser("bye!"));
		var text = "hello!hello!";
		var seq = ScpSequenceOf([s1,s2,s1]);
		var result = seq.run(text); // [ "hello!", nil, "hello!" ]
		this.assertEquals(result.isError, false);
		this.assert(result.result[0].compare("hello!") == 0);
		this.assert(result.result[1] == nil);
		this.assert(result.result[2].compare("hello!") == 0);
	}

    init {
    }
}