import java.io.IOException;
import java.text.Format;

public class Main {
    public static void main(String[] args) throws IOException {
//        opcode op = new opcode();
//        System.out.println(op.getOpOpcode("ADD  "));

        onepassasm asm = new onepassasm();
        asm.assemble();


//        String test = Integer.toHexString(Integer.parseInt("10101001001000000001111",2)).toUpperCase();
//        System.out.println(test);
//        String.format("%06d", Integer.parseInt(Integer.toHexString(Integer.parseInt("10101001001000000001111",2)).toUpperCase()));

//        1101110010;
//        String hex = String.format("%05d", Integer.parseInt(Integer.toHexString(Integer.parseInt("0101000100",2))));
//        System.out.println(hex);
//        System.out.println(Long.toHexString(Long.parseLong("1101110010",2)));
//        String immediate = String.format("%015d",Long.parseLong(Long.toBinaryString(Long.parseLong("0",16))));
//        System.out.println(immediate);

//        locationCounter lc = new locationCounter();
//        lc.initializeLocCtr(1000);
//        System.out.println(lc.getLocCtr());
//        lc.incrementLocCtr("18");
//        System.out.println(lc.getLocCtr());

//        System.out.println(Integer.toHexString(10));

//        symbolTable sym = new symbolTable();

//        System.out.println(sym.getSymbolTable());
//        System.out.println(sym.checkSYMTAB("1000"));
//        sym.addSymbol("EOF","");
//        sym.addSymbol("leo","1003");
//        System.out.println(sym.getSymbolTable());
//        sym.chcekLabel("EOF","1000");
//        System.out.println(sym.getSymbolTable());
//        System.out.println(sym.getSymbolTable());
//        System.out.println(sym.checkSYMTAB("1000"));

//        If opcode = 'START' then{
//            StartingAdd = operand
//            LocationCtr = starting add
//            Read the next input
//        }else{
//            LocationCtr=0
//            Create header record and write object program
//            Initialize the 1st TEXT record
//
//            While(opcode != 'END'){
//                If (label in symtab){
//                    If(label value is null){
//                        Label value = loactionCtr
//                        Generate text record with operas address of each entry In the linked list
//                        Delete linked list
//                    }
//                }else{
//                    Insert(Label,LocCtr) into symtab
//                }
//                If(operand address in symtab){
//                    if(symbol value != null)
//                    operand add = symbol value
//                            Else
//                    Insert a node at the end of the linked list with address as Loctctr+1
//                }else{
//                    Insert(symbol name,null) into symtab
//                    Create a linked list with address as locCtr+1
//                }
//                Generate object code
//                locctr+=3
//            }else if(opcode = 'WORD'){
//                locctr +=3;
//                objectcode = operand
//            }else if(opcode = 'WORD'){
//                locctr +=3;
//                objectcode = operand
//            }else if(opcode = 'WORD'){
//                locctr +=3;
//                objectcode = operand
//            }else{
//
//            }
//        }
    }
}

