/*
[general]
title = "ScpErrorPropagationTester"
summary = "unit tests verifying that combinators surface the specific deepest sub-parser error"
categories = "Parsing Tools"
related = "Classes/ScpChoice, Classes/ScpSequenceOf, Classes/ScpManyOne, Classes/ScpLongestChoice"
description = '''unit tests verifying that, on failure, combinators report the specific error of the sub-parser that advanced furthest into the input (furthest-failure), instead of substituting a generic message'''
*/
ScpErrorPropagationTester : UnitTest {
	*new {
		^super.new.init();
	}

	// ScpSequenceOf: a failure inside the sequence must surface the failing
	// sub-parser's specific message, not a generic "Couldn't match sequence".
	test_sequenceof_surfaces_child_error {
		var p = ScpSequenceOf([ScpStrParser("("), ScpRegexParser("[a-z]+"), ScpStrParser(")")]);
		var r = p.run("(123)");
		this.assertEquals(r.isError, true, message: "sequenceof: isError");
		this.assert(r.errorMsg.find("[a-z]+").notNil,
			message: "sequenceof: surfaces the specific regex error");
		this.assert(r.errorMsg.find("Couldn't match sequence").isNil,
			message: "sequenceof: no generic message");
	}

	// ScpChoice: when all alternatives fail, surface a specific alternative error,
	// not "ScpChoice: unable to match with any parser".
	test_choice_surfaces_child_error {
		var c = ScpChoice([ScpStrParser("good morning Vietnam!"), ScpStrParser("bye world!")]);
		var r = c.run("hello world!goodbye world!");
		this.assertEquals(r.isError, true, message: "choice: isError");
		this.assert(r.errorMsg.find("expected to match").notNil,
			message: "choice: surfaces a specific alternative error");
		this.assert(r.errorMsg.find("ScpChoice").isNil,
			message: "choice: no generic message");
	}

	// ScpChoice furthest-failure: the reported error must come from the
	// alternative that advanced DEEPEST, not merely the last one tried.
	test_choice_reports_deepest_alternative {
		// altA matches "foo" then fails at index 3 expecting "XXX" (deep)
		// altB fails immediately at index 0
		var altA = ScpSequenceOf([ScpStrParser("foo"), ScpStrParser("XXX")]);
		var altB = ScpStrParser("zzz");
		var c = ScpChoice([altA, altB]);
		var r = c.run("foobar");
		this.assertEquals(r.isError, true, message: "choice-deepest: isError");
		this.assert(r.errorMsg.find("XXX").notNil,
			message: "choice-deepest: reports the furthest-advancing alternative (expects XXX)");
	}

	// ScpManyOne: zero matches must surface the child parser's specific error.
	test_manyone_surfaces_child_error {
		var p = ScpManyOne(ScpStrParser("ab"));
		var r = p.run("xyz");
		this.assertEquals(r.isError, true, message: "manyone: isError");
		this.assert(r.errorMsg.find("expected to match").notNil,
			message: "manyone: surfaces the child error");
		this.assert(r.errorMsg.find("ScpManyOne").isNil,
			message: "manyone: no generic message");
	}

	// ScpLongestChoice: when all alternatives fail, surface a specific error.
	test_longestchoice_surfaces_child_error {
		var c = ScpLongestChoice([ScpStrParser("good morning"), ScpStrParser("goodbye")]);
		var r = c.run("hello");
		this.assertEquals(r.isError, true, message: "longestchoice: isError");
		this.assert(r.errorMsg.find("expected to match").notNil,
			message: "longestchoice: surfaces a specific alternative error");
		this.assert(r.errorMsg.find("ScpLongestChoice").isNil,
			message: "longestchoice: no generic message");
	}

	// makeSepByOne: a failing first value must surface the value parser's error.
	test_sepbyone_surfaces_child_error {
		var p = ScpParserFactory.makeSepByOne(ScpStrParser(",")).(ScpRegexParser("[0-9]+"));
		var r = p.run("xyz");
		this.assertEquals(r.isError, true, message: "sepbyone: isError");
		this.assert(r.errorMsg.find("[0-9]+").notNil,
			message: "sepbyone: surfaces the value parser error");
		this.assert(r.errorMsg.find("SepByOne").isNil,
			message: "sepbyone: no generic message");
	}

	// Regression guard: a SUCCESSFUL parse must NOT be flagged as an error.
	// (Protects against reintroducing the run() regression.)
	test_success_not_flagged_as_error {
		var r = ScpParserFactory.makeIntegerParser.run("3.14");
		this.assertEquals(r.isError, false, message: "success-guard: isError must be false");
		this.assertEquals(r.result, 3, message: "success-guard: result is 3");
	}

	// run(consumeAll:) defaults to false: trailing unparsed input is allowed.
	test_consumeall_default_allows_trailing_input {
		var r = ScpParserFactory.makeIntegerParser.run("3.14");
		this.assertEquals(r.isError, false, message: "consumeAll-default: trailing input allowed");
		this.assertEquals(r.result, 3, message: "consumeAll-default: result is 3");
	}

	// run(consumeAll: true): trailing unparsed input becomes an error.
	test_consumeall_true_flags_trailing_input {
		var r = ScpParserFactory.makeIntegerParser.run("3.14", consumeAll: true);
		this.assertEquals(r.isError, true, message: "consumeAll-true: trailing input is an error");
		this.assert(r.errorMsg.find("consume all input").notNil,
			message: "consumeAll-true: reports the leftover-input error");
	}

	// run(consumeAll: true): fully consumed input still succeeds.
	test_consumeall_true_accepts_full_input {
		var r = ScpParserFactory.makeIntegerParser.run("42", consumeAll: true);
		this.assertEquals(r.isError, false, message: "consumeAll-true: full input ok");
		this.assertEquals(r.result, 42, message: "consumeAll-true: result is 42");
	}

	// run(consumeAll: true): a genuine parse error is preserved, not masked by the consumeAll message.
	test_consumeall_true_preserves_parse_error {
		var r = ScpParserFactory.makeIntegerParser.run("abc", consumeAll: true);
		this.assertEquals(r.isError, true, message: "consumeAll-true: parse failure still errors");
		this.assert(r.errorMsg.find("consume all input").isNil,
			message: "consumeAll-true: keeps the specific parse error");
	}

	// run(consumeAll: true) for binary targets: index is in BITS, size in BYTES.
	test_consumeall_true_binary_full_input_ok {
		var r = ScpParserFactory.makeBinaryUIntParser(8).run(Int8Array[0x41], consumeAll: true);
		this.assertEquals(r.isError, false, message: "consumeAll-true binary: full byte consumed ok");
		this.assertEquals(r.result, 65, message: "consumeAll-true binary: result is 65");
	}

	// run(consumeAll: true) for binary: a leftover byte is flagged (guards the *8 bit accounting).
	test_consumeall_true_binary_partial_flags {
		var r = ScpParserFactory.makeBinaryUIntParser(8).run(Int8Array[0x41, 0x42], consumeAll: true);
		this.assertEquals(r.isError, true, message: "consumeAll-true binary: leftover byte is an error");
	}

	// run(consumeAll: true): the consume-all note is APPENDED to the parser's own
	// stop reason, not substituted for it. Here ManyOne stops at 'x' and carries that
	// reason in errorMsg even though it succeeded; consumeAll must preserve it.
	test_consumeall_true_appends_not_replaces {
		var p = ScpManyOne(ScpRegexParser("[0-9]"));
		var r = p.run("12x", consumeAll: true);
		this.assertEquals(r.isError, true, message: "consumeAll-append: isError");
		this.assert(r.errorMsg.find("[0-9]").notNil,
			message: "consumeAll-append: keeps the parser's own stop reason");
		this.assert(r.errorMsg.find("consume all input").notNil,
			message: "consumeAll-append: appends the consume-all note");
	}

	// errorMapState: the callback receives the whole ScpParserState, so it can show code
	// context (target, index, ...). (errorMap with (msg, index) stays unchanged — see below.)
	test_errormapstate_exposes_state {
		var p, r;
		this.assert(ScpStrParser("x").respondsTo(\errorMapState),
			message: "errorMapState: method exists");
		if (ScpStrParser("x").respondsTo(\errorMapState)) {
			p = ScpStrParser("(").errorMapState({ |state|
				"expected '(' at index " ++ state.index ++ " in: " ++ state.target
			});
			r = p.run("abc");
			this.assertEquals(r.isError, true, message: "errorMapState: isError");
			this.assert(r.errorMsg.find("at index 0").notNil,
				message: "errorMapState: callback can read state.index");
			this.assert(r.errorMsg.find("in: abc").notNil,
				message: "errorMapState: callback can read state.target");
		};
	}

	// errorMapState must leave successful parses untouched.
	test_errormapstate_leaves_success_untouched {
		var p, r;
		if (ScpStrParser("x").respondsTo(\errorMapState)) {
			p = ScpStrParser("abc").errorMapState({ |state| "should not be used" });
			r = p.run("abc");
			this.assertEquals(r.isError, false, message: "errorMapState: success not affected");
			this.assert(r.result.compare("abc") == 0, message: "errorMapState: result preserved");
		} {
			this.assert(false, message: "errorMapState: method exists (success test)");
		};
	}

	// Backward compatibility: errorMap still passes (errorMsg, index).
	test_errormap_backward_compatible {
		var p = ScpStrParser("(").errorMap({ |msg, idx| "at " ++ idx ++ ": " ++ msg });
		var r = p.run("abc");
		this.assertEquals(r.isError, true, message: "errorMap: isError");
		this.assert(r.errorMsg.beginsWith("at 0: "),
			message: "errorMap: still receives (msg, index)");
	}

	init {
	}
}
