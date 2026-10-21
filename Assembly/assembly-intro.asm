.globl monkeyTrouble sleepIn posNeg
.text

#Dante Givens



# You can use these lines to run your program directly in MARS for
# troubleshooting. Change the values of $a0 and $a1 to try out your
# function with different arguments.
# As the code is written now, it will run all three functions. You can
# comment out a `jal` call if you do not want the function to run.
# ----------------------------------------------------------------------------
addi $a0, $0, 1		# $a0 and $a1 are used to store function arguments
addi $a1, $0, 1
jal monkeyTrouble	# functions are called using `jal`

addi $a0, $0, 1
addi $a1, $0, 1
jal sleepIn

addi $a0, $0, 1
addi $a1, $0, 1
jal posNeg

addi $v0, $0, 10	# syscall calls the operating system to perform various tasks
syscall # It looks in $v0 to determine what to do
      # In the case of $v0 == 10, syscall exits the program
      # Without this, MARS would keep running with the next line
      # of code
# ----------------------------------------------------------------------------
#
# Lines below here are where you will write your functions.
#
monkeyTrouble:
    xor $v0, $a0, $a1     # v0 = 0 if equal, 1 if different
    xori $v0, $v0, 1      # invert: equal -> 1, different -> 0
    jr $ra

sleepIn:
    # Compute !weekday
    not $t0, $a0          # flip bits
    andi $t0, $t0, 1      # keep only LSB

    # v0 = (!weekday) OR vacation
    or $v0, $t0, $a1
    jr $ra

posNeg:
    # Check a < 0
    slti $t0, $a0, 0      # t0 = 1 if a < 0

    # Check b < 0
    slti $t1, $a1, 0      # t1 = 1 if b < 0

    # Case 1: negative == 1 → both negative
    and $t2, $t0, $t1     # t2 = 1 if both negative

    # Case 2: negative == 0 -> one negative, one positive
    xor $t3, $t0, $t1     # t3 = 1 if exactly one is negative

    # Select correct result:
    # If negative == 1 -> return t2
    # If negative == 0 -> return t3
    #
    # v0 = (negative AND t2) OR ((!negative) AND t3)

    and $t4, $a2, $t2     # negative AND both-negative
    not $t5, $a2
    andi $t5, $t5, 1      # !negative
    and $t6, $t5, $t3     # (!negative) AND one-negative
    or  $v0, $t4, $t6

    jr $ra
