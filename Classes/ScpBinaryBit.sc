/*
[general]
title = "ScpBinaryBit"
summary = "a specialized ScpParser that can match a single bit from a binary stream (contained in an Int8Array)."
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpBinaryBit is a ScpParser that a single Bit in a binary stream (contained in an Int8Array). Building block for binary stream parsers.
These can be useful e.g to interpret headers of sound files, or to parse Sysex messages. Note that the ScpParserState.index here represents a bit offset and not a character offset as was the case in text parsers.
'''
*/
ScpBinaryBit : ScpParser {
	/*
	[classmethod.new]
	description = "new creates a new ScpBinaryBit ScpParser"
	[classmethod.new.returns]
	what = "ScpBinaryBit"
	*/
	*new {
		^super.new.init;
	}

	/*
	[method.init]
	description = "init initializes the ScpBinaryBit parser with a suitable parserStateTransformer function that consumes a bit and advances the parser index"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "ScpBinaryBit"
	*/
	init {
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError) {
				parserStateIn;
			} {
				var byteOffset = (parserStateIn.index.div(8)).asInteger;
				var nextState = parserStateIn;
				this.logStartTrace(parserStateIn, "ScpBinaryBit");
				if (byteOffset >= (parserStateIn.target.size)) {
					this.logEndTrace(parserStateIn, "ScpBinaryBit", false);
					nextState = parserStateIn.updateError("ScpBinaryBit: unexpected end of input");
				} {
					var byte = parserStateIn.target[byteOffset];
					var bitoffset = parserStateIn.index.mod(8);
					var result = byte.asBinaryDigits[bitoffset];
					this.logEndTrace(parserStateIn, "ScpBinaryBit", true);
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
	var p1 = ScpMany(ScpBinaryBit());
	var r1 = p1.run(Int8Array[2r11101011]);
	r1.prettyprint; // expect [1, 1, 1, 0, 1, 0, 1, 1]
	)
	'''
	*/
}