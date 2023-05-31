/*
[general]
title = "BinaryZero"
summary = "a specialized Parser that can match a single binary 0 from a binary stream (contained in an Int8Array)."
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
BinaryZero is a Parser that matches a single 0 bit in a binary stream (contained in an Int8Array). Building block for binary stream parsers.
'''
*/
BinaryZero : Parser {
	/*
	[classmethod.new]
	description = "new creates a new BinaryZero Parser"
	[classmethod.new.returns]
	what = "BinaryZero"
	*/
	*new {
		^super.new.init;
	}

	/*
	[method.init]
	description = "init initializes the BinaryZero parser with a suitable parserStateTransformer function that consumes a bit and advances the parser index"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "BinaryZero"
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
					nextState = parserStateIn.updateError("BinaryZero: unexpected end of input");
				} {
					var byte = parserStateIn.target[byteOffset];
					var bitoffset = parserStateIn.index.mod(8);
					var result = byte.asBinaryDigits[bitoffset];
					if (result != 0) {
						nextState = parserStateIn.updateError("BinaryZero: expected a 0 but got a 1 at index" + parserStateIn.index);
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
	var p1 = Many(BinaryZero());
	var r1 = p1.run(Int8Array[2r00011101]);
	r1.prettyprint; // expect [0, 0, 0]
	)
	'''
	*/
}