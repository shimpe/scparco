/*
[general]
title = "ScpParserFactory"
summary = "a factory for commonly used parsers, made from smaller library provided parsers"
categories = "ScpParser Tools"
related = "Classes/ScpParser"
description = '''
ScpParserFactory is a util that can create some parsers and families of parsers for common use cases. These parsers are built as combinations of more primitive library provided parsers. In some cases (e.g. html tag parser), you may want to build your own parser to make it easier to extract all information. ScpParserFactory only implements classmethods, meaning you can call them using the syntax ScpParserFactory.makeIntegerParser (i.e. there's no need to create a ScpParserFactory instance first).
'''
*/
ScpParserFactory {
	/*
	[classmethod.new]
	description = "creates a new ScpParserFactory"
	[classmethod.new.returns]
	what = "a new ScpParserFactory"
	*/
	*new {
		^super.new.init();
	}

	/*
	[method.init]
	description = "initializes the parser factory (but in reality it does nothing :))"
	[method.init.returns]
	what = "ScpParserFactory"
	*/
	init {
	}

	/*
	[classmethod.makeBetween]
	description = '''creates a parser factory that can extract something between two other parsers.
	So, given a string and 3 parsers [LeftParser, ContentParser, RightParser], it will return only what is found by the ContentParser. Think of a value between brackets, where you are only interested in what is between the brackets (see example code).

	The result of a call to makeBetween is not a complete parser yet. It is a function that expects the ContentParser as argument. This allows to prepare a "between parser", where the LeftParser and RightParser are specified when makeBetween is called, and the ContentParser is specified at the moment of finalizing the parser. To finalize the parser, call the function return by makeBetween with a ContentParser as argument.'''

	[classmethod.makeBetween.args]
	leftParser = "a ScpParser that matches whatever should be left of the content you are interested in"
	rightParser = "a ScpParser that matches whatever should be right of the content you are interested in"

	[classmethod.makeBetween.returns]
	what = '''a function that, when called with a ContentParser as argument, will produce a new parser that matches first LeftParser, then ContentParser, then RightParser and keeps only the result of the ContentParser.'''
	*/
	*makeBetween {
		| leftParser, rightParser |
		^{
			| contentParser |
			var newP = ScpSequenceOf([
				leftParser,
				contentParser,
				rightParser])
			.map({|results| results[1]});
			// map keeps only the result of the ContentParser and throws away the results of LeftParser and RightParser
			newP;
		}
	}

	/*
	[classmethod.makeIntegerParser]
	description = "class to parse an integer number and convert it from string to Integer"
	[classmethod.makeIntegerParser.returns]
	what = "ScpParser that can parse a (signed) integer number"
	*/
	*makeIntegerParser {
		^ScpRegexParser("[+-]?[0-9]+").map({|txt| txt.asInteger });
	}

	/*
	[classmethod.makePositiveIntegerParser]
	description = "class to parse an unsigned integer number and convert it from string to Integer"
	[classmethod.makePositiveIntegerParser.returns]
	what = "ScpParser that can parse an (usigned) integer number"
	*/
	*makePositiveIntegerParser {
		^ScpRegexParser("[+]?[0-9]+").map({|txt| txt.asInteger });
	}


	/*
	[classmethod.makeFloatParser]
	description = "class to parse a float number and convert it from string to Float"
	[classmethod.makeFloatParser.returns]
	what = "ScpParser that can parse a (signed) float number"
	*/
	*makeFloatParser {
		^ScpRegexParser("[+-]?([0-9]*[.])?[0-9]+").map({|txt| txt.asFloat });
	}


	/*
	[classmethod.makePositiveFloatParser]
	description = "class to parse a positive float number and convert it from string to Float"
	[classmethod.makePositiveFloatParser.returns]
	what = "ScpParser that can parse an (unsigned) float number"
	*/
	*makePositiveFloatParser {
		^ScpRegexParser("[+]?([0-9]*[.])?[0-9]+").map({|txt| txt.asFloat });
	}

	/*
	[classmethod.makeEmailParser]
	description = "class to parse an email address"
	[classmethod.makeEmailParser.returns]
	what = "ScpParser that can parse an email address (some extremely exotic uses cases are excluded)"
	*/
	*makeEmailParser {
		^ScpRegexParser("[a-z0-9!#$%&\'*+/=?^_‘{|}~-]+(\\\\.[a-z0-9!#$%&\'*+/=?^_‘{|}~-]+)*@([a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
	}

	/*
	[classmethod.makeUrlParser]
	description = "class to parse a URL"
	[classmethod.makeUrlParser.returns]
	what = "ScpParser that can parse a URL"
	*/
	*makeUrlParser {
		^ScpRegexParser("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#()?&//=]*)");
	}

	/*
	[classmethod.makeLetters]
	description = "class to parse one or more letters"
	[classmethod.makeLetters.returns]
	what = "ScpParser that can parse one or more letters"
	*/
	*makeLetters {
		^ScpRegexParser("[a-zA-Z]+");
	}

	/*
	[classmethod.makeDigits]
	description = "class to parse one or more digits"
	[classmethod.makeDigits.returns]
	what = "ScpParser that can parse one or more digits"
	*/
	*makeDigits {
		^ScpRegexParser("[0-9]+");
	}

	/*
	[classmethod.makeAlphanumeric]
	description = "class to parse an alphanumeric value"
	[classmethod.makeAlphanumeric.returns]
	what = "ScpParser that can parse an alphanumeric value"
	*/
	*makeAlphanumeric {
		^ScpRegexParser("[a-zA-Z0-9]+");
	}

	/*
	[classmethod.makeIpAddressParser]
	description = "class to parse an IPv4/IPv6 address"
	[classmethod.makeIpAddressParser.returns]
	what = "ScpParser that can parse an IPv4/IPv6 address"
	*/
	*makeIpAddressParser {
		^ScpRegexParser("((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))");
	}

	/*
	[classmethod.makeYYYYMMddParser]
	description = "class to parse an YYY-MM-dd date (separator - . or /)"
	[classmethod.makeYYYYMMddParser.returns]
	what = "ScpParser that can parse an an YYY-MM-dd date "
	*/
	*makeYYYYMMddParser {
		^ScpRegexParser("([12]\\d{3}(\\/|-|\\.)(0[1-9]|1[0-2])(\\/|-|\\.)(0[1-9]|[12]\\d|3[01]))");
	}

	/*
	[classmethod.makeddMMYYYYParser]
	description = "class to parse an dd-MM-YYYY parser (separators - . or /) "
	[classmethod.makeddMMYYYYParser.returns]
	what = "ScpParser that can parse an dd-MM-YYYY date"
	*/
	*makeddMMYYYYParser {
		^ScpRegexParser("(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})");
	}

	/*
	[classmethod.makeHtmlTagParser]
	description = "class to match an html tag (with attributes). The tag with attributes is matched as a whole. No subparsers are present to find the individual attributes."
	[classmethod.makeHtmlTagParser.returns]
	what = "ScpParser that can parse an html tag with attributes"
	*/
	*makeHtmlTagParser {
		^ScpRegexParser("<\\/?[\\w\\s]*>|<.+[\\W]>");
	}

	/*
	[classmethod.makeWs]
	description = "class to match zero or more whitespace characters"
	[classmethod.makeWs.returns]
	what = '''ScpParser that parses multiple whitespace characters [\t\r\n\f]'''
	*/
	*makeWs {
		^ScpOptional(ScpRegexParser("\\s+"));
	}

	/*
	[classmethod.makeWsOne]
	description = "class to match one or more whitespace characters"
	[classmethod.makeWsOne.returns]
	what = '''ScpParser that parses multiple whitespace characters [\t\r\n\f]'''
	*/
	*makeWsOne {
		^ScpRegexParser("\\s+");
	}

	/*
	[classmethod.makeIdentifierParser]
	description = "class to match an identifier as used in many programming languages"
	[classmethod.makeIdentifierParser.returns]
	what = '''ScpParser that parses an identifier'''
	*/
	*makeIdentifierParser {
		^ScpRegexParser("[_a-zA-Z][_a-zA-Z0-9]*");
	}

	/*
	[classmethod.makeNewlineParser]
	description = "class to match a newline independent of platform"
	[classmethod.makeNewlineParser.returns]
	what = '''ScpParser that parses a newline'''
	*/
	*makeNewlineParser {
		^ScpChoice([ScpStrParser("\r\n"), ScpStrParser("\n"), ScpStrParser("\r")]);
	}

	/*
	[classmethod.makeSepBy]
	description = "generates a function that expects a ValueParser and produces a parser that matches zero or more separated values (see example code)."
	[classmethod.makeSepBy.args]
	separatorParser = "a ScpParser that matches whatever separates the values we are interested in"
	[classmethod.makeSepBy.returns]
	what = "a function that expects a ValueParser as argument and produces a ScpParser that matches zero or more separated values"
	*/
	*makeSepBy {
		| separatorParser |
		^{
			| valueParser |
			var newP = ScpParser();
			newP.parserStateTransformer = {
				| parserState |
				var results = [];
				var nextState = parserState;
				var keepTrying = true;
				if (parserState.isError) {
					parserState;
				}{
					while ({keepTrying}) {
						var thingWeWantState = valueParser.parserStateTransformer.(nextState);
						if (thingWeWantState.isError) {
							keepTrying = false;
						} {
							var separatorState;
							results = results.add(thingWeWantState.result);
							nextState = thingWeWantState;
							separatorState = separatorParser.parserStateTransformer.(nextState);
							if (separatorState.isError) {
								keepTrying = false;
							} {
								nextState = separatorState;
							}
						}
					};

					nextState = nextState.updateResult(results);

				}
			};
			newP;
		}
	}

	/*
	[classmethod.makeSepByOne]
	description = "generates a function that expects a ValueParser and produces a parser that matches one or more separated values (see example code)."
	[classmethod.makeSepByOne.args]
	separatorParser = "a ScpParser that matches whatever separates the values we are interested in"
	[classmethod.makeSepByOne.returns]
	what = "a function that expects a ValueParser as argument and produces a ScpParser that matches one or more separated values"
	*/
	*makeSepByOne {
		| separatorParser |
		^{
			| valueParser |
			var newP = ScpParser();
			newP.parserStateTransformer = {
				| parserState |
				var results = [];
				var nextState = parserState;
				var keepTrying = true;
				if (parserState.isError) {
					parserState;
				}{
					while ({keepTrying}) {
						var thingWeWantState = valueParser.parserStateTransformer.(nextState);
						if (thingWeWantState.isError) {
							keepTrying = false;
						} {
							var separatorState;
							results = results.add(thingWeWantState.result);
							nextState = thingWeWantState;
							separatorState = separatorParser.parserStateTransformer.(nextState);
							if (separatorState.isError) {
								keepTrying = false;
							} {
								nextState = separatorState;
							}
						}
					};

					if (results.size == 0) {
						nextState = nextState.updateError("SepByOne: Unable to capture any results at index" + nextState.index);
					} {
						nextState = nextState.updateResult(results);
					};
					nextState;
				}
			};
			newP;
		}
	}

	/*
	[classmethod.makeBinaryUIntParser]
	description = "generates a parser that extracts an n-bit unsigned integer from a binary message(contained in an Int8Array)."
	[classmethod.makeBinaryUIntParser.args]
	no_of_bits = "an integer in the interval [1,32]"
	[classmethod.makeBinaryUIntParser.returns]
	what = "a ScpParser that extracts an n-bit unsigned integer from a binary message (contained in an Int8Array)"
	*/
	*makeBinaryUIntParser {
		| no_of_bits |
		if ((no_of_bits < 1) || (no_of_bits > 32))
		{
			"Error! makeBinaryUIntParser argument n must be 1 <= n <= 32".postln;
		} {
			^ScpSequenceOf(ScpBinaryBit() ! no_of_bits).map({
				|bits|
				(bits.collect({ | bit, idx | (bit << (no_of_bits - 1 - idx) ) })).sum;
			})
		};
	}

	/*
	[classmethod.makeBinaryIntParser]
	description = "generates a parser that extracts an n-bit signed integer from a binary message(contained in an Int8Array)."
	[classmethod.makeBinaryIntParser.args]
	no_of_bits = "an integer in the interval [1,32]"
	[classmethod.makeBinaryIntParser.returns]
	what = "a ScpParser that extracts an n-bit signed integer from a binary message (contained in an Int8Array)"
	*/
	*makeBinaryIntParser {
		| no_of_bits |
		if ((no_of_bits < 1) || (no_of_bits > 32))
		{
			"Error! makeBinaryIntParser argument n must be 1 <= n <= 32".postln;
		} {
			^ScpSequenceOf(ScpBinaryBit() ! no_of_bits).map({
				|bits|
				if (bits[0] == 0) {
					// sign bit is 0 -> treat same as uint
					(bits.collect({ | bit, idx | (bit << (no_of_bits - 1 - idx) ) })).sum;
				} {
					// sign bit is 1
					((bits.collect({ | bit, idx | (bit << (no_of_bits - 1 - idx) ) })).sum + 1).neg;
				};
			});
		};
	}

	/*
	[classmethod.makeBinaryRawStringParser]
	description = "generates a parser that matches a literal string (e.g. a magic marker) in a binary message(contained in an Int8Array)."
	[classmethod.makeBinaryRawStringParser.args]
	s = "string to match"
	[classmethod.makeBinaryRawStringParser.returns]
	what = "a ScpParser that extracts matches a literal string in a binary message (contained in an Int8Array)"
	*/
	*makeBinaryRawStringParser {
		| s |
		if (s.size < 1) {
			"Error! makeBinaryRawStringParser expects a string argument with at least one character.".postln;
		} {
			var byteParsers = s.ascii.collect({
				|c|
				ScpParserFactory.makeBinaryUIntParser(8).chain({
					| result |
					if (result == c) {
						ScpSucceedParser(result);
					} {
						ScpFailParser("BinaryRawStringParser: Expected character "+ c.asAscii + "but got" + result.asAscii + "instead");
					};
				});
			});
			^ScpSequenceOf(byteParsers);
		};
	}

	/*
	[classmethod.makeBinaryLiteralInt8ArrayParser]
	description = "generates a parser that matches a literal Int8Array (e.g. a magic marker) in a binary message(contained in an Int8Array)."
	[classmethod.makeBinaryLiteralInt8ArrayParser.args]
	a = "array to match"
	[classmethod.makeBinaryLiteralInt8ArrayParser.returns]
	what = "a ScpParser that extracts matches a literal Int8Array in a binary message (contained in an Int8Array)"
	*/
	*makeBinaryLiteralInt8ArrayParser {
		| a |
		if (a.size < 1) {
			"Error! makeBinaryLiteralInt8ArrayParser expects an Int8Array argument with at least one byte value.".postln;
		} {
			var byteParsers = a.collect({
				|c|
				ScpParserFactory.makeBinaryUIntParser(8).chain({
					| result |
					if (result == c.wrap(0,255)) { // .wrap(0,255) converts from signed to unsigned byte value
						ScpSucceedParser(result);
					} {
						ScpFailParser("BinaryRawLiteralInt8ArrayParser: Expected byte value "+ a.asHexString + "but got" + result.asHexString + "instead");
					};
				});
			});
			^ScpSequenceOf(byteParsers);
		}
	}

	/*
	[classmethod.forwardRef]
	description = "forwardRef is used in recursive parsers, since without forward reference it's impossible to use parsers that have not been defined yet leading to a chicken-and-egg problem (in a recursive parser, parser A refers to parser B, but parser B refers to parser A again)."
	[classmethod.forwardRef.args]
	parserThunk = "a Thunk (= unevaluated function) that, when evaluated, will produce a parser. The Thunk can contain parsers that have not yet been defined in the code (see example code)."
	[classmethod.forwardRef.returns]
	what = "a parser wrapper that will evaluate to an actual parser only when it is being used during parsing. At that moment in time, all parsers it refers to must have been instantiated and initialized. This allows for making parser A depend on parser B, where parser B depends on parser A (circular dependencies, which often occur when defining recursive parsers)."
	*/
	*forwardRef {
		| parserThunk |
		var newP = ScpParser();
		newP.parserStateTransformer = {
			| parserState |
			// only now (while parsing is already ongoing) the real parser is instantiated and run
			// since by now, all parsers referred to in the thunk have been initialized
			var parser = parserThunk.value;
			var nextState = parser.parserStateTransformer.(parserState);
			nextState;
		};
		^newP;
	}

	/*
	[examples]
	what = '''
	(
	var intp = ScpParserFactory.makeIntegerParser;
	var result = intp.run("3.14"); // expected result: 3
	result.result.postcs;
	)
	(
	var floatp = ScpParserFactory.makeFloatParser;
	var result = floatp.run("3.14"); // expected result: 3.14
	result.result.postcs;
	)
	(
	var emailp = ScpParserFactory.makeEmailParser;
	var result = emailp.run("fubar.smalltalk@brainiac.co.uk");
	result.result.postcs; // result: "fubar.smalltalk@brainiac.co.uk"
	)
	(
	var urlp = ScpParserFactory.makeUrlParser;
	var result = urlp.run("https://www.google.com/givemebreak?parameter=value&&parameter2=56");
	result.result.postcs; // result: "https://www.google.com/givemebreak?parameter=value&&parameter2=56"
	)
	(
	var ip = ScpParserFactory.makeIpAddressParser;
	var result = ip.run("127.0.0.1");
	var result2 = ip.run("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
	result.result.postcs;
	result2.result.postcs;
	)
	(
	var ymd = ScpParserFactory.makeYYYYMMddParser;
	var result = ymd.run("2023-06-02");
	result.result.postcs;
	)
	(
	var dmy = ScpParserFactory.makeddMMYYYYParser;
	var result = dmy.run("13/02/1975");
	result.result.postcs;
	)
	(
	var tag = ScpParserFactory.makeHtmlTagParser;
	var result = tag.run("<element value=\"56\">");
	result.result.postcs;
	)
	(
	var betweenBrackets = ScpParserFactory.makeBetween(ScpStrParser("("), ScpStrParser(")"));
	var lettersBetweenBrackets = betweenBrackets.(ScpRegexParser("[A-Za-z]+"));
	var result = lettersBetweenBrackets.run("(foobarsnafu)"); // result should be "foobarsnafu"
	result.result.postcs;
	)
	(
	var commasep = ScpParserFactory.makeSepBy(ScpStrParser(","));
	var csv_ints = commasep.(ScpParserFactory.makeIntegerParser);
	var result = csv_ints.run("45,78,-93,12"); // result should be [45, 78, -93, 12]
	result.result.postcs;
    )
    (
	// both separators and values can be more complex parsers as well
	var commacolonsep = ScpParserFactory.makeSepBy(ScpChoice([ScpStrParser(","), ScpStrParser(";")]));
	var csv_numbers = commacolonsep.(ScpParserFactory.makeFloatParser);
	var text = "45,-89.23;56.7,67;90.09";
	var result = csv_numbers.run(text); // result should be [ 45.0, -89.23, 56.7, 67.0, 90.09 ]
	result.result.postcs;
    )
	(
	var betweenSquareBrackets = ScpParserFactory.makeBetween(ScpStrParser("["), ScpStrParser("]"));
	var commaSeparated = ScpParserFactory.makeSepBy(ScpStrParser(","));
	var digitparser = ScpParserFactory.makeIntegerParser;
	var nested_array_parser = ScpParserFactory.forwardRef(Thunk({
	// Thunk makes sure its inside is not evaluated yet
	// and forwardRef "disguises" (wraps) the Thunk as a ScpParser
	//
	// We need a forward ref here to get around a mutual dependency problem
	// namely: nested_array_element uses flat_arrayparser, but
	// flat_arrayparser uses nested_array_element... (= mutually recursive definition).
	ScpChoice([
	    digitparser,
		arrayparser])
	}));
	var arrayparser = betweenSquareBrackets.(commaSeparated.(nested_array_parser));
	var result = arrayparser.run("[1,[2,3,[4,5],6],7,[8],9]"); // expected result: [ 1, [ 2, 3, [ 4, 5 ], 6 ], 7, [ 8 ], 9 ]
	result.result.postcs;
	)
	(
	var t = Int8Array[0xF7, 0x00, 0x00, 0x41, 0xF0];
	var pl = [ScpParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7])].addAll(ScpBinaryBit() ! 8);
	var p2 = [ScpParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7])].add(ScpParserFactory.makeBinaryIntParser(8));
	var sysexParser = ScpSequenceOf([
	ScpParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7])].add(
	ScpParserFactory.makeBinaryIntParser(8).chain({
	    | result |
	    // using chain we can influence the next parser:
	    // if the first byte is 0x00, two more bytes will follow with the manufacturer id
	    // otherwise, the byte value itself is already the manufacturer's id
	    if (result == 0) {
	        // extract extended ID and tag it as \manufacturerId in the parse result
	        ScpParserFactory.makeBinaryUIntParser(16).map(ScpMapFactory.tag(\manufacturerId));
	    } {
	        // current ID is already extracted; tag it as \manufacturerId in the parse result
	        ScpSucceedParser(result).map(ScpMapFactory.tag(\manufacturerId));
	    }
	}))).map({|result| result[1]});
	var result;
	result = ScpSequenceOf(sysexParser).run(t);
	result.result.postcs;
	)

	'''
	*/

}