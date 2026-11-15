.globl wackySum
.text
#Dante Givens


wackySum:
    #we have 3 ints/parameters + we need to save the return address 
    #+ we'll want to keep out running sum total safe
    #so well take 5 ints worth of space on the stack 5(4)=20
    addi $sp, $sp, -20

    #save current return address to stack
    sw $ra, 0($sp)
    #save $s0-$s2 so we can use them later
    sw $s0, 4($sp)
    sw $s1 8($sp)
    sw $s2, 12($sp)
    sw $s3, 16($sp)

    #now we can actually start working
    #move are params into the save regs
    move $s0, $a0
    move $s1, $a1
    move $s2, $a2
    li $s3, 0 #clear because we dont know what could be in here

    move $t0, $s0 #i = a

    loop:
        bgt $t0, $s1, done   # if (i > b) exit loop

        #set our 4 parameters $a0-$a3
        #$a0 = i
        move $a0, $t0 

        #$a1 = (i+1)/2
        addi $a1, $t0, 1
        li $t1, 2
        div $a1, $t1
        mflo $a1
        
        #$a2 = (i+2)/2
        addi $a2, $t0, 2
        div $a2, $t1
        mflo $a2

        #$a3 = i+3
        addi $a3, $t0, 3

        jal combineFour
        #$v0 should now have the return from combine 4
        add $s3, $s3, $v0 #sum += combineFour(i, (i+1)/2, (i+2)/2, (i+3));

        add $t0, $t0, $s2 # i += c
        j loop
    done:
        move $v0, $s3

    #put everything back how we found it
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1 8($sp)
    lw $s2, 12($sp)
    lw $s3, 16($sp)

    add $sp, $sp, 20
    jr $ra

#Combine four ints and return their integer sum if even, 
#otherwise return the sum with integer division by two.
combineFour:
    #we have 4 ints/parameters + we need to save the return address
    #so well take 5 ints worth of space on the stack 5(4)=20
    addi $sp, $sp, -20
    #save current return address to stack
    sw $ra, 0($sp)
    #save $s0-$s2 so we can use them later
    sw $s0, 4($sp)
    sw $s1 8($sp)
    sw $s2, 12($sp)
    sw $s3, 16($sp)

    #move are params into the save regs
    move $s0, $a0
    move $s1, $a1
    move $s2, $a2
    move $s3, $a3

    #actual function starts here
    li $t0, 0
    add $t0, $a0, $a1
    add $t1, $a2, $a3
    add $t0, $t0, $t1 #t0 = a+b+c+d

    li $t1, 2
    div $t0, $t1
    mflo $t2 # quotient of $t0/$t1
    mfhi $t3 # remainder of $t0/$t1

    li $v0, 0 #clear because we dont know what could be in here
    bne $t3, $zero, ifHasRemainder
    move $v0, $t0
    j endIfHasRemainder
ifHasRemainder:
    move $v0, $t2
endIfHasRemainder:
    #put everything back how we found it
    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1 8($sp)
    lw $s2, 12($sp)
    lw $s3, 16($sp)

    add $sp, $sp, 20
    jr $ra

