Project 2a: Implement Subtraction
Overview:
Extend your signed adder from the previous project to build:

a circuit that can both add and subtract, and
a circuit that can compare signed and unsigned integers
Pre-requisites:
You will use your signed adder from the previous project.

Task 1: Adder/Subtractor
Build a circuit that can perform addition and subtraction. Specifically, you are surrounding your adder with additional gates so that it performs both addition and subtraction. Your circuit should contain only one adder. You may include one copy of your signed adder, and no other adders of any type or size. Ask for clarification or stop by during office hours if you don't understand this requirement.

Your Inputs must be named InputA, InputB, and Op. If they are misnamed, your circuit won't work with my tests. The Op input specifies whether to perform addition or subtraction (1 means subtract, 0 means add).

Your outputs must be named Output, Overflow, and UnsignedOverflow.

When interpreting the inputs as signed values, Overflow is 1 when the result requires more than 16 two's complement bits.
When interpreting the inputs as unsigned values, UnsignedOverflow is 1 when either (a) the result requires more than 16 bits or (b) the result is negative.
Be Careful:

UnsignedOverflow is not simply the most significant bit of Output
UnsignedOverflow is not the same thing as your adder's carry out, so don't just run that pin straight to the output.
To figure out the logic for UnsignedOverflow:

Find two examples that demonstrate why UnsignedOverflow is not simply the most significant bit of Output — one where the most significant bit of Output claims an overflow that didn't happen, and one where it misses an overflow that did.
Look at many different examples and determine when UnsignedOverflow is equal to carry out and when it is not.
Once you have discovered the pattern in #2 above, make an argument for why that pattern is true for all inputs. Hint: Notice that ~B = 65535 - B. What does this tell you about what your inner adder is actually calculating when you are subtracting? When will this "actual" calculation have a carry out? When will it not have a carry out? Why? (The addition half of your pattern should need much less work — why?)

You will not be using the split feature of your adder for this project.

Subtractor_16bit.jls has the inputs and outputs set up for you.
You may use the test file SampleSigned16BitAddSubtractTest.java as a basis for your own tests. A word of warning -- this test script is incomplete. Don't assume that a circuit passing these sample tests will pass all of my tests. Add test cases as necessary to ensure correct functionality.

Task 2: Set if Less Than
Build a circuit that can compare two integers. It must support both signed and unsigned comparisons.

For InputA and InputB, the circuit outputs True if InputA < InputB. Otherwise, the circuit outputs False. Remember: in binary, whether InputA is less than InputB can depend on whether they are signed or unsigned numbers.

Use your adder/subtracter as a starting point and leverage the fact that:

if A < B, then A - B < 0, and
if A ≥ B, then A - B ≥ 0
However, this algorithm only works as long as A - B doesn't overflow. You'll need to think about the conditions in which A - B might overflow and alternate techniques for comparing A and B in this case.

Your inputs must be named InputA, InputB, and Signed. If not, your circuit will fail my tests. The Signed input specifies whether to perform a signed or unsigned comparison (1 means signed, 0 means unsigned).

Your output must be named LessThan.

Slt_16bit.jls has the inputs and outputs set up for you.
You may use this file SampleSlt16BitTest.java as a basis for your own tests. As always, this test script is incomplete. Don't assume that passing all tests here will pass my tests.

Rules:
The simulator comes with a built-in adder. You may not use the built-in adder.
To build the required circuits, you will probably find it useful to design and build several subcircuits. These subcircuits may have any interface you choose; however, you are responsible for testing them.
You must write all of your own chips. Do not share "internal" chips with your classmates or download them off the Internet.
Be sure to test your components thoroughly. Just because it passes the included tests does not mean it will pass mine.
You may use the built-in mux component.
Hints and Shortcuts:
JLS inserts the code for sub-circuits directly into the circuits when you command it to. Changes to sub-circuit files are not seen by the circuits that contain them. If you discover a bug in a sub-circuit, you must delete and re-import the sub-circuit into the parent. (Test your circuits thoroughly before importing them!)
No software is perfect. This includes JLS and DLUnit. Save often in case problems arise.
Remember to test your slt circuit thoroughly. The sample test file will not catch several common mistakes.
Submission constraints:

As with the first project, you can submit this project as many times as you need to get it correct, but you can submit at most once every two hours. So, if you wait until the deadline to submit and do not score 100%, you may not be able to submit again until the next day, resulting in a score penalty. This is meant to strongly incentivize you to (a) start early and (b) test your adder yourself using DLUnit. Note that you also get a slight benefit for submitting the project early.

Deliverables:
This question will allow you to submit your subtractor. To submit your comparator (SLT), go to the next question.

To submit your project:

Add a text box to your main jls file that includes your name and your partner's name.
Once you have thoroughly tested your circuit, upload it below to run it against my tests.
I suggest ensuring you have a working subtractor before uploading and testing your comparator.
** Please be sure your name(s) appear in all submitted files **

** Remember: Your subtractor may have only one adder circuit**