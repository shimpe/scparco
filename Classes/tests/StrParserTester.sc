/*
[general]
title = "StrParserTester"
summary = "unit tests for StrParser"
categories = "Parsing Tools"
related = "Classes/StrParser"
description = '''unit tests for StrParser'''
*/
StrParserTester : UnitTest {
	*new {
		^super.new.init();
	}

	test_singlestr_pass {
		var lookFor = "hello world!";
		var fullText = "hello world!";
		var p = StrParser.new(lookFor);
		var result = p.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		this.assert(lookFor.compare(result.result) == 0,
			onFailure:{lookFor + "!=" + result.result},
			message:"check if parse result is ok");
	}

	test_singlestr_excesstext {
		var lookFor = "hello world!";
		var fullText = "hello world!hello world!";
		var p = StrParser.new(lookFor);
		var result = p.run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		this.assert(lookFor.compare(result.result) == 0,
			onFailure:{lookFor + "!=" + result.result},
			message:"check if parse result is ok");
	}

	test_singlestr_notenoughtext {
		var lookFor = "hello world!hello world!";
		var fullText = "hello world!";
		var p = StrParser.new(lookFor);
		var result = p.run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		this.assert(result.result.isNil,
			onFailure:{lookFor + "!=" + result.result},
			message:"check if parse result is ok");
	}

	test_singlestr_wrongtext {
		var lookFor = "bye world!";
		var fullText = "hello world!";
		var p = StrParser.new(lookFor);
		var result = p.run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		this.assert(result.result.isNil,
			onFailure:{lookFor + "!=" + result.result},
			message:"check if parse result is ok");
	}

	test_singlestr_emptystr {
		var lookFor = "bye world!";
		var fullText = "";
		var p = StrParser.new(lookFor);
		var result = p.run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		this.assert(result.result.isNil,
			onFailure:{lookFor + "!=" + result.result},
			message:"check if parse result is ok")
	}

	test_singlestr_map {
		var lookFor = "bye world!";
		var fullText = "bye world!g'bye!";
		var p = StrParser.new(lookFor);
		var result = p.map({|txt| txt.toUpper; }).run(fullText);
		this.assertEquals(result.isError, false, message:"isError");
		this.assert(lookFor.toUpper.compare(result.result) == 0,
			onFailure:{lookFor.toUpper + "!=" + result.result},
			message:"check if parse result is ok");
	}

	test_singlestr_maperror {
		var lookFor = "bye wrld!";
		var fullText = "bye world!g'bye!";
		var p = StrParser.new(lookFor);
		var result = p.map({|txt| txt.toUpper; }).errorMap({|msg, idx| "At position" + idx + "couldn't find greeting!\n" ++ msg }).run(fullText);
		this.assertEquals(result.isError, true, message:"isError");
		this.assert(result.errorMsg.beginsWith("At position"));
		result.errorMsg.debug;
	}

    init {
    }
}