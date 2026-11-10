# Name: Dante Givens

.text
.globl intMax

intMax:
    move $v0, $a0
    slt  $t0, $v0, $a1
    beq  $t0, $zero, intMax_check_c
    nop
    move $v0, $a1

intMax_check_c:
    slt  $t0, $v0, $a2
    beq  $t0, $zero, intMax_done
    nop
    move $v0, $a2

intMax_done:
    jr $ra