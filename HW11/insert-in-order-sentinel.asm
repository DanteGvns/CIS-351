# Name: Dante Givens

.text
.globl insert_in_order_sentinel

insert_in_order_sentinel:
    move $t0, $a0

insert_in_order_sentinel_find_end:
    lw   $t1, 0($t0)
    bltz $t1, insert_in_order_sentinel_move_end
    nop
    addiu $t0, $t0, 4
    b    insert_in_order_sentinel_find_end
    nop

insert_in_order_sentinel_move_end:
    sw   $t1, 4($t0)

insert_in_order_sentinel_loop:
    beq  $t0, $a0, insert_in_order_sentinel_store
    nop
    lw   $t1, -4($t0)
    slt  $t2, $a1, $t1
    beq  $t2, $zero, insert_in_order_sentinel_store
    nop
    sw   $t1, 0($t0)
    addiu $t0, $t0, -4
    b    insert_in_order_sentinel_loop
    nop

insert_in_order_sentinel_store:
    sw   $a1, 0($t0)
    jr $ra