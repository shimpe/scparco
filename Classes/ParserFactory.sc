/*
[general]
title = "ParserFactory"
summary = "a factory for commonly used parsers, made from smaller library provided parsers"
categories = "Parser Tools"
related = "Classes/Parser"
description = '''
ParserFactory is a util that can create some parsers and families of parsers for common use cases. These parsers are built as combinations of more primitive library provided parsers. In some cases (e.g. html tag parser), you may want to build your own parser to make it easier to extract all information. ParserFactory only implements classmethods, meaning you can call them using the syntax ParserFactory.makeIntegerParser (i.e. there's no need to create a ParserFactory instance first).
'''
*/
ParserFactory {
	/*
	[classmethod.new]
	description = "creates a new ParserFactory"
	[classmethod.new.returns]
	what = "a new ParserFactory"
	*/
	*new {
		^super.new.init();
	}

	/*
	[method.init]
	description = "initializes the parser factory (but in reality it does nothing :))"
	[method.init.returns]
	what = "ParserFactory"
	*/
	init {
	}

	/*
	[classmethod.makeBetween]
	description = '''creates a parser factory that can extract something between two other parsers.
	So, given a string and 3 parsers [LeftParser, ContentParser, RightParser], it will return only what is found by the ContentParser. Think of a value between brackets, where you are only interested in what is between the brackets (see example code).

	The result of a call to makeBetween is not a complete parser yet. It is a function that expects the ContentParser as argument. This allows to prepare a "between parser", where the LeftParser and RightParser are specified when makeBetween is called, and the ContentParser is specified at the moment of finalizing the parser. To finalize the parser, call the function return by makeBetween with a ContentParser as argument.'''

	[classmethod.makeBetween.args]
	leftParser = "a Parser that matches whatever should be left of the content you are interested in"
	rightParser = "a Parser that matches whatever should be right of the content you are interested in"

	[classmethod.makeBetween.returns]
	what = '''a function that, when called with a ContentParser as argument, will produce a new parser that matches first LeftParser, then ContentParser, then RightParser and keeps only the result of the ContentParser.'''
	*/
	*makeBetween {
		| leftParser, rightParser |
		^{
			| contentParser |
			var newP = SequenceOf([
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
	what = "Parser that can parse a (signed) integer number"
	*/
	*makeIntegerParser {
		^RegexParser("[+-]?[0-9]+").map({|txt| txt.asInteger });
	}

	/*
	[classmethod.makeFloatParser]
	description = "class to parse a float number and convert it from string to Float"
	[classmethod.makeFloatParser.returns]
	what = "Parser that can parse a (signed) float number"
	*/
	*makeFloatParser {
		^RegexParser("[+-]?([0-9]*[.])?[0-9]+").map({|txt| txt.asFloat });
	}

	/*
	[classmethod.makeEmailParser]
	description = "class to parse an email address"
	[classmethod.makeEmailParser.returns]
	what = "Parser that can parse an email address (some extremely exotic uses cases are excluded)"
	*/
	*makeEmailParser {
		^RegexParser("[a-z0-9!#$%&\'*+/=?^_‘{|}~-]+(\\\\.[a-z0-9!#$%&\'*+/=?^_‘{|}~-]+)*@([a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
	}

	/*
	[classmethod.makeUrlParser]
	description = "class to parse a URL"
	[classmethod.makeUrlParser.returns]
	what = "Parser that can parse a URL"
	*/
	*makeUrlParser {
		^RegexParser("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#()?&//=]*)");
	}

	/*
	[classmethod.makeLetters]
	description = "class to parse one or more letters"
	[classmethod.makeLetters.returns]
	what = "Parser that can parse one or more letters"
	*/
	*makeLetters {
		^RegexParser("[a-zA-Z]+");
	}

	/*
	[classmethod.makeDigits]
	description = "class to parse one or more digits"
	[classmethod.makeDigits.returns]
	what = "Parser that can parse one or more digits"
	*/
	*makeDigits {
		^RegexParser("[0-9]+");
	}

	/*
	[classmethod.makeAlphanumeric]
	description = "class to parse an alphanumeric value"
	[classmethod.makeAlphanumeric.returns]
	what = "Parser that can parse an alphanumeric value"
	*/
	*makeAlphanumeric {
		^RegexParser("[a-zA-Z0-9]+");
	}

	/*
	[classmethod.makeIpAddressParser]
	description = "class to parse an IPv4/IPv6 address"
	[classmethod.makeIpAddressParser.returns]
	what = "Parser that can parse an IPv4/IPv6 address"
	*/
	*makeIpAddressParser {
		^RegexParser("((^\\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\\s*$)|(^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$))");
	}

	/*
	[classmethod.makeYYYYMMddParser]
	description = "class to parse an YYY-MM-dd date (separator - . or /)"
	[classmethod.makeYYYYMMddParser.returns]
	what = "Parser that can parse an an YYY-MM-dd date "
	*/
	*makeYYYYMMddParser {
		^RegexParser("([12]\\d{3}(\\/|-|\\.)(0[1-9]|1[0-2])(\\/|-|\\.)(0[1-9]|[12]\\d|3[01]))");
	}

	/*
	[classmethod.makeddMMYYYYParser]
	description = "class to parse an dd-MM-YYYY parser (separators - . or /) "
	[classmethod.makeddMMYYYYParser.returns]
	what = "Parser that can parse an dd-MM-YYYY date"
	*/
	*makeddMMYYYYParser {
		^RegexParser("(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})");
	}

	/*
	[classmethod.makeHtmlTagParser]
	description = "class to match an html tag (with attributes). The tag with attributes is matched as a whole. No subparsers are present to find the individual attributes."
	[classmethod.makeHtmlTagParser.returns]
	what = "Parser that can parse an html tag with attributes"
	*/
	*makeHtmlTagParser {
		^RegexParser("<\\/?[\\w\\s]*>|<.+[\\W]>");
	}

	/*
	[classmethod.makeWs]
	description = "class to match zero or more whitespace characters"
	[classmethod.makeWs.returns]
	what = '''Parser that parses multiple whitespace characters [\t\r\n\f]'''
	*/
	*makeWs {
		^Optional(RegexParser("\\s+"));
	}

	/*
	[classmethod.makeWsOne]
	description = "class to match one or more whitespace characters"
	[classmethod.makeWsOne.returns]
	what = '''Parser that parses multiple whitespace characters [\t\r\n\f]'''
	*/
	*makeWsOne {
		^RegexParser("\\s+");
	}

	/*
	[classmethod.makeIdentifierParser]
	description = "class to match an identifier as used in many programming languages"
	[classmethod.makeIdentifierParser.returns]
	what = '''Parser that parses an identifier'''
	*/
	*makeIdentifierParser {
		^RegexParser("[_a-zA-Z][_a-zA-Z0-9]*");
	}

	/*
	[classmethod.makeSepBy]
	description = "generates a function that expects a ValueParser and produces a parser that matches zero or more separated values (see example code)."
	[classmethod.makeSepBy.args]
	separatorParser = "a Parser that matches whatever separates the values we are interested in"
	[classmethod.makeSepBy.returns]
	what = "a function that expects a ValueParser as argument and produces a Parser that matches zero or more separated values"
	*/
	*makeSepBy {
		| separatorParser |
		^{
			| valueParser |
			var newP = Parser();
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
	separatorParser = "a Parser that matches whatever separates the values we are interested in"
	[classmethod.makeSepByOne.returns]
	what = "a function that expects a ValueParser as argument and produces a Parser that matches one or more separated values"
	*/
	*makeSepByOne {
		| separatorParser |
		^{
			| valueParser |
			var newP = Parser();
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
	what = "a Parser that extracts an n-bit unsigned integer from a binary message (contained in an Int8Array)"
	*/
	*makeBinaryUIntParser {
		| no_of_bits |
		if ((no_of_bits < 1) || (no_of_bits > 32))
		{
			"Error! makeBinaryUIntParser argument n must be 1 <= n <= 32".postln;
		} {
			^SequenceOf(BinaryBit() ! no_of_bits).map({
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
	what = "a Parser that extracts an n-bit signed integer from a binary message (contained in an Int8Array)"
	*/
	*makeBinaryIntParser {
		| no_of_bits |
		if ((no_of_bits < 1) || (no_of_bits > 32))
		{
			"Error! makeBinaryIntParser argument n must be 1 <= n <= 32".postln;
		} {
			^SequenceOf(BinaryBit() ! no_of_bits).map({
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
	what = "a Parser that extracts matches a literal string in a binary message (contained in an Int8Array)"
	*/
	*makeBinaryRawStringParser {
		| s |
		if (s.size < 1) {
			"Error! makeBinaryRawStringParser expects a string argument with at least one character.".postln;
		} {
			var byteParsers = s.ascii.collect({
				|c|
				ParserFactory.makeBinaryUIntParser(8).chain({
					| result |
					if (result == c) {
						SucceedParser(result);
					} {
						FailParser("BinaryRawStringParser: Expected character "+ c.asAscii + "but got" + result.asAscii + "instead");
					};
				});
			});
			^SequenceOf(byteParsers);
		};
	}

	/*
	[classmethod.makeBinaryLiteralInt8ArrayParser]
	description = "generates a parser that matches a literal Int8Array (e.g. a magic marker) in a binary message(contained in an Int8Array)."
	[classmethod.makeBinaryLiteralInt8ArrayParser.args]
	a = "array to match"
	[classmethod.makeBinaryLiteralInt8ArrayParser.returns]
	what = "a Parser that extracts matches a literal Int8Array in a binary message (contained in an Int8Array)"
	*/
	*makeBinaryLiteralInt8ArrayParser {
		| a |
		if (a.size < 1) {
			"Error! makeBinaryLiteralInt8ArrayParser expects an Int8Array argument with at least one byte value.".postln;
		} {
			var byteParsers = a.collect({
				|c|
				ParserFactory.makeBinaryUIntParser(8).chain({
					| result |
					if (result == c.wrap(0,255)) { // .wrap(0,255) converts from signed to unsigned byte value
						SucceedParser(result);
					} {
						FailParser("BinaryRawLiteralInt8ArrayParser: Expected byte value "+ a.asHexString + "but got" + result.asHexString + "instead");
					};
				});
			});
			^SequenceOf(byteParsers);
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
		var newP = Parser();
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
	var intp = ParserFactory.makeIntegerParser;
	var result = intp.run("3.14"); // expected result: 3
	result.result.postcs;
	)
	(
	var floatp = ParserFactory.makeFloatParser;
	var result = floatp.run("3.14"); // expected result: 3.14
	result.result.postcs;
	)
	(
	var emailp = ParserFactory.makeEmailParser;
	var result = emailp.run("fubar.smalltalk@brainiac.co.uk");
	result.result.postcs; // result: "fubar.smalltalk@brainiac.co.uk"
	)
	(
	var urlp = ParserFactory.makeUrlParser;
	var result = urlp.run("https://www.google.com/givemebreak?parameter=value&&parameter2=56");
	result.result.postcs; // result: "https://www.google.com/givemebreak?parameter=value&&parameter2=56"
	)
	(
	var ip = ParserFactory.makeIpAddressParser;
	var result = ip.run("127.0.0.1");
	var result2 = ip.run("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
	result.result.postcs;
	result2.result.postcs;
	)
	(
	var ymd = ParserFactory.makeYYYYMMddParser;
	var result = ymd.run("2023-06-02");
	result.result.postcs;
	)
	(
	var dmy = ParserFactory.makeddMMYYYYParser;
	var result = dmy.run("13/02/1975");
	result.result.postcs;
	)
	(
	var tag = ParserFactory.makeHtmlTagParser;
	var result = tag.run("<element value=\"56\">");
	result.result.postcs;
	)
	(
	var betweenBrackets = ParserFactory.makeBetween(StrParser("("), StrParser(")"));
	var lettersBetweenBrackets = betweenBrackets.(RegexParser("[A-Za-z]+"));
	var result = lettersBetweenBrackets.run("(foobarsnafu)"); // result should be "foobarsnafu"
	result.result.postcs;
	)
	(
	var commasep = ParserFactory.makeSepBy(StrParser(","));
	var csv_ints = commasep.(ParserFactory.makeIntegerParser);
	var result = csv_ints.run("45,78,-93,12"); // result should be [45, 78, -93, 12]
	result.result.postcs;
    )
    (
	// both separators and values can be more complex parsers as well
	var commacolonsep = ParserFactory.makeSepBy(Choice([StrParser(","), StrParser(";")]));
	var csv_numbers = commacolonsep.(ParserFactory.makeFloatParser);
	var text = "45,-89.23;56.7,67;90.09";
	var result = csv_numbers.run(text); // result should be [ 45.0, -89.23, 56.7, 67.0, 90.09 ]
	result.result.postcs;
    )
	(
	var betweenSquareBrackets = ParserFactory.makeBetween(StrParser("["), StrParser("]"));
	var commaSeparated = ParserFactory.makeSepBy(StrParser(","));
	var digitparser = ParserFactory.makeIntegerParser;
	var nested_array_parser = ParserFactory.forwardRef(Thunk({
	// Thunk makes sure its inside is not evaluated yet
	// and forwardRef "disguises" (wraps) the Thunk as a Parser
	//
	// We need a forward ref here to get around a mutual dependency problem
	// namely: nested_array_element uses flat_arrayparser, but
	// flat_arrayparser uses nested_array_element... (= mutually recursive definition).
	Choice([
	    digitparser,
		arrayparser])
	}));
	var arrayparser = betweenSquareBrackets.(commaSeparated.(nested_array_parser));
	var result = arrayparser.run("[1,[2,3,[4,5],6],7,[8],9]"); // expected result: [ 1, [ 2, 3, [ 4, 5 ], 6 ], 7, [ 8 ], 9 ]
	result.result.postcs;
	)
	(
	var t = Int8Array[0xF7, 0x00, 0x00, 0x41, 0xF0];
	var pl = [ParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7])].addAll(BinaryBit() ! 8);
	var p2 = [ParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7])].add(ParserFactory.makeBinaryIntParser(8));
	var sysexParser = SequenceOf([
	ParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7])].add(
	ParserFactory.makeBinaryIntParser(8).chain({
	    | result |
	    // using chain we can influence the next parser:
	    // if the first byte is 0x00, two more bytes will follow with the manufacturer id
	    // otherwise, the byte value itself is already the manufacturer's id
	    if (result == 0) {
	        // extract extended ID and tag it as \manufacturerId in the parse result
	        ParserFactory.makeBinaryUIntParser(16).map(MapFactory.tag(\manufacturerId));
	    } {
	        // current ID is already extracted; tag it as \manufacturerId in the parse result
	        SucceedParser(result).map(MapFactory.tag(\manufacturerId));
	    }
	}))).map({|result| result[1]});
	var result;
	result = SequenceOf(sysexParser).run(t);
	result.result.postcs;
	)

	'''
	*/

}