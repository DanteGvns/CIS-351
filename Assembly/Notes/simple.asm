li $t0, 3
li $t1, 5
li $t2, 6 
li $t3, 7
	#set $t3 ti 1 if $t0 is < 7
	slti $t3, $t0, 7
	
	bne $t3, $zero, areEqual
	li $t1, 21
	j endIf

areEqual:
	li $t1, 14

endIf:
	li $t2, 15