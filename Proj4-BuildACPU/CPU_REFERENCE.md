# Project 4 CPU Reference

## Purpose and sources

Read this file before continuing work on the Project 4 CPU. It preserves the structure and behavior of the textbook diagrams shared in the conversation, independently of chat history.

- **Figure 7.13, printed page 377:** the single-cycle datapath with the active paths and control values for an `or` instruction highlighted. This is the latest screenshot the user shared.
- **Figure 7.14, printed page 379:** the same basic datapath extended with jump support. This is the diagram required by the assignment and was shared earlier in the conversation.
- **Figure 7.4, printed page 369:** an earlier, incomplete construction step showing immediate sign extension, not the complete CPU.
- Textbook: [Digital Design and Computer Architecture](https://acs.pub.ro/~cpop/CN1_2CB_2CD/CN1-NewCourses/Digital%20Design%20and%20Computer%20Architecture.pdf), Harris and Harris, Section 7.3.
- [Project instructions](INSTRUCTIONS.md) are authoritative for the required instructions and JLS-specific differences.
- [JLS circuit](PHSingleCycleCPU.jls) contains the project implementation. This document explains the intended design; it does **not** certify that the current circuit is fully wired or working. Its internal wiring was not audited when this reference was written.

This is a functional reconstruction and explanation, not a pixel-for-pixel reproduction of the figures.

## 1. Big picture: what the CPU does

This is a 32-bit, single-cycle MIPS processor. An instruction is fetched, decoded, executed, and, when needed, accesses data memory and writes a register within one clock cycle. These are logical steps, not separate pipeline stages.

The state is held in the program counter (PC), register file, and data memory. The control unit and the combinational datapath determine the next state from the current instruction and operands.

Instruction memory and data memory are separate, allowing instruction fetch and a load/store access during the same cycle.

### How the diagram is arranged

- **Far left:** the next-PC selection muxes and the PC register.
- **Left:** instruction memory, addressed by the PC.
- **Upper middle:** control unit, receiving the instruction's opcode and function fields.
- **Center:** register file, with two read ports and one write port.
- **Center-right:** ALU and its second-operand mux.
- **Right:** data memory and the result/writeback mux.
- **Bottom:** immediate sign extension, branch-offset shift, and branch-target adder.
- **Lower left:** the separate adder that computes `PC + 4`.
- **Long return paths:** the result bus returns from the right to register-file input `WD3`; next-PC paths return toward the PC on the left.
- **Figure 7.14 addition:** a jump-target path and an extra mux immediately before the PC.

### Simplified dataflow

```text
 PC --> Instruction Memory --> Instr
  |                            |-- opcode, funct --> Control Unit
  |                            |-- rs -----------> Register File A1
  |                            |-- rt -----------> Register File A2
  |                            |-- rt, rd --------> destination mux --> A3
  |                            `-- immediate ----> sign extension --+
  |                                                                |
  |         Register File RD1 ---------------------------> ALU A     |
  |         Register File RD2 --> operand mux -----------> ALU B <---+
  |                    |
  |                    `--------------------------------> Data Memory WD
  |                                                       ^
  |                                        ALUResult ----> address
  |                                            |          |
  |                                            |          `--> ReadData
  |                                            `--> result mux <--+
  |                                                     |
  |                                                     `--> Result --> WD3
  |
  +--> PC + 4 ---------------------> branch mux --> jump mux --> PC'
          |                              ^             ^
          +--> + (SignImm << 2) ---------+             |
          `--> upper 4 bits + jump field + 00 ---------+
```

The operand mux selects either `RD2` or the extended immediate, not both. The result mux selects either `ALUResult` or `ReadData`. Control signals select the appropriate path.

## 2. Instruction fields and bus widths

`Instr` is 32 bits. The numbers printed next to wires are bit slices, not literal values.

| Field | Bits | Width | Use |
| --- | --- | --- | --- |
| Opcode | `Instr[31:26]` | 6 | Main instruction decoding |
| `rs` | `Instr[25:21]` | 5 | First source register, connected to `A1` |
| `rt` | `Instr[20:16]` | 5 | Second source register at `A2`, or I-type destination |
| `rd` | `Instr[15:11]` | 5 | R-type destination |
| Function | `Instr[5:0]` | 6 | Selects the R-type ALU operation |
| Immediate | `Instr[15:0]` | 16 | Offset or immediate operand |
| Jump index | `Instr[25:0]` | 26 | Encoded jump target field |

The register file has 32 registers. Register addresses `A1`, `A2`, `A3`, and `WriteReg` are 5 bits; register data ports are 32 bits. MIPS register zero must remain zero.

`PC`, `PCPlus4`, `PCBranch`, `PCJump`, `SignImm`, `SrcA`, `SrcB`, `ALUResult`, `WriteData`, `ReadData`, and `Result` are all 32-bit values. The textbook `ALUControl` bus is 3 bits. Do not assume the supplied JLS ALU has the identical control encoding or interface without checking it.

## 3. Datapath connections and equations

Notation: `R[n]` is register n; braces mean bit concatenation; `X` means a control value is irrelevant for that instruction. Equations below describe the textbook baseline, before project-specific additions such as `lui` and `halt`.

### Fetch and decode

```text
InstructionMemory.address = PC
Instr = InstructionMemory.readData
ControlUnit.Op = Instr[31:26]
ControlUnit.Funct = Instr[5:0]
PCPlus4 = PC + 4
```

PC is a **byte address**, so advancing one 32-bit instruction adds 4, not 1.

### Read operands and select the destination

```text
A1 = Instr[25:21]
A2 = Instr[20:16]
RD1 = R[A1]
RD2 = R[A2]
WriteReg = RegDst ? Instr[15:11] : Instr[20:16]
A3 = WriteReg
```

The register file reads both source ports even when an instruction does not need both values. Reading `rt` does not imply that `rt` will be modified.

### Extend the immediate and execute

```text
SignImm = {16 copies of Instr[15], Instr[15:0]}
SrcA = RD1
SrcB = ALUSrc ? SignImm : RD2
ALUResult = ALU(SrcA, SrcB, ALUControl)
Zero = (ALUResult == 0)
```

For example, sign-extending `0xFFFC` produces `0xFFFFFFFC` (-4), not `0x0000FFFC`.

The immediate has different uses:

- `lw`/`sw`: signed byte offset added to the base register.
- `addi`/`slti`: signed immediate operand.
- `beq`: signed instruction displacement, shifted left by 2 before addition to `PC + 4`.

### Data memory and writeback

```text
DataMemory.address = ALUResult          [textbook byte-address view]
WriteData = RD2
DataMemory.writeData = WriteData
ReadData = DataMemory.readData
Result = MemtoReg ? ReadData : ALUResult
WD3 = Result
WE3 = RegWrite
```

When register writing is enabled, the destination receives `Result` at the active register-file clock edge.

For a store, `RD2` supplies the value to store **directly**, while the ALU uses the immediate to calculate the address. Connecting memory write data to `SrcB` would store the offset instead of the register value.

### Branch and jump

```text
PCBranch = PCPlus4 + (SignImm << 2)
PCSrc = Branch AND Zero
BranchSelectedPC = PCSrc ? PCBranch : PCPlus4
PCJump = {PCPlus4[31:28], Instr[25:0], 2'b00}
PC' = Jump ? PCJump : BranchSelectedPC
```

- `beq` makes the ALU subtract `RD2` from `RD1`; a zero result means the registers are equal.
- A zero ALU result alone does not take a branch: `Branch` must also be asserted.
- The branch displacement is relative to **`PC + 4`**, not PC.
- The jump address takes its high four bits from **`PC + 4`**, not directly from PC. This distinction matters at a 256 MiB boundary.
- The jump field is shifted/concatenated, not sign-extended.
- Figure 7.13 does not yet include the jump mux. Figure 7.14 adds it after the branch mux.
- This simple datapath selects the target for the next instruction; it does not implement a branch delay slot.

## 4. What the highlighted `or` example means

Figure 7.13 traces an R-type instruction of the form:

```text
or rd, rs, rt
R[rd] = R[rs] OR R[rt]
```

The highlighted control values are:

| Signal | Value | Effect |
| --- | --- | --- |
| `RegWrite` | 1 | Enable the register-file write |
| `RegDst` | 1 | Select `rd`, not `rt`, as the destination |
| `ALUSrc` | 0 | Select `RD2`, not the immediate |
| `ALUControl` | `001` | Bitwise OR in this textbook ALU |
| `MemWrite` | 0 | Do not write data memory |
| `MemtoReg` | 0 | Write back the ALU result, not memory data |
| `Branch` | 0 | Force `PCSrc` low, regardless of `Zero` |

Execution path:

1. PC addresses instruction memory.
2. The instruction identifies `rs`, `rt`, and `rd`; the control unit recognizes OR.
3. The register file outputs `R[rs]` and `R[rt]`.
4. The ALU computes their bitwise OR.
5. The result mux sends `ALUResult` along the long return wire to `WD3`.
6. The register file writes that value into `rd`; the PC advances to `PC + 4`.

Data memory is not used for an architectural read or write. Its inputs can still carry values, but its output is not selected and writing is disabled. The branch-target circuitry may calculate a value too, but that value is not selected.

In Figure 7.14, `Jump = 0` for `or`, preserving this same behavior. Faded or unhighlighted components are still part of the CPU, not components to omit.

## 5. How the baseline instruction classes use the same hardware

This table uses the textbook's logical, active-high controls, not the active-low pins of JLS RAM/ROM.

| Class | RegWrite | RegDst | ALUSrc | Branch | MemWrite | MemtoReg | Jump | ALU action |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| R-type arithmetic/logic | 1 | 1 | 0 | 0 | 0 | 0 | 0 | Selected by function |
| `lw` | 1 | 0 | 1 | 0 | 0 | 1 | 0 | Add base and offset |
| `sw` | 0 | X | 1 | 0 | 1 | X | 0 | Add base and offset |
| `beq` | 0 | X | 0 | 1 | 0 | X | 0 | Subtract and check Zero |
| `addi` | 1 | 0 | 1 | 0 | 0 | 0 | 0 | Add register and immediate |
| `j` | 0 | X | X | X | 0 | X | 1 | Irrelevant |

The main decoder's textbook `ALUOp` is an intermediate control: `00` requests addition, `01` subtraction, and `10` function-based decoding. It is not the same bus as the final `ALUControl` input to the ALU. The project provides a control unit, so reproducing the decoder truth table is not required.

## 6. Project requirements beyond simply copying the figure

The assignment requires **16 instructions**:

`add`, `addi`, `and`, `andi`, `beq`, `halt`, `j`, `lw`, `lui`, `nor`, `or`, `ori`, `slt`, `slti`, `sw`, `sub`.

- `add`, `sub`, `and`, `or`, `nor`, and `slt` use the R-type register-to-register path. `slt` produces 1 or 0 from a signed comparison.
- `addi`, `andi`, `ori`, and `slti` use an immediate and write `rt`, with the appropriate ALU operation.
- `lui` must write `{Instr[15:0], 16'b0}` to `rt`. The basic sign-extension/ALU path alone does not establish how this is implemented; inspect the supplied components before adding hardware.
- `halt` is a project-specific requirement with opcode **0x20**, not a behavior defined by these textbook diagrams. Inspect the starter control and halt interface before deciding how to stop execution.
- Standard MIPS zero-extends `andi`/`ori` immediates, but the assignment explicitly warns that its implementation gives incorrect results for negative immediates or bit 15 set. Follow the documented test restriction; do not assume the starter implements full MIPS immediate semantics.
- The supplied `slt`/`slti` implementation is also limited: tests must avoid subtraction overflow.

### JLS-specific integration

1. **Names:** retain `PHSingleCycleCPU.jls`, instruction memory `InstMemory`, register file `RegisterFile`, and RAM `MainMemory`.
2. **Capacity:** configure `MainMemory` with `2^20` words.
3. **Clock edge:** the provided register file uses negative-edge-triggered flip-flops. PC and other added registers must also use negative-edge triggering. The falling edge is a cycle boundary; combinational logic must settle before its result is captured at the next active edge. Do not copy the textbook clock polarity blindly.
4. **Memory controls:** JLS `WE`, `OE`, and `CS` are active low. The control unit's `memWrite` and `memRead` signals are active high. Adapt their polarity and obey the memory component's timing; directly wiring `memWrite` to an active-low `WE` reverses its meaning.
5. **Address units:** built-in JLS RAM/ROM are word-addressed, while the textbook uses byte addresses. For aligned words, a byte address is converted to a word index by removing the low two bits. Check the actual component address width and mapping before wiring slices.
6. **Do not double-convert instruction addresses:** the provided instruction-memory subcircuit already accepts byte addresses, according to the assignment. Feed it PC in bytes.
7. **Control interface:** names and encodings in the diagram describe the textbook. Inspect the supplied control unit and ALU ports before connecting them; the starter may contain additions for the extra instructions.

## 7. Debugging reference

Useful probes:

```text
PC, Instr, PCPlus4, PCBranch, PCJump, PC'
rs, rt, rd, WriteReg
RD1, RD2, SignImm, SrcA, SrcB, ALUResult, Zero
WriteData, ReadData, Result
RegWrite, RegDst, ALUSrc, ALUControl, Branch, PCSrc, Jump
memRead, memWrite, actual RAM WE/OE/CS inputs
```

For one failing instruction, check fetch and decode first, then operands, ALU result, memory access, writeback, and next PC. Compare values at the intended sampling time, not during transient propagation.

Typical wiring mistakes to rule out:

- Swapped mux inputs or select polarity.
- Selecting `rt` instead of `rd` for an R-type result.
- Zero-extending a negative load/store offset.
- Using `SrcB` instead of `RD2` as store data.
- Selecting memory data for an arithmetic instruction.
- Forgetting to shift the branch offset by 2.
- Adding the branch offset to PC instead of `PC + 4`.
- Using PC rather than `PC + 4` for the jump target's high nibble.
- Applying byte-to-word conversion twice, or not applying it where required.
- Connecting active-high control wires directly to active-low memory enables.

Use DLUnit to find failing programs, then step through them in JLS. DLUnit compares final register states, so preserve meaningful test results rather than overwriting them. Test both taken and not-taken `beq`; load stored data back into a register to expose store errors. See [the project instructions](INSTRUCTIONS.md) for commands and submission requirements.

## 8. Resume checklist for future work

- Read this reference and [the project instructions](INSTRUCTIONS.md).
- Inspect the actual JLS circuit before claiming any component or path is finished.
- Treat Figure 7.14 as the full baseline; Figure 7.13 explains the OR execution path.
- Reuse the provided control unit and other components where suitable.
- Verify extra-instruction support, clocking, memory polarity, and address conversion rather than assuming the textbook diagram handles the JLS details.
- Build and test the circuit separately; writing this reference is not implementation or simulation validation.
