# Name: Dante Givens

.text
.globl sum13

sum13:
    move $v0, $zero
    move $t0, $a0
    move $t1, $a1
    li   $t2, 13

sum13_loop:
    blez $t1, sum13_done
    nop
    lw   $t3, 0($t0)
    beq  $t3, $t2, sum13_skip
    nop
    addu $v0, $v0, $t3
    addiu $t0, $t0, 4
    addiu $t1, $t1, -1
    b    sum13_loop
    nop

sum13_skip:
    addiu $t0, $t0, 8
    addiu $t1, $t1, -2
    b    sum13_loop
    nop

sum13_done:
    jr $ra