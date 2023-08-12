/*
[general]
title = "ScpBinaryOne"
summary = "a specialized ScpParser that can match a single binary 1 from a binary stream (contained in an Int8Array)."
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpBinaryOne is a ScpParser that matches a single 1 bit in a binary stream (contained in an Int8Array). Building block for binary stream parsers.
'''
*/
ScpBinaryOne : ScpParser {
	/*
	[classmethod.new]
	description = "new creates a new ScpBinaryOne ScpParser"
	[classmethod.new.returns]
	what = "ScpBinaryOne"
	*/
	*new {
		^super.new.init;
	}

	/*
	[method.init]
	description = "init initializes the ScpBinaryOne parser with a suitable parserStateTransformer function that consumes a bit and advances the parser index"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "ScpBinaryOne"
	*/
	init {
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError) {
				parserStateIn;
			} {
				var byteOffset = (parserStateIn.index.div(8)).asInteger;
				var nextState = parserStateIn;
				this.logStartTrace(parserStateIn, "ScpBinaryOne");
				if (byteOffset >= (parserStateIn.target.size)) {
					this.logEndTrace(parserStateIn, "ScpBinaryZero", false);
					nextState = parserStateIn.updateError("ScpBinaryOne: unexpected end of input");
				} {
					var byte = parserStateIn.target[byteOffset];
					var bitoffset = parserStateIn.index.mod(8);
					var result = byte.asBinaryDigits[bitoffset];
					if (result != 1) {
						this.logEndTrace(parserStateIn, "ScpBinaryZero", false);
						nextState = parserStateIn.updateError("ScpBinaryOne: expected a 1 but got a 0 at index" + parserStateIn.index);
					}{
						this.logEndTrace(parserStateIn, "ScpBinaryZero", true);
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
	var p1 = ScpMany(ScpBinaryOne());
	var r1 = p1.run(Int8Array[2r11101010]);
	r1.prettyprint; // expect [1, 1, 1]
	)
	'''
	*/
}