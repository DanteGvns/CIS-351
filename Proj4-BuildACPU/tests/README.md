# Whole-CPU test suite

Save the CPU in JLS, then run from `Proj4-BuildACPU`:

```powershell
.\runCpuTests.ps1
```

The runner tests the saved `PHSingleCycleCPU.jls` against MARS using the repository's DLUnit jar. It runs every `.a` file in this folder, continues after failures, and exits nonzero if any fail. The four instructor examples are separate; keep running those too.

To run or debug just one test:

```powershell
java -jar ..\JLS\DLUnit.jar PHSingleCycleCPU.jls builtin.SingleCycleCPUTest --param tests\storeTiming.a
.\marsAssembler.ps1 tests\storeTiming.a -o instructions
```

The assembler command replaces `instructions` for stepping through that program in JLS. If a defective branch/jump makes a test loop forever, interrupt it with Ctrl+C and debug that test individually.

## Coverage

| Program | Cases |
| --- | --- |
| `arithmetic.a` | `add`, `addi`, `sub`; negative immediates; immediate limits; carry/borrow across the low half; high-bit operands; immediate dependencies |
| `logicAndLui.a` | `and`, `or`, `nor`, `andi`, `ori`, `lui`; complementary masks; all-zero/all-one cases; clearing upper bits; replacing old register contents with LUI |
| `signedComparisons.a` | `slt`, `slti`; less/equal/greater; negative versus positive; two negatives; sign extension; safe extreme comparisons |
| `registers.a` | Writes and reads across registers 1-31; stores retain individual values; attempts to modify register zero; destinations alias either or both sources |
| `bitPatterns.a` | Each of 32 bit positions through logic, store, and load; individually saved observations; sign-bit preservation |
| `branchesAndJumps.a` | Taken/not-taken `beq`; equal negative values; zero offset; multi-instruction skips; negative branch displacement; forward/backward `j`; no delay-slot side effects; zero ALU result without branch |
| `memoryOffsets.a` | Initialized data; signed words; positive/negative byte offsets; -32768 offset; adjacent words; read-after-store; load destination aliases base; pseudo-address forms |
| `memoryAddressBits.a` | Sparse word locations separated by powers of two through the RAM address range; rereads expose address aliasing; preserves each observation |
| `storeTiming.a` | Consecutive stores; store-to-arithmetic/LUI/load/branch/jump transitions; skipped stores; load-to-store and ALU-to-store dependencies; final store immediately before halt |
| `integration.a` | Bounded array loop combining signed arithmetic, comparisons, branches, jumps, logic, pointer increments, loads, and stores |

The intended loops terminate, and the tests use only the assignment's supported machine instructions. The two deliberately used pseudo-address forms in `memoryOffsets.a` expand into supported instructions. No printing syscalls, `jr`, `bne`, multiplication instructions, or shift instructions are required.

All tests end by falling off their text segment in MARS. The DLUnit assembler appends the project's opcode-0x20 **halt** to the CPU program. This tests normal termination, including final-register updates and final-store completion; do not add MARS exit syscalls to these tests.

## Why the results remain observable

Most independent ALU results remain in different final registers. Tests that reuse registers preserve individual observations in data memory, which the installed DLUnit 1.1.2 CPU test also compares. Loads additionally expose stored values in registers. Branches and jumps skip poison instructions that would change final results if executed.

These programs deliberately avoid:

- Signed overflow in `add`/`addi`/`sub`, which MARS traps.
- Subtraction overflow in `slt`/`slti`, which the provided ALU does not handle.
- `andi`/`ori` immediates with bit 15 set, excluded by the assignment.
- Unaligned word accesses or accesses outside the supplied memories.

## Limits

Passing this suite is strong evidence, not proof of correctness. It does not enumerate every operand pair or timing condition, and each DLUnit invocation may report only its first mismatch.

In particular, these ordinary programs run near MARS's default text address and cannot establish that the **nonzero high nibble of a jump address** or a jump across a 256 MiB boundary is handled correctly. Inspect that wiring directly: jump bits 31-28 must come from **PC+4**, not PC or its low nibble. Also inspect RAM capacity, active-low control polarity, negative-edge register settings, and clock timing. Timing coverage applies to the clock configuration used when you run the tests.
