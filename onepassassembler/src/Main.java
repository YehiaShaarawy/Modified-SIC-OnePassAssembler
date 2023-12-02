import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        opcode op = new opcode();
//        System.out.println(op.getOpOpcode("ADD  "));

        onepassasm asm = new onepassasm();
        asm.assemble();

//        System.out.println(Integer.toHexString(10));

//        symbolTable sym = new symbolTable();
//        System.out.println(sym.getSymbolTable());
//        System.out.println(sym.checkSYMTAB("1000"));
//        sym.addSymbol("EOF","1000");
//        sym.addSymbol("EOF","1003");
//        System.out.println(sym.getSymbolTable());
//        System.out.println(sym.checkSYMTAB("1000"));

    }
}

