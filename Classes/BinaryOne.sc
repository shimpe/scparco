/*
[general]
title = "BinaryOne"
summary = "a specialized Parser that can match a single binary 1 from a binary stream (contained in an Int8Array)."
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
BinaryOne is a Parser that matches a single 1 bit in a binary stream (contained in an Int8Array). Building block for binary stream parsers.
'''
*/
BinaryOne : Parser {
	/*
	[classmethod.new]
	description = "new creates a new BinaryOne Parser"
	[classmethod.new.returns]
	what = "BinaryOne"
	*/
	*new {
		^super.new.init;
	}

	/*
	[method.init]
	description = "init initializes the BinaryOne parser with a suitable parserStateTransformer function that consumes a bit and advances the parser index"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "BinaryOne"
	*/
	init {
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError) {
				parserStateIn;
			} {
				var byteOffset = (parserStateIn.index.div(8)).asInteger;
				var nextState = parserStateIn;
				if (byteOffset >= (parserStateIn.target.size)) {
					nextState = parserStateIn.updateError("BinaryOne: unexpected end of input");
				} {
					var byte = parserStateIn.target[byteOffset];
					var bitoffset = parserStateIn.index.mod(8);
					var result = byte.asBinaryDigits[bitoffset];
					if (result != 1) {
						nextState = parserStateIn.updateError("BinaryOne: expected a 1 but got a 0 at index" + parserStateIn.index);
					}{
						nextState = parserStateIn.updateState(parserStateIn.index + 1, result);
					}
				};
				nextState;
			};
		};
	}

	/*
	[examples]
	what = '''
	(
	var p1 = Many(BinaryOne());
	var r1 = p1.run(Int8Array[2r11101010]);
	r1.prettyprint; // expect [1, 1, 1]
	)
	'''
	*/
}