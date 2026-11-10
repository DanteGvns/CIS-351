# Name: Dante Givens
# Partner: ADD PARTNER NAME IF APPLICABLE

.text
.globl max_sentinel

max_sentinel:
    move $v0, $zero
    move $t0, $a0

max_sentinel_loop:
    lw   $t1, 0($t0)
    bltz $t1, max_sentinel_done
    nop
    slt  $t2, $v0, $t1
    beq  $t2, $zero, max_sentinel_next
    nop
    move $v0, $t1

max_sentinel_next:
    addiu $t0, $t0, 4
    b    max_sentinel_loop
    nop

max_sentinel_done:
    jr $ra