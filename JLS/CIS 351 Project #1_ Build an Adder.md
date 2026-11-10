# Tab 1

### Project \#1: Build an Adder

#### Overview:

For this project, you will:

* (1) Work through the JLS tutorials specified below (you will build a half adder as part of these)  
* (2) Use JLS to implement a 16-bit unsigned adder  
* (3) Use JLS to implement a 16-bit signed adder

#### Pre-requisites:

The JLS introduction homework problem (from Homework 1\) explains how to obtain, set up, and use JLS, as well as the DLUnit testing software.

Once you have completed the introduction, you might find [this video](https://www.youtube.com/watch?v=XzF2TBw7Fgw) from Dr. Kurmas helpful for working in JLS. The video demonstrates several helpful JLS shortcuts. Remember, JLS was designed specifically with these projects in mind. If you find a task highly repetitive/tedious, you may be overlooking a shortcut.

#### Task 1: Getting started in JLS (building a half adder circuit)

Begin by working through the "Introduction", "Full Adder", and "Sign Extender" tutorials. You will find all the tutorials in the Help-\>Tutorials menu within JLS (look in the top right for Help).

As you work through the tutorial, pay special attention to the following details:

* Name your half adder HalfAdder.jls, *not* ha as suggested by the tutorial.  
* Name your sum output Sum, *not* SUM as suggested by the tutorial.  
* Name the inputs to your half adder InputA and InputB, *not* A and B as suggested by the tutorial.  
* Name your full adder FullAdder.jls, *not* fa as suggested by the tutorial.  
* Importing a sub-circuit inserts a *copy* of the imported sub-circuit into the circuit you are creating. As a result, if you later discover a bug in your sub-circuit, you must re-import that sub-circuit everywhere. It is not sufficient to simply fix the bug in the sub-circuit's jls file. Moral: Thoroughly test your sub-circuits before importing them.

When you are finished, save your half-adder in a file named HalfAdder.jls. Make sure the inputs are named "InputA" and "InputB", and the outputs are named "Sum" and "Carry". DLUnit requires the circuits to present a known interface. (In other words, the program needs to know what the input and output pins are named.) If you do not conform to the specified interface (i.e., the given circuit and input names), your circuit will be graded as incorrect.

##### Testing your half adder

After completing a circuit, your first order of business should be to test it by setting the values of the input pins and examining the values of the output pins. In fact, it is a good idea to test sub-circuits as you build them.

When you have finished building the full adder (as described in the tutorial):

1. Add input pins named InputA, InputB, and CarryIn.  
2. Add a "generate test signals" element. (This element is on the toolbar to the right of the "pause" element.) Set appropriate values for the three input pins. As an example, one set of inputs may look like this:

| InputA 1 end InputB 1 end CarryIn 1 end  |
| :---- |

3. Add watches to all the output pins.  
4. Place probes on the "Carry" outputs of each component half adder.  
5. Open the simulator window, run the simulation, and verify that the resulting output is correct.  
6. Change the scale factor from 1 to 5 and observe what happens.

Using the simulator window is a good way to check a few example input/output pairs, or to track down bugs; however, trying to thoroughly test a circuit using this technique would be very tedious and time consuming. Hence, we use DLUnit to automate the running of many tests.

Practice using this testing setup by testing the half adder you built while completing the tutorial. (Again, make sure the file, inputs, and outputs are all named correctly.) See the previous homework for details on how to run DLUnit (or ask me). As a reminder, here are the general steps (assuming everything is in the same directory):

1. Download [HalfAdderTest.java](https://us.prairielearn.com/pl/course_instance/217555/instance_question/729864209/clientFilesQuestion/HalfAdderTest.java).  
2. Compile the test: javac \-cp DLUnit.jar HalfAdderTest.java  
3. Run java \-jar DLUnit.jar HalfAdder.jls HalfAdderTest.class

To make sure you understand how DLUnit works, deliberately introduce a bug into HalfAdder.jls (e.g., replace the XOR gate with an AND gate) and re-run the test script. (Then fix the circuit once you know what happens when there is an error.)

There is no deliverable for task 1; however, it will be useful for completing tasks 2 and 3\.

#### Task 2: Build an Unsigned Adder

Now that you have had some practice using JLS, it's time to build your own circuit. Build a circuit named UnsignedFullAdder\_16bit that adds two 16-bit unsigned integers.

I will only be grading the *functionality* of your unsigned adder; however, you will eventually have to build a signed adder that has a maximum propagation delay of 160 or less. I suggest simply getting a working unsigned adder for this part of your project; but, as you do that, be thinking about how you can make performance improvements for the next task.

To keep the design manageable, you may want to use one or more sub-circuits (e.g., connect four 4-bit adders, or two 8-bit adders). If you do use sub-circuits, notice that you can unbundle buses into arbitrary groups. For example, you can take a 16-bit bus and split it into 0-3, 4-7, 8-11, and 12-15. In the "Create unbundler" dialog, select "Group bits" instead of "Single bits". (See the "Sign Extender" tutorial built into JLS.)

* Name the inputs "InputA","InputB", and "CarryIn".  
* Name the outputs "Output" and "CarryOut". (If you do not name the inputs and outputs correctly, the test script will not run.) The "CarryOut" output should be 1 if, and only if, there is an overflow.  
* Test your circuit using [SampleUnsigned16BitAdderTest.java](https://us.prairielearn.com/pl/course_instance/217555/instance_question/729864209/clientFilesQuestion/SampleUnsigned16BitAdderTest.java). If the test script doesn't work without modification, then you either named your circuit, inputs, or outputs incorrectly.  
* The provided tests will not completely test your circuit. Add as many tests as necessary to be confident your circuit works

##### Rules:

1. The simulator comes with a built-in adder. You may *not* use the built-in adder.  
2. To build the required circuits, you will probably find it useful to design and build several subcircuits. These subcircuits may have any interface you choose; however, you are responsible for testing them.  
3. You must write all of your own chips. Do not share "internal" chips with your classmates or download them off the Internet.  
4. Be sure to test your components thoroughly. Just because it passes the included tests does not mean it will pass mine.

##### Task 2 submission instructions

You can submit this circuit as many times as you need to get it correct, but **you can submit at most once every two hours**. So, if you wait until the deadline to submit and do not score 100%, you may not be able to submit again until the next day, resulting in a score penalty. This is meant to strongly incentivize you to (a) start early and (b) test your adder yourself using DLUnit. Note that you also get a slight benefit for submitting the project early.

To submit your circuit:

1. Add a text box to your main jls file that includes your name and your partner's name.  
2. Once you have thoroughly tested your circuit, upload it below to run it against my tests.

# Tab 2

### Project \#1: Build an Adder

Prior to completing task 3, complete project tasks 1 and 2\.

#### Task 3: Build a signed adder with split mode

Implement a 16-bit *signed* adder using any algorithm you wish, provided the circuit has a maximum propagation delay of 170 or less.

Your adder must report overflow conditions when they occur (with respect to signed inputs). This circuit must have an input named CarryIn. You will use this input for future projects.

Your inputs *must* be named InputA, InputB, CarryIn, and Split. Your circuit will not work with my tests otherwise.

Your outputs *must* be named Output, CarryOut, and Overflow. CarryOut is the carry out from the last full adder. It is also, in effect, the overflow indicator for *unsigned* inputs. Overflow is the overflow indicator for *signed* inputs. CarryOut and Overflow will not necessarily have the same value.

##### Split mode

Real processors often need to add several small values at once (for example, the four 8-bit color channels of a pixel). Rather than build a separate adder for each width, they build one wide adder whose carry chain can be *broken* into segments. Your adder must do the same.

The Split input selects the mode:

* When Split is 0, your circuit adds InputA and InputB as ordinary 16-bit values, exactly as described above.  
* When Split is 1, your circuit must behave as *two independent 8-bit adders*. Bits 7-0 of InputA are added to bits 7-0 of InputB, and bits 15-8 of InputA are added to bits 15-8 of InputB. (The basic approach to splitting the adder is to simply prevent the carry from crossing the boundary between bit 7 and bit 8\)

For example, when Split is 1, 0x05FF \+ 0x0301 is 0x0800 \--- *not* 0x0900. The low segment wraps around from 0xFF to 0x00, and the carry it produces is discarded rather than added into bit 8\.

When Split is 1:

* CarryIn is the carry in to the *low* segment only. The high segment always adds with a carry in of 0.  
* CarryOut is the carry out of the *high* segment.  
* Overflow is ignored. My tests will not examine it when Split is 1.

You must implement split mode by controlling the carry *inside* the adder you already built. Do not build a second adder, and do not try to correct the output after the fact. Think carefully about where the carry into bit 8 actually comes from in your design \--- the answer depends on which addition algorithm you chose, and for some designs it is not a single wire.

You may use [SampleSigned16BitAdderTest](https://us.prairielearn.com/pl/course_instance/217555/instance_question/729864208/clientFilesQuestion/SampleSigned16BitAdderTest.java) and [SampleSplitSigned16BitAdderTest](https://us.prairielearn.com/pl/course_instance/217555/instance_question/729864208/clientFilesQuestion/SampleSplitSigned16BitAdderTest.java) as a basis for your own tests. A word of warning: these tests scripts are incomplete. Don't assume that a circuit passing these sample tests will pass all of my tests. Add test cases to ensure correct functionality.

##### Rules:

1. The simulator comes with a built-in adder. You may *not* use the built-in adder. Your circuit may contain only *one* adder; split mode must reuse it rather than duplicate it.  
2. To build the required circuits, you will probably find it useful to design and build several subcircuits. These subcircuits may have any interface you choose; however, you are responsible for testing them.  
3. You must write all of your own chips. Do not share "internal" chips with your classmates or download them off the Internet.  
4. Be sure to test your components thoroughly. Just because it passes the included tests does not mean it will pass mine.

##### Circuit submission instructions

You can submit this circuit as many times as you need to get it correct, but **you can submit at most once every two hours**. So, if you wait until the deadline to submit and do not score 100%, you may not be able to submit again until the next day, resulting in a score penalty. This is meant to strongly incentivize you to (a) start early and (b) test your adder yourself using DLUnit.

To submit your circuit:

1. Add a text box to your main jls file that includes your name and your partner's name.  
2. Once you have thoroughly tested your circuit, upload it below to run it against my tests.

### Analysis

When your adder passes all of the automated tests, prepare a document that discusses its overall design and analyzes its

* exact size  
* exact maximum propagation delay  
* big-O running time  
* how you implemented split mode: where the carry into bit 8 comes from in your design, how you prevented it from crossing the boundary when Split is 1, and what that cost you in gates and in propagation delay

To compute the exact size, simply count the total number of gates used. (You don't need to consider the number of inputs.)

To compute the exact time, compute the propagation delay using the default component delays given by JLS (10 for AND, OR, XOR, 5 for NOT, NAND, NOR, etc.). To see a component's default delay, right-click on it and choose "change timing" from the context menu. (Look at the time; but, don't actually chagne it\!)

Your adder's big-O running time describes how the running time will change if you applied your pattern to adders of increasing size. If you used a hybrid design, describe how you would extend your design to build 32-bit and 64-bit adders. (Your technique for extending your hybrid design will determine the big-O running time.)

Finally, add a paragraph or two discussing your adder's overall design. In particular, if you used a hybrid design, list any sub-linear addition algorithms used and how you incorporated them into your adder.

Place this information in a file named either analysis.txt or analysis.pdf. For each answer, please include a paragraph or two describing how you got your answer.

Make sure your name(s) appear in the file.

#### Project Grading

***You are expected to revise your circuits and analysis documents until both (1) the automated tests pass, and (2) the analysis document has earned a score of "E" or "M".***

To receive an "M" or better for this project:

* The unsigned adder must pass all instructor tests  
* The signed adder must pass all instructor tests (including overflow-related tests)  
* The maximum propagation delay of the signed adder must be at most 170\.  
* The analysis must be correct.

# Tab 3

### Circuits and Unit Testing

---

Credit: Much of this assignment is based on similar assignments by Professor Kurmas.

### Intro

You will use simulation software when designing and building circuits in this course. The circuit simulator was developed by Dave Poplawski at [Michigan Tech](http://www.cs.mtu.edu/) (now retired) and named *JLS* (*J*ava *L*ogic *S*imulator).

Dr. Kurmas has a YouTube video explaining some of the basics of using JLS to build circuits: [https://www.youtube.com/watch?v=l5SDkUcI46k](https://www.youtube.com/watch?v=l5SDkUcI46k).

We won't actually be *building* circuits in JLS yet because we have not discussed how circuits work. However, you can still write tests for circuits using the same skills you would use to write unit tests for any software. These tests will come in handy in the next few weeks when you start building circuits of your own.

### Setting up JLS

All JLS code is contained in JLS.jar. There are two ways of getting JLS set up:

* Local installation  
* Use remote installation on EOS/Arch

I **strongly** recommend that you get JLS working on your own computer if at all possible. This will make your life much easier on future assignments/projects when you'll need to build your own circuits using JLS. Come talk to me if you run into issues getting things working locally\!

**Option 1 \-- Local installation**

Running JLS (and DLUnit later) on your own machine will take a little bit of work because you'll need a working Java Development Kit (JDK) and you'll need to download some JAR files. The benefit is that you can develop on your own machine. This can be helpful because the connection to the Guacamole website (the remote access to EOS) is sometimes spotty. I am happy to help with this option if you are having difficulty setting up your local Java environment.

To run JLS on your own machine, download [JLS.jar](https://us.prairielearn.com/pl/course_instance/217555/instance_question/721969996/clientFilesCourse/jls_jars/JLS.jar). You run JLS by typing java \-jar jlsDir/JLS.jar, where jlsDir is the directory into which you saved JLS.jar.

Note: It is best to run JLS from the command line.

1. Double-clicking the .jar file icon often sets your current working directory to something inconvenient.  
2. On a Mac, double-clicking the .jar file tends to raise security warnings. This doesn't happen when launching from the terminal

**Option 2 \-- Use the installation on EOS/Arch**

It is simple to get JLS up and running if you are using EOS. You can log in through [computing.gvsu.edu](https://computing.gvsu.edu/) and select EOS RDP to get a remote EOS desktop.

*Note:* If you are going to work via EOS RDP at any point in the semester, it is strongly recommended that you open Prairielearn through the RDP session itself (once logged in, go to Activities \-\> Firefox \-\> www.prairielearn.org). This is a bit annoying the first time because you need to log in again, but it will save you a lot of headaches in the long run because

* you can copy/paste commands from PL  
* any files you download from PL will be on the machine you are using \-- no need for scp or ftp

The jar file you need is stored in /home/lalejina/public/bin. The easiest way to run JLS is to put this directory in your path. (For most of you, the best way to do this is to add the line export PATH=$PATH:/home/lalejina/public/bin to the end of your .bashrc, then open a new terminal so the changes take effect.) If you want to add that directory to your path and are not sure how, you can use the following command:

| echo 'export PATH=$PATH:/home/lalejina/public/bin' \>\> \~/.bashrc  |
| :---- |

There is a script named jls in this directory that sets up the java classpath and launches JLS. This directory also contains other scripts needed for future assignments.

Once you have added the line to your .bashrc, open a new terminal so the changes take effect. From this point forward (even after logging out and back in) you will be able to run JLS simply by typing jls in a terminal.

### Check your installation

Once you have set up JLS using one of the options above, download [comparatorTwoBit.jls](https://us.prairielearn.com/pl/course_instance/217555/instance_question/721969996/clientFilesQuestion/comparatorTwoBit.jls) and ensure that you can open it in JLS and view it.

### Testing JLS circuits with DLUnit

To test your circuits, you will use a tool called DLUnit. (Think of it as JUnit for hardware.) DLUnit allows you to write and execute JUnit tests for circuits. The way you access DLUnit will depend on how you set up JLS.

**Option 1**

If you are using your own machine, follow the steps below:

* Download [DLUnit.jar](https://us.prairielearn.com/pl/course_instance/217555/instance_question/721969996/clientFilesCourse/jls_jars/DLUnit.jar)  
* Download [ComparatorTwoBitTestWeak.java](https://us.prairielearn.com/pl/course_instance/217555/instance_question/721969996/clientFilesQuestion/ComparatorTwoBitTestWeak.java)  
* Compile the test: javac \-cp DLUnit.jar ComparatorTwoBitTestWeak.java  
* Run DLUnit with this command: java \-jar DLUnit.jar comparatorTwoBit.jls ComparatorTwoBitTestWeak.class

**Option 2**

If you're using Arch, follow the steps below:

* Ensure /home/lalejina/public/bin is in your PATH. If you followed the instructions above, you are all set on this part.  
* Download [ComparatorTwoBitTestWeak.java](https://us.prairielearn.com/pl/course_instance/217555/instance_question/721969996/clientFilesQuestion/ComparatorTwoBitTestWeak.java)  
* Compile the test: dlunit\_compile ComparatorTwoBitTestWeak.java  
* Run DLUnit with this command: dlunit comparatorTwoBit.jls ComparatorTwoBitTestWeak.class

**All options**

The circuit that you downloaded is a **comparator** \-- it takes two numbers, A and B, and checks whether A \< B. We will see later that there are better ways to build comparators. You can study the provided comparator circuit if you want, but you are not expected to have any idea what it is doing yet. The provided tests should not find any errors in the comparator.

### Writing your own tests

Next, you are going to detect errors in a broken circuit. Download [comparatorTwoBitBroken.jls](https://us.prairielearn.com/pl/course_instance/217555/instance_question/721969996/clientFilesQuestion/comparatorTwoBitBroken.jls). Run the same tests on this circuit using the process above (modifying filenames where appropriate). Because the tests are not thorough, they will not report any errors even though one exists.

Take a look at the tests in ComparatorTwoBitTestWeak.java. Familiarize yourself with the format of the tests. The provided tests are not very thorough, but they can be used as a template to build your own, better tests.

A two-bit comparator accepts as inputs two numbers between 0 and 3, inclusive, and determines whether the first is strictly less than the second. Because there are relatively few input combinations, DLUnit can test them all in a relatively short amount of time.

* Write a DLUnit test suite that checks every possible input combination exactly once. Put the tests in a file named ComparatorTwoBitTest.java  
* Run the test suite you just wrote on the broken comparator. If done correctly, you should see an error.

Congratulations \-- you can now test circuits\! At this point, you would normally figure out what is wrong with the circuit and fix it. But, for this assignment, we will be content to have caught the errors.

One word of warning: generally, the circuits you write in this course will be too large to test exhaustively as you did here. You will need to think more carefully about the types of numbers that you test. Don't worry \-- you will get plenty of practice writing unit tests in this course.

### Submitting

Upload the tests you wrote below. PrairieLearn will "test your tests" using some unit-testing software of its own. If you followed directions correctly up to this point, you should get full credit for the question. If not, you can adjust your tests and try again.

