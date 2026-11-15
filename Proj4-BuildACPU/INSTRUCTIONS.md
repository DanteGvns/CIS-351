# Project #4: Build a Single Cycle CPU

## Overview

For this project, you will use JLS to build the Single Cycle CPU described in Chapter 7 of the Harris and Harris textbook (Chapter 4 in the Patterson and Hennessy textbook). You will not need to implement the control logic for the CPU -- you may use the control unit provided in the starter code.

Be sure to read this entire document (especially the hints) before you begin. Overlooking any of the instructions or hints can lead you down a dead-end path that will waste hours of your valuable time.

## Pre-requisites

You will need to understand:

- How to turn assembly language statements into machine language statements (you practiced this on the homework).
- How the single-cycle CPU shown in Figure 7.14 works.

## Starter Files

- [PHSingleCycleCPU.jls](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/generatedFilesQuestion/variant/185412721/PHSingleCycleCPU.jls) -- skeleton CPU in JLS
- [`basicOpsDemoTest.a`](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/clientFilesQuestion/basicOpsDemoTest.a) -- example test code
- [`andiOnly.a`](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/clientFilesQuestion/andiOnly.a) -- example test code
- [`basicLoadStoreTest.a`](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/clientFilesQuestion/basicLoadStoreTest.a) -- example test code
- [`Multiply.a`](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/clientFilesQuestion/Multiply.a) -- example test code
- [`marsAssembler`](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/clientFilesQuestion/marsAssembler) -- helper file for converting MIPS assembly to machine code; useful for debugging in JLS
- [`instructions`](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/clientFilesQuestion/instructions) -- example output of `marsAssembler`

## Implementation

Build the Single-Cycle CPU shown in Figure 7.14. To help you get started, the file `PHSingleCycleCPU.jls` includes a:

- Register file
- ALU
- Instruction memory
- Data memory
- Control unit

You aren't required to use the provided components; but building your own can be tedious.

### Interface

At the end of the project, your CPU should support the following instructions:

| Name | Mnemonic | Format | OpCode (in hex) | Func Code (in hex) |
| --- | --- | --- | --- | --- |
| Add | `add` | R | 0 | 20 |
| Add Immediate | `addi` | I | 8 | |
| And | `and` | R | 0 | 24 |
| And Immediate | `andi` | I | c | |
| Branch if Equal | `beq` | I | 4 | |
| Halt | `halt` | | 20 | |
| Jump | `j` | J | 2 | |
| Load Word | `lw` | I | 23 | |
| Load Upper Immediate | `lui` | I | F | |
| Nor | `nor` | R | 0 | 27 |
| Or | `or` | R | 0 | 25 |
| Or Immediate | `ori` | I | d | |
| Set Less Than | `slt` | R | 0 | 2a |
| Set Less Than Immediate | `slti` | I | a | |
| Store Word | `sw` | I | 2b | |
| Subtract | `sub` | R | 0 | 22 |

### Testing

In order for your CPU to work seamlessly with the test suite, it must use the following interface:

- Name the circuit for the entire CPU `PHSingleCycleCPU.jls`.
- Name the instruction memory `InstMemory`.
- Name the register file `RegisterFile`.
- Main Memory is simply a built-in RAM. Name it `MainMemory` and configure it with 2^20 words.

`DLUnit` has a built-in test class that can test this CPU. In addition to the `.jls` file, you also provide an assembly file as input. `DLUnit` will:

1. Simulate the assembly code using `MARS`.
2. Simulate the assembly code using your CPU.
3. Compare the final state of the registers from each simulation.

The test will pass if your CPU ends in the same state as the CPU simulated by `MARS`.

**IMPORTANT:** `DLUnit` only checks the *final* state of the CPU. If you write to a register more than once, the intermediate values of that register won't get checked.

The syntax for running `DLUnit` to test a CPU is:

```text
java -jar DLUnit.jar jls_file.jls builtin.SingleCycleCPUTest --param assembly_file.a
```

To test your code, you will need to write several example assembly programs. The starter code includes a few example testing programs; however, these programs alone won't test your CPU well.

Avoid the following cases when writing your tests:

- The ALU has a very simple implementation of `slt`/`slti`. It will give the wrong answer if the inputs overflow when subtracting.
- `andi` and `ori` will give incorrect results if the immediate value is negative (or has bit 15 set to 1).

