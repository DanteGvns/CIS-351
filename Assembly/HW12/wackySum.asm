.globl wackySum
.globl combineFour
.text
#Dante Givens


wackySum:
    #save the return address, $s0-$s2, and running sum: 5(4)=20 bytes
    addi $sp, $sp, -20

    #save current return address to stack
    sw $ra, 0($sp)
    #save $s0-$s2 so we can use them later
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $s2, 12($sp)
    sw $zero, 16($sp) # running sum placed on stack

    #move our params into the saved regs
    move $s0, $a0
    move $s1, $a1
    move $s2, $a2

    loop:
        bgt $s0, $s1, done   # if (i > b) exit loop

        #set our 4 parameters $a0-$a3
        #$a0 = i
        move $a0, $s0 

        #$a1 = (i+1)/2
        addi $a1, $s0, 1
        li $t0, 2
        div $a1, $t0
        mflo $a1
        
        #$a2 = (i+2)/2
        addi $a2, $s0, 2
        div $a2, $t0
        mflo $a2

        #$a3 = i+3
        addi $a3, $s0, 3

        jal combineFour
        #$v0 should now have the return from combineFour
        lw $t0, 16($sp)
        addu $t0, $t0, $v0
        sw $t0, 16($sp)

        add $s0, $s0, $s2 # i += c
        j loop
    done:
        lw $v0, 16($sp)

    #put everything back how we found it
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1, 8($sp)
    lw $s2, 12($sp)

    add $sp, $sp, 20
    jr $ra

#Combine four ints and return their integer sum if even, 
#otherwise return the sum with integer division by two.
combineFour:
    # This leaf procedure uses only caller-saved registers, so no stack work is needed.
    addu $t0, $a0, $a1
    addu $t1, $a2, $a3
    addu $t0, $t0, $t1 # t0 = a+b+c+d

    li $t1, 2
    div $t0, $t1
    mflo $t2 # quotient of $t0/$t1
    mfhi $t1 # remainder of $t0/$t1

    bne $t1, $zero, ifHasRemainder
    move $v0, $t0
    j endIfHasRemainder
ifHasRemainder:
    move $v0, $t2
endIfHasRemainder:
    jr $ra

