# scparco

<img src="https://github.com/shimpe/scparco/blob/main/image/logo.png?raw=true" width="250" height="250"/>
GPLv3 Library of parser combinators for supercollider. It can be used with text (modeled as a string) or binary data (contained in an Int8Array).

Example of parsing binary data from a sysex message:

<pre><code>
(
var t = Int8Array[0xF7, 0x00, 0x01, 0x41, 0xF0]; // contrived example containing only manufacturer's id
var startOfSysex = ParserFactory.makeBinaryLiteralInt8ArrayParser(Int8Array[0xF7]);
var manufacturerId = ParserFactory.makeBinaryUIntParser(8).chain({
		| result |
		// using chain we can influence the next parser:
		// if the first byte is 0x00, two more bytes will follow with the manufacturer id
		// otherwise, the byte value itself is already the manufacturer's id
		if (result == 0) {
			// extract extended ID and tag it as \manufacturerId in the parse result
			ParserFactory.makeBinaryUIntParser(16).map(MapFactory.tag(\manufacturerId));
		} {
			// current byte value is already the ID; tag it as \manufacturerId in the parse result
			SucceedParser(result).map(MapFactory.tag(\manufacturerId));
		}
});
var sysexParser = SequenceOf([startOfSysex, manufacturerId]).map({|result| result[1] }); // keep only manufacturer's id
var result = sysexParser.run(t);
result.result.postcs;
)
</pre></code>

A non-trivial example for parsing and evaluating a mathematical expresssion from text:

<pre><code>
(
// simple calculator
// first the parser
var numberParser = ParserFactory.makeFloatParser.map({|x| (\type : \number, \value : x) });
var operatorParser = Choice([StrParser("+"), StrParser("-"), StrParser("*"), StrParser("/")]);
var betweenBrackets = ParserFactory.makeBetween(StrParser("("), StrParser(")"));
var expression = ParserFactory.forwardRef(Thunk({
	Choice([
		numberParser,
		operationParser
	])
}));
var operationParser = betweenBrackets.(SequenceOf([
	operatorParser,
	ParserFactory.makeWs,
	expression,
	ParserFactory.makeWs,
	expression
])).map({ |results| (\type: \operation, \value : (\op : results[0], \a : results[2], \b : results[4]) ) });

// then an evaluator to perform the actual calculations based on the parsing result
var evaluate = {
	| node |
	if (node[\type] == \number) {
		node[\value];
	} {
		if (node[\type] == \operation) {
			if (node[\value][\op].compare("+") == 0) {
				thisFunction.(node[\value][\a]) + thisFunction.(node[\value][\b]); //recursion
			} {
				if (node[\value][\op].compare("-") == 0) {
					thisFunction.(node[\value][\a]) - thisFunction.(node[\value][\b]);
				} {
					if (node[\value][\op].compare("*") == 0) {
						thisFunction.(node[\value][\a]) * thisFunction.(node[\value][\b]);
					} {
						if (node[\value][\op].compare("/") == 0) {
							thisFunction.(node[\value][\a]) / thisFunction.(node[\value][\b]);
						}
					}
				}
			}
		}
	};
};

var interpreter = {
	| program |
	// first parse the program, then evaluate it
	var parseResult = expression.run(program);
	if (parseResult.isError) {
		"Invalid program".postln;
		nil;
	} {
		evaluate.(parseResult.result);
	}
};

// try it out
var program = "(+ (* 10 2) (-       (/50 3) 2))";
var result = interpreter.(program);
result.debug("result of the calculation");
)
</code></pre>
