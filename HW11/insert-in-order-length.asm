# Name: Dante Givens

.text
.globl insert_in_order_length

insert_in_order_length:
    sll  $t0, $a1, 2
    addu $t0, $a0, $t0
    move $t1, $a1

insert_in_order_length_loop:
    beq  $t1, $zero, insert_in_order_length_store
    nop
    lw   $t2, -4($t0)
    slt  $t3, $a2, $t2
    beq  $t3, $zero, insert_in_order_length_store
    nop
    sw   $t2, 0($t0)
    addiu $t0, $t0, -4
    addiu $t1, $t1, -1
    b    insert_in_order_length_loop
    nop

insert_in_order_length_store:
    sw   $a2, 0($t0)
    jr $ra