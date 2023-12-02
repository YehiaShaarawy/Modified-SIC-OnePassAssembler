import java.util.ArrayList;

public class opcode {
    private ArrayList<opcode> instructionSet = new ArrayList<>(32);
    private String mnemonic;
    private String opcode;
    private int format;
    public String getMnemonic() {
        return mnemonic;
    }
    public String getOpcode() {
        return opcode;
    }
    public int getFormat() {
        return format;
    }

    public opcode(){
        addInstructionsToTable();
    }
    public opcode(String mnemonic, String  opcode, int format){
        this.mnemonic = mnemonic;
        this.opcode = opcode;
        this.format = format;
    }

    //INSTRUCTION SET
    public void addInstructionsToTable(){
        instructionSet.add(new opcode("ADD  ","18",3));
        instructionSet.add(new opcode("AND  ","40",3));
        instructionSet.add(new opcode("COMP ","28",3));
        instructionSet.add(new opcode("DIV  ","24",3));
        instructionSet.add(new opcode("J    ","3C",3));
        instructionSet.add(new opcode("JEQ  ","30",3));
        instructionSet.add(new opcode("JGT  ","34",3));
        instructionSet.add(new opcode("JLT  ","38",3));
        instructionSet.add(new opcode("JSUB ","48",3));
        instructionSet.add(new opcode("LDA  ","00",3));
        instructionSet.add(new opcode("LDCH ","50",3));
        instructionSet.add(new opcode("LDL  ","08",3));
        instructionSet.add(new opcode("LDX  ","04",3));
        instructionSet.add(new opcode("MUL  ","20",3));
        instructionSet.add(new opcode("OR   ","44",3));
        instructionSet.add(new opcode("RD   ","D8",3));
        instructionSet.add(new opcode("RSUB ","4C",3));
        instructionSet.add(new opcode("STA  ","0C",3));
        instructionSet.add(new opcode("STCH ","54",3));
        instructionSet.add(new opcode("STL  ","14",3));
        instructionSet.add(new opcode("STSW ","E8",3));
        instructionSet.add(new opcode("STX  ","10",3));
        instructionSet.add(new opcode("SUB  ","1C",3));
        instructionSet.add(new opcode("TD   ","E0",3));
        instructionSet.add(new opcode("TIX  ","2C",3));
        instructionSet.add(new opcode("WD   ","DC",3));
        instructionSet.add(new opcode("FIX  ","C4",1));
        instructionSet.add(new opcode("FLOAT","C0",1));
        instructionSet.add(new opcode("HIO  ","F4",1));
        instructionSet.add(new opcode("NORM ","C8",1));
        instructionSet.add(new opcode("SIO  ","F0",1));
        instructionSet.add(new opcode("TIO  ","F8",1));
    }
    // Getting Opcode Mnemonic
    public String getOpMnemonic(String opcode){
        for(opcode op : instructionSet){
            if(op.getOpcode().equals(opcode))
                return op.getMnemonic();
        }
        return null;
    }
    // Getting Opcode
    public String getOpOpcode(String mnemonic){
        for (opcode op : instructionSet){
            if (op.getMnemonic().equals(mnemonic))
                return op.getOpcode();
        }
        return null;
    }
    // Getting Opcode format
    public int getOpFormat(String mnemonic){
        for(opcode op : instructionSet){
            if(op.getMnemonic().equals(mnemonic))
                return op.getFormat();
        }
        return 0;
    }
    // Extract the value of a specific bit in an integer at a given position. The result will be 0 if the bit is originally 0 and 1 if the bit is originally 1 at the specified position.
    public int getOpBit(int input, int position){
        return (input>>position)&1;
    }
}
