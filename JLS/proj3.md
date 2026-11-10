Project #3: Build an ALU using JLS
Important: Your ALU may have only one adder. You will need to re-design your slt circuit slightly so that it can use the result of your adder-subtractor.

Overview:
Combine your adder, adder/subtractor, and comparator from previous projects into a single ALU (Arithmetic Logic Unit). Notice that you will not be implementing the ALU described in the textbook. Your ALU will have different operations and opcodes.

Your ALU must perform the following 16-bit operations:

Operation	Op
Unsigned Addition	0
Unsigned Subtraction	1
And	2
Or	3
Not	4
Xor	5
lui	6
sltu	7
Signed Addition	8
Signed Subtraction	9
slt	15
Pre-requisites:
You will use your circuits from previous projects. See me if you were unable to complete any of the previous projects.

Requirements:
Name your inputs InputA, InputB, and Op.

Name your outputs Output and Overflow.

Set the Overflow output to 0 if overflow doesn't apply to the given operation. Specifically, it should be 0 for all operations except signed addition and signed subtraction (but not signed slt). In the case of an ALU, Overflow does not apply to unsigned addition and unsigned subtraction because their error cases are handled with the "CarryOut" flag, which you do not need to implement in your ALU.

The not operator returns the negation of InputA. Ignore InputB.

lui stands for load upper immediate. Take the lower 8 bits of InputA and place them in the upper 8 bits of the output. Ignore InputB. (Look up the definition of lui in the MIPS Instruction Reference for explanation of the command's output, but instead of a 16-bit immediate and a 32-bit register, treat this as 8-bit immediate and 16-bit output).

slt stands for set if less than. The output should be 1 if InputA is less than InputB and 0 otherwise. slt treats the inputs as signed integers, sltu treats the numbers as unsigned. Note: In Project 2, slt and sltu returned 1 bit, but, in this project, the LSB should be 1 and all other bits 0.

Your entire ALU may contain only one adder. Your Adder/Subtractor from the previous project should already follow this design. For this project, design your ALU so that your comparator uses your Adder/Subtractor.

Rules:
To build the required circuits, you will probably find it useful to design and build several subcircuits. These subcircuits may have any interface you choose; however, you are responsible for testing them.
You must write all of your own chips. Do not share "internal" chips with your classmates or download them off the Internet.
Be sure to test your components thoroughly. Just because it passes the included tests does not mean it will pass the autograder.
You may use the built-in mux component.
Hints and Shortcuts:
You may findALU_16bit_starter.jls useful. It has the input and output pins set and also contains an example of using the "group bits" feature of the unbundler. However, you are not required to unbundle the bits in the way specified by the starter file if you do not find it helpful.
You may find these sample tests helpful: Unit tests
JLS inserts the code for sub-circuits directly into the circuits when you command it to. Changes to sub-circuit files are not seen by the circuits that contain them. If you discover a bug in a sub-circuit, you must delete and re-import the sub-circuit into the parent. (Test your circuits thoroughly before importing them!)
Grading:
You are expected to revise your circuits until both (1) the automated tests pass, and (2) the design and presentation have earned a score of "E" or "M".

Submission constraints

You can submit this project as many times as you need to get it correct, but you can submit at most once every two hours. So, if you wait until the deadline to submit and do not score 100%, you may not be able to submit again until the next day, resulting in a score penalty. This is meant to strongly incentivize you to (a) start early and (b) test your adder yourself using DLUnit. Note that you also get a slight benefit for submitting the project early.

Deliverables:
To submit your project:

Add a text box to your main jls file that includes your name and your partner's name.
Once you have thoroughly tested your circuit, upload it below to run it against my tests. (Note that a test will not report all failures. It typically reports only the first. Do not assume because you update your circuit for a particular case that it is completely correct. Again, test thoroughly!)
** Please be sure your name(s) appear in all submitted files **

** Remember: Your ALU may have only one adder. You will need to re-design your slt circuit slightly so that it can use the result of your adder-subtractor. **