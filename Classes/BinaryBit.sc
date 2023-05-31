/*
[general]
title = "BinaryBit"
summary = "a specialized Parser that can match a single bit from a binary stream (contained in an Int8Array)."
categories = "Parsing Tools"
related = "Classes/Parser"
description = '''
BinaryBit is a Parser that a single Bit in a binary stream (contained in an Int8Array). Building block for binary stream parsers.
These can be useful e.g to interpret headers of sound files, or to parse Sysex messages. Note that the ParserState.index here represents a bit offset and not a character offset as was the case in text parsers.
'''
*/
BinaryBit : Parser {
	/*
	[classmethod.new]
	description = "new creates a new BinaryBit Parser"
	[classmethod.new.returns]
	what = "BinaryBit"
	*/
	*new {
		^super.new.init;
	}

	/*
	[method.init]
	description = "init initializes the BinaryBit parser with a suitable parserStateTransformer function that consumes a bit and advances the parser index"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "BinaryBit"
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
					nextState = parserStateIn.updateError("BinaryBit: unexpected end of input");
				} {
					var byte = parserStateIn.target[byteOffset];
					var bitoffset = parserStateIn.index.mod(8);
					var result = byte.asBinaryDigits[bitoffset];
					nextState = parserStateIn.updateState(parserStateIn.index + 1, result);
				};
				nextState;
			};
		};
	}

	/*
	[examples]
	what = '''
	(
	var p1 = Many(BinaryBit());
	var r1 = p1.run(Int8Array[2r11101011]);
	r1.prettyprint; // expect [1, 1, 1, 0, 1, 0, 1, 1]
	)
	'''
	*/
}