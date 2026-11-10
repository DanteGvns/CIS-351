# Name: Dante Givens
# Partner: ADD PARTNER NAME IF APPLICABLE

.text
.globl close10

close10:
    li   $t2, 10
    subu $t0, $a0, $t2
    slti $t3, $a0, 10
    beq  $t3, $zero, close10_check_b
    nop
    subu $t0, $zero, $t0

close10_check_b:
    subu $t1, $a1, $t2
    slti $t3, $a1, 10
    beq  $t3, $zero, close10_compare
    nop
    subu $t1, $zero, $t1

close10_compare:
    move $v0, $zero
    beq  $t0, $t1, close10_done
    nop
    sltu $t2, $t0, $t1
    beq  $t2, $zero, close10_return_b
    nop
    move $v0, $a0
    b    close10_done
    nop

close10_return_b:
    move $v0, $a1

close10_done:
    jr $ra