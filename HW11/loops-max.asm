# Name: Dante Givens

.text
.globl max

max:
    move $v0, $zero
    blez $a1, max_done
    nop
    lw   $v0, 0($a0)
    addiu $t0, $a0, 4
    addiu $t1, $a1, -1

max_loop:
    blez $t1, max_done
    nop
    lw   $t2, 0($t0)
    slt  $t3, $v0, $t2
    beq  $t3, $zero, max_next
    nop
    move $v0, $t2

max_next:
    addiu $t0, $t0, 4
    addiu $t1, $t1, -1
    b    max_loop
    nop

max_done:
    jr $ra