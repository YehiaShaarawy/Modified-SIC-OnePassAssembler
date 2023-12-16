# Modi-SIC One Pass Assembler

It is a program that performs one-pass assembly for the Modified Simple Instruction Computer (modi-SIC). <br> 
The assembler will translate assembly code into machine code compatible with the Modified Simplified Instructional Computer. <br><br><br>

The modi-SIC architecture retains the instruction set and Format 3 instructions from the original SIC (Simple
Instruction Computer). <br>It also includes the concept of reserving variables in memory using directives such as
BYTE, WORD, RESB, and RESW.

### Key Modifications and Extensions:
• Format 1 Instructions: Modi-SIC extends its capabilities by introducing Format 1 instructions,expanding the range of supported operations.<br><br>
• Immediate Instructions (Format 3): Modi-SIC introduces immediate instructions, allowing for the handling of immediate values passed as integers. This provides greater flexibility in executing instructions.<br><br>
• Relocation: The Modi-SIC also supports relocation by using the masking bits in the text records, where 1 denotes memory location that needs modification and 0 otherwise.

### Instructions Handling:
The assembler will process the assembly code in a single pass, generating machine code (HTE records) for execution on the modi-SIC. It will consider both the original SIC Format 3 instructions and the newly introduced Format 1 instructions and immediate instructions

# Instruction Set

<img width="424" alt="modisic" src="https://github.com/YehiaSharawy/Modified-SIC-ASSEMBLER/assets/65984199/c5533b21-0dae-42eb-ac25-7d68fb37bfc7">

# Instruction Format
## Format 1
|OPCODE (8 Bits)|
|---|

## Format 3
All Type 3 instruction could be immediate instructions this is done by a new division of bits of instructions of Type 3 (Format 3) as shown in following table.

|OPCODE (7 Bits)|Immediate Flag [i] (1 Bit)|Indexing Flag [x] (1 Bit)|Address (15 Bits)|
|---|---|---|---|

The modification applied on the opcode as
<ol>
<li>Only opcode is represented as 7 bits (not 8) as in SIC</li>
<li>The 8th bit of the opcode represents the immediate flag (i) which has two value</li>
<ul>
a. 0 if the instruction without immediate value (has an address) <br>
b. 1 if the instruction with immediate value
</ul>
</ol>

# Implementation

### Input
It takes as an input a text file (in.txt) that contains modi-SIC assembly program. <br>
Remember that The modi-SIC program includes Format 1 instruction of SIC/XE.

### Output
A generated symbol table file (symbolTable.txt) for all the symbols extracted from the program.<br><br>
A generated complete HTE records (objectcode.txt) which contain ne header, one or more text records (including masking bits) and one end record.

