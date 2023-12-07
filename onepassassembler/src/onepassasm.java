import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class onepassasm {
    private boolean createTStartAdd = false, newTextRecord = false;
    private String startingAddress = "", startingInstruction = "", firstInstructionAddress ="", maskingBitBinary ="";
    private int tRecordSkipped = 0 , textRecordLength = 0;
    private opcode op = new opcode();
    private symbolTable sym = new symbolTable();
    private locationCounter lc = new locationCounter();
    private StringBuilder headerRecord = new StringBuilder();
    private StringBuilder textRecord = new StringBuilder();
    private List<String> TextRecords = new ArrayList<>();
    private ArrayList<String> assemblyCode = new ArrayList<>();
    private String AssemblyFilePath = "src/in.txt";
    private String HTEFilePath = "src/objectcode.txt";
    private String SymbolTableFilePath = "src/symbolTable.txt";
    private BufferedReader AsmReader;
    private BufferedWriter AsmWriter = new BufferedWriter(new FileWriter("src/assembly.txt"));
    private BufferedWriter HteWriter = new BufferedWriter(new FileWriter(HTEFilePath));
    private BufferedWriter SymbolWriter = new BufferedWriter(new FileWriter(SymbolTableFilePath));

    public onepassasm() throws IOException {
    }
    private void insertToSYMTAB(String mnemonic, String address) throws IOException {
        SymbolWriter.write(mnemonic+"\t"+address+"\n");
        SymbolWriter.flush();
    }
    private void headerRecordAssembler(int row) throws IOException {
        String programName = assemblyCode.get(row).substring(0,6).toUpperCase();
        startingAddress = assemblyCode.get(row).substring(12,16); //setting the starting address
        insertToSYMTAB(programName,startingAddress);
        lc.initializeLocCtr(Integer.parseInt(startingAddress,16)); //initializing the location counter
        headerRecord.append("H"+String.format("%6s", programName).replace(' ', '0')+String.format("%06d",Integer.parseInt(lc.getLocCtr())));
    }

    private void newTRecord(){
        createTStartAdd=false; //so it doesn't create a new T.starting address.
        TextRecords.add(textRecord.toString());
        textRecord = new StringBuilder();
        generateStartTextRecord();
        tRecordSkipped=0;
        textRecordLength=0;
        newTextRecord = false;
    }
    public void cutTRecord(){
//        System.out.println("Masking bits ->"+maskingBitBinary +"\t\t"+ Integer.toHexString(Integer.parseInt(String.format("%-" + 12 + "s", maskingBitBinary).replace(' ', '0'),2)).toUpperCase());
        if (!newTextRecord){ // On the same T record, we calculate the length of the t record and add it to List which will hold all t records and start a new T record.
            textRecord.insert(7,String.format("%02X",Integer.parseInt(Integer.toString(Integer.parseInt(lc.getLocCtr(),16)-Integer.parseInt(firstInstructionAddress,16))),16)); // inserting the T record Length
            textRecord.insert(9,Integer.toHexString(Integer.parseInt(String.format("%-" + 12 + "s", maskingBitBinary).replace(' ', '0'),2)).toUpperCase()); // inserting the masking bit
            TextRecords.add(textRecord.toString());
            textRecord = new StringBuilder();
            createTStartAdd = true;
        }
        textRecordLength=0; //resetting the textrecord length for new T records for future.
        newTextRecord = true; // setting it to true to make a new t record.
    }
    private void generateStartTextRecord(){
        if(!createTStartAdd){ //if createTStartAdd is false which means that it's ready to generate T.starting address.
            textRecord.append("T"+String.format("%06X",Integer.parseInt(lc.getLocCtr(),16)));
            firstInstructionAddress = lc.getLocCtr(); //getting the locationCounter of first instruction for getting the length of the t record.
            createTStartAdd = true; //setting it to true,so it doesn't create a new first T record.
        }
    }
    private void generateTextRecord(String instruction){
        generateStartTextRecord(); //writing the first T record -> T.starting address.

        if(instruction.equals("RESW ")||instruction.equals("RESB ")){
            tRecordSkipped++; // skip variable increments how many times resw or resb has been skipped do it doesnt create a new t record [future case].
            cutTRecord();
        }

        if(!(instruction.equals("RESW ")||instruction.equals("RESB "))&&(tRecordSkipped>0)&&newTextRecord&&textRecordLength==0){ //if instruction isn't RESW / RESB, and check if there are skips and its read for new T record and making sure that the text record length is zero, Therefore creates a new T record.
            newTRecord();
            startingInstruction = lc.getLocCtr(); // for E record starting program
//            System.out.println("Masking bits ->"+maskingBitBinary +"\t\t"+ Integer.toHexString(Integer.parseInt(String.format("%-" + 12 + "s", maskingBitBinary).replace(' ', '0'),2)).toUpperCase());
            maskingBitBinary = "";
        }

        if(!(instruction.equals("RESW ")||instruction.equals("RESB "))&&(tRecordSkipped==0)&&textRecordLength>27){
//            System.out.println(firstInstructionAddress);
//            System.out.println(lc.getLocCtr());
            textRecord.insert(7,String.format("%02X",Integer.parseInt(Integer.toString(Integer.parseInt(lc.getLocCtr(),16)-Integer.parseInt(firstInstructionAddress,16))),16)); // inserting the T record Length
            textRecord.insert(9,Integer.toHexString(Integer.parseInt(String.format("%-" + 12 + "s", maskingBitBinary).replace(' ', '0'),2)).toUpperCase()); // inserting the masking bit
            newTRecord();
//            System.out.println("Masking bits ->"+maskingBitBinary+"\t\t"+ Integer.toHexString(Integer.parseInt(String.format("%-" + 12 + "s", maskingBitBinary).replace(' ', '0'),2)).toUpperCase());
            maskingBitBinary = "";
        }
        System.out.println("Text Record -> "+textRecord);
        System.out.println(TextRecords);
    }
    private void textRecordAssembler(int row) throws IOException {
        String label = assemblyCode.get(row).substring(0,6).toUpperCase();
        String instruction = assemblyCode.get(row).substring(6,11).toUpperCase();
        String opcode = op.getOpOpcode(instruction);
        int format = op.getOpInstFormat(instruction);
        String operand = assemblyCode.get(row).substring(12).toUpperCase();
        String objectcodeHex = "";

        //Writing the text record
        generateTextRecord(instruction);

        //Writing label to symbolTable
        if(label.equals("      "))
            label = "      ";
        else{
            sym.ForwardReferencing = new ArrayList<>(); //Resetting the forward referencing arraylist for new T record
            sym.addSymbol(label,lc.getLocCtr(),TextRecords);
            if(sym.cut){
                cutTRecord();
                for(String k : sym.ForwardReferencing) // for writing in the textRecords correctly
                    TextRecords.add(k);
                newTRecord();
                maskingBitBinary = "";
                sym.cut = false;
            }
            insertToSYMTAB(label,lc.getLocCtr());
        }

        //Writing Object Code
        if(instruction.equals("BYTE ")){
            if (assemblyCode.get(row).charAt(12) == 'C'){
                textRecordLength += assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 1).length(); //textRecordLength takes the length of Characters
                String characters = assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 1);
                for (int i = 0; i < characters.length(); i++) {
                    char character = characters.charAt(i);
                    String hexRepresentation = Integer.toHexString(character).toUpperCase();
                    objectcodeHex += hexRepresentation;
                }
//                System.out.println("OBJECT CODE C: "+objectcodeHex);
                textRecord.append(objectcodeHex);
            }
            else if (assemblyCode.get(row).charAt(12) == 'X') {
                textRecordLength += assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 1).length(); //textRecordLength takes the length of Hexanumbers
                objectcodeHex = assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 1);
                objectcodeHex = objectcodeHex.substring(0,objectcodeHex.indexOf("'"));
