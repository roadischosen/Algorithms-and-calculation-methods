.text

.global ShakerSort
.type   ShakerSort, %function

ShakerSort:
    pushl %ebp
    movl %esp, %ebp
    pushl %ebx
    pushl %esi
    pushl %edi

    movl 8(%ebp), %esi                      # first element pointer

    movl 12(%ebp), %ebx                     # right border
    decl %ebx

    movl %ebx, %ebp                         # last swap index
    movl $1, %eax                           # left border
    cycle:
        movl %ebx, %ecx                     # for ecx from right border to the left
        backward:
        cmpl %eax, %ecx
        jl backward_end
            movl -4(%esi,%ecx,4), %edx      # load arr[ecx-1]
            movl (%esi,%ecx,4), %edi        # load arr[ecx]
            cmpl %edi, %edx
            jle dont_swap_backward
                movl %edi, -4(%esi,%ecx,4)
                movl %edx, (%esi,%ecx,4)
                movl %ecx, %ebp             # update last swap index
            dont_swap_backward:
            decl %ecx
        jmp backward
        backward_end:
        movl %ebp, %eax                     # shift left border
        incl %eax

        movl %eax, %ecx                     # for ecx from left border to the right
        forward:
        cmpl %ebx, %ecx
        jg forward_end
            movl -4(%esi,%ecx,4), %edx      # load arr[ecx-1]
            movl (%esi,%ecx,4), %edi        # load arr[ecx]
            cmpl %edi, %edx
            jle dont_swap_forward
                movl %edi, -4(%esi,%ecx,4)
                movl %edx, (%esi,%ecx,4)
                movl %ecx, %ebp             # update last swap index
            dont_swap_forward:
            incl %ecx
        jmp forward
        forward_end:
        movl %ebp, %ebx                     # shift right border
        decl %ebx
        cmpl %ebx, %eax
    jle cycle

    popl %edi
    popl %esi
    popl %ebx
    popl %ebp
    ret

.size ShakerSort, .-ShakerSort
