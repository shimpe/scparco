/*
[general]
title = "ScpRegexParserTester"
summary = "unit tests for ScpRegexParser"
categories = "Parsing Tools"
related = "Classes/ScpRegexParser"
description = '''unit tests for ScpRegexParser'''
*/
ScpRegexParserTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_singlestr_pass {
		var lookFor = "[a-z]+";
		var fullText = "hello world!";
		var p = ScpRegexParser.new(lookFor);
		var result = p.run(fullText);
		result.prettyprint;
		this.assertEquals(result.isError, false, message:"isError");
		this.assert("hello".compare(result.result) == 0,
			onFailure:{"hello !=" + result.result},
			message:"check if parse result is ok");
	}

    init {
    }
}