/*
[general]
title = "ScpBinaryZero"
summary = "a specialized ScpParser that can match a single binary 0 from a binary stream (contained in an Int8Array)."
categories = "Parsing Tools"
related = "Classes/ScpParser"
description = '''
ScpBinaryZero is a ScpParser that matches a single 0 bit in a binary stream (contained in an Int8Array). Building block for binary stream parsers.
'''
*/
ScpBinaryZero : ScpParser {
	/*
	[classmethod.new]
	description = "new creates a new ScpBinaryZero ScpParser"
	[classmethod.new.returns]
	what = "ScpBinaryZero"
	*/
	*new {
		^super.new.init;
	}

	/*
	[method.init]
	description = "init initializes the ScpBinaryZero parser with a suitable parserStateTransformer function that consumes a bit and advances the parser index"
	[method.init.args]
	parsers = "a list of parsers"
	[method.init.returns]
	what = "ScpBinaryZero"
	*/
	init {
		this.parserStateTransformer = {
			| parserStateIn |
			if (parserStateIn.isError) {
				parserStateIn;
			} {
				var byteOffset = (parserStateIn.index.div(8)).asInteger;
				var nextState = parserStateIn;
				this.logStartTrace(parserStateIn, "ScpBinaryZero");
				if (byteOffset >= (parserStateIn.target.size)) {
					this.logEndTrace(parserStateIn, "ScpBinaryZero", false);
					nextState = parserStateIn.updateError("ScpBinaryZero: unexpected end of input");
				} {
					var byte = parserStateIn.target[byteOffset];
					var bitoffset = parserStateIn.index.mod(8);
					var result = byte.asBinaryDigits[bitoffset];
					if (result != 0) {
						this.logEndTrace(parserStateIn, "ScpBinaryZero", false);
						nextState = parserStateIn.updateError("ScpBinaryZero: expected a 0 but got a 1 at index" + parserStateIn.index);
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
	var p1 = ScpMany(ScpBinaryZero());
	var r1 = p1.run(Int8Array[2r00011101]);
	r1.prettyprint; // expect [0, 0, 0]
	)
	'''
	*/
}