//                System.out.println("OBJECT CODE X: "+objectcodeHex);
                textRecord.append(objectcodeHex);
            }
            maskingBitBinary += "0";
        } else if (instruction.equals("WORD ")) {
            textRecordLength +=3;
            objectcodeHex = String.format("%06X",Integer.parseInt(operand));
            textRecord.append(objectcodeHex);
            maskingBitBinary += "0";
//            System.out.println("OBJECT CODE WORD: "+objectcodeHex);
        }else if (instruction.equals("RESW ")||instruction.equals("RESB ")){
            //Handling only
        } else if (instruction.equals("RSUB ")) {
            textRecordLength +=3;
            objectcodeHex = "4C0000";
            textRecord.append(objectcodeHex);
            maskingBitBinary += "0";
//            System.out.println("OBJECT CODE RSUB: "+objectcodeHex);
        } else { //Format 1 & 3
            if(format == 1){
                maskingBitBinary += "0";
                textRecordLength +=1;
                objectcodeHex = opcode;
                textRecord.append(objectcodeHex);
            }else if (format ==3) {
                maskingBitBinary += "1";
                textRecordLength +=3;
                //Converting opcode to binary
                String opcodeBinary = String.format("%07d",Long.parseLong(Long.toBinaryString(Long.parseLong(opcode,16))));
                //Checking the immediate value
                if(operand.substring(0,1).equals("#"))
                    opcodeBinary = opcodeBinary.substring(0,6)+'1'+opcodeBinary.substring(7);
                else
                    opcodeBinary = opcodeBinary.substring(0,6)+'0'+opcodeBinary.substring(7);
                //checking the index
                String opcodeIndex ="";
                if(operand.split(",").length>1)
                    opcodeIndex = opcodeBinary +'1';
                else
                    opcodeIndex = opcodeBinary +'0';
                //Format 3, immediate object code
                if(operand.substring(0,1).equals("#")){
                    String immediate = String.format("%015d",Long.parseLong(Long.toBinaryString(Long.parseLong(operand.substring(1,2),16))));
                    String opjectcodeBinary = opcodeIndex + immediate;
                    objectcodeHex = String.format("%06d", Integer.parseInt(Integer.toHexString(Integer.parseInt(opjectcodeBinary,2)).toUpperCase()));
                    textRecord.append(objectcodeHex);
//                    System.out.println("# OBJECT CODE ->"+objectcodeHex);
                } else if (operand.substring(6,7).equals(",")) { //Format 3 , Indexing object code
                    String address = sym.getOperand(operand.substring(0,6),lc.getLocCtr(),TextRecords);
                    String addressBinary = String.format("%015d",Long.parseLong(Long.toBinaryString(Long.parseLong(address,16))));
                    String opjectcodeBinary = opcodeIndex + addressBinary;
                    objectcodeHex = Integer.toHexString(Integer.parseInt(opjectcodeBinary,2)).toUpperCase();
                    textRecord.append(objectcodeHex);
//                    System.out.println("INDEX OBJECT CODE ->"+objectcodeHex);
                } else{ //Normal Format  3 object code
                    String address = sym.getOperand(operand.substring(0,6),lc.getLocCtr(),TextRecords);
                    objectcodeHex = opcode + address;
                    textRecord.append(objectcodeHex);
                }
            }
        }

        System.out.println(lc.getLocCtr()+"\t"+label+"\t"+instruction+"\t"+operand+"\t"+objectcodeHex+"\t"+textRecordLength);
        AsmWriter.write(lc.getLocCtr()+"\t"+label+"\t"+instruction+"\t"+operand+"\t"+objectcodeHex+"\n");
        AsmWriter.flush();

        //Writing Location Counter
        switch (instruction) {
            case "BYTE " -> {
                if (assemblyCode.get(row).charAt(12) == 'C')
                    lc.incrementLocCtr_Byte(assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 1).length());
                else if (assemblyCode.get(row).charAt(12) == 'X')
                    lc.incrementLocCtr_Byte((assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 4).length()) / 2); //4 due to 05'
            }
            case "WORD " -> lc.incrementLocCtr_Word();
            case "RESW " -> lc.incrementLocCtr_RSW(Integer.parseInt(assemblyCode.get(row).substring(12)));
            case "RESB " -> lc.incrementLocCtr_Byte(Integer.parseInt(assemblyCode.get(row).substring(12)));
            default -> lc.incrementLocCtr(opcode);
        }
    }

    private void endRecordAssembler(int row) throws IOException{
        cutTRecord(); // cutting the last t record to be added to the textRecords
        //Adding H record
        headerRecord.append(String.format("%06X",Integer.parseInt(lc.getLocCtr(),16) - Integer.parseInt(startingAddress,16),16).toUpperCase());// calculate the length of the program and append it to hRecord
//        System.out.println(headerRecord);
        HteWriter.write(headerRecord.toString()+"\n");
        //Adding T record
        for(String k: TextRecords){
            if(k.length()>10) {
//                System.out.println(k);
                HteWriter.write(k+"\n");
            }
        }
        //Adding E record
        HteWriter.write("E"+ String.format("%06X",Integer.parseInt(startingInstruction,16)));
//        System.out.println("E"+ String.format("%06X",Integer.parseInt(startingInstruction,16)));
    }

    private void openFile(){
        try{
            AsmReader = new BufferedReader(new FileReader(AssemblyFilePath));
            String line;
            while((line = AsmReader.readLine()) != null)
                assemblyCode.add(line);
            AsmReader.close();
        }catch (FileNotFoundException e){
            System.out.println("File not found");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void assemble(){
        openFile();
        System.out.println(assemblyCode);
        try{
            for (int i=0;i<assemblyCode.size();i++){
                switch(assemblyCode.get(i).substring(6,11).toUpperCase()){
                    case "START" -> headerRecordAssembler(i);
                    case "END  " -> endRecordAssembler(i);
                    default -> textRecordAssembler(i);
                }
            }
//            System.out.println("Symbol Table ->"+sym.getSymbolTable());
            System.out.println("All Text Records ->"+TextRecords);
            System.out.println("Successfully assembled Assembly code");
        }catch (Exception e){
            System.out.println("Error in assembling Assembly file at "+e);
        }finally {
            try{
                if (HteWriter != null)
                    HteWriter.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}