### Debugging

Your debugging will generally be a two-step process:

1. Find assembly code for which the CPU is failing (use DLUnit for this).
2. Trace the execution of the code directly in JLS until you spot the bug.

The best way to debug a circuit that is failing tests is to use JLS to step through each instruction. The starter code includes a bash script named `marsAssembler` that takes an assembly file as input and generates machine code. To debug your circuit, run `marsAssembler` and redirect its output to a file named `instructions`. I.e., in a terminal and in the directory containing `marsAssembler`, run:

```bash
./marsAssembler test_being_failed.asm > instructions
```

Your circuit's Instruction Memory element is configured to load data from this file. So, if you launch JLS **in a directory containing `instructions`** you will be able to step through your CPU instruction by instruction.

**Important:** `marsAssembler` is a *bash* script. It won't run on the Windows command line. You will need to use Cygwin, WSL, or something similar to run it. Another option is to look inside the bash script and just run the Windows version of that command "by hand".

#### Sign-extension subcircuit

[SignExtend16To32.jls](SignExtend16To32.jls) can be imported into JLS as a subcircuit. Connect the 16-bit immediate field to `Input16`; its 32-bit `Output32` output preserves bits 15-0 and repeats bit 15 across bits 31-16. It is combinational and needs no clock.

#### Windows PowerShell helper

This repository also includes [marsAssembler.ps1](marsAssembler.ps1). It uses Java on your PATH and `DLUnit.jar` in the repository's `JLS` directory, located relative to the script.

From the `Proj4-BuildACPU` directory, run:

```powershell
.\marsAssembler.ps1 .\basicOpsDemoTest.a
```

To save the machine code for JLS, use `-o` (short for `-OutputFile`). The helper writes ASCII automatically:

```powershell
.\marsAssembler.ps1 basicOpsDemoTest.a -o instructions
```

This replaces the existing `instructions` file. Without `-o`, the helper still prints the machine code to the terminal. Paths containing spaces must be quoted.

If PowerShell blocks script execution, invoke it with a process-only execution policy override:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\marsAssembler.ps1 .\basicOpsDemoTest.a
```

**Note:** `marsAssembler` only works with `DLUnit` version 1.1.1 or newer (built 3 November 2020). You may need to [download the most recent version](https://us.prairielearn.com/pl/course_instance/217555/instance_question/778114628/clientFilesCourse/jls_jars/DLUnit.jar). Also, make sure `marsAssembler` and `DLUnit.jar` are in the same directory.

Once you have the JLS circuit set up to run the relevant instructions, you can run it and view circuit state using the `Simulator Window`. Be sure to add watches, probes, and so on as necessary to track any values of interest in the CPU. You may also need to change the `Scale Factor` to make the results easier to understand.

### Hints

- The built-in Register File uses *negative*-edge triggered flip-flops. Your registers (e.g., the program counter) should also use negative-edge triggered flip-flops. When using negative-edge triggered flip-flops, the clock's falling edge defines the beginning of each CPU cycle.
- The `WE`, `OE`, and `CS` inputs to RAM and ROM are *active low*.
- Even though the RAM and ROM control inputs are active low, none of the control wires are active low. Specifically, the `memWrite` and `memRead` wires are 1 when a memory write or memory read is desired.
- The built-in RAM and ROM circuits are *word* addressable, whereas the memories in the textbook are *byte* addressable. Make sure your circuit converts between them correctly. (The Instruction Memory circuit provided takes bytes as input, not words.)
- Read carefully about how MIPS implements jump and branch. In particular, notice (1) how MIPS fills bits 28-31 of the jump target and (2) that the branch offset is added to `PC + 4`, not `PC`.
- You need a minimum of two tests for `beq`.
- Make use of probes, watches, and displays to help debug your circuit.
- Be aware of pseudo instructions when writing MIPS code. For example, `lw $16, val1` is a pseudo instruction. It won't work unless both `lui` and `lw` work.

## Deliverables

Once you have thoroughly tested your circuit, upload it on the assignment page to run it against the instructor's tests.

**Note:** A test will not report all failures. It typically reports only the first. Do not assume because you update your circuit for a particular case that it is completely correct. Again, test thoroughly!

Only the file listed below will be accepted; others will be ignored. The combined size limit of all uploaded files is **5 MB**.

- **`PHSingleCycleCPU.jls`**
