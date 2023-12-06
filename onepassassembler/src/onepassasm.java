import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class onepassasm {
    boolean createTStartAdd = false;
    String firstInstructionAddress ="";
    boolean newTextRecord = false;
    int tRecordSkipped =0;
    int textRecordLength = 0;
    String maskingBitBinary ="";
    opcode op = new opcode();
    symbolTable sym = new symbolTable();
    locationCounter lc = new locationCounter();
    private StringBuilder textRecord = new StringBuilder();
    List<String> textRecords = new ArrayList<>();
    private ArrayList<String> assemblyCode = new ArrayList<>();
    private String AssemblyFilePath = "src/full.txt";
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
        String startingAddress = assemblyCode.get(row).substring(12,16); //setting the starting address
        insertToSYMTAB(programName,startingAddress);
        lc.initializeLocCtr(Integer.parseInt(startingAddress,16)); //initializing the location counter
        HteWriter.write("H"+String.format("%6s", programName).replace(' ', 'X')+String.format("%06d",Integer.parseInt(lc.getLocCtr()))+"Append at the end record");
    }


    private void newTRecord(){
//        System.out.println("INSIDE");
        createTStartAdd=false; //so it doesnt create a new T.starting address.
//        System.out.println(textRecords);
        textRecords.add(textRecord.toString()); //<--
        textRecord = new StringBuilder();
        generateStartTextRecord();
        tRecordSkipped=0;
        textRecordLength=0;
        newTextRecord = false;
    }
    public void cutTRecord(){
        if (!newTextRecord){ // On the same T record, we calculate the length of the t record and add it to List which will hold all t records and start a new T record.
            textRecord.insert(7,String.format("%02X",Integer.parseInt(Integer.toString(Integer.parseInt(lc.getLocCtr(),16)-Integer.parseInt(firstInstructionAddress,16))),16));
            textRecords.add(textRecord.toString());
            textRecord = new StringBuilder();
            createTStartAdd = true;
        }
        textRecordLength=0; //resetting the textrecord length for new T records for future.
        newTextRecord = true; // setting it to true to make a new t record.
    }
    private void generateStartTextRecord(){
        if(!createTStartAdd){ //if createTStartAdd is false which means that it's ready to generate T.starting address.
//            System.out.println("Generating First T record ->"+ "T"+String.format("%06X",Integer.parseInt(lc.getLocCtr(),16)));
            textRecord.append("T"+String.format("%06X",Integer.parseInt(lc.getLocCtr(),16)));
            firstInstructionAddress = lc.getLocCtr(); //getting the locationCounter of first instruction for getting the length of the t record.
            createTStartAdd = true; //setting it to true so it doesn't create a new first T record.
        }
    }


    private void generateTextRecord(String instruction){
        generateStartTextRecord(); //writing the first T record -> T.starting address.

        if(instruction.equals("RESW ")||instruction.equals("RESB ")){
//            System.out.println("found RESW/RESB");
            tRecordSkipped++; // skip variable increments how many times resw or resb has been skipped do it doesnt create a new t record [future case].
            cutTRecord();
        }
        //Counter bits/textrecordlength = textrecord length w myzdshg 3n 30 approx 1E
        //Counter bits must 0 w flag true w mykonsh resw /resb
        //Text record

        if(!(instruction.equals("RESW ")||instruction.equals("RESB "))&&(tRecordSkipped>0)&&newTextRecord&&textRecordLength==0){ //if instruction isn't RESW / RESB, and check if there are skips and its read for new T record and making sure that the text record length is zero, Therefore creates a new T record.
//            System.out.println("new text record at address : "+lc.getLocCtr());
            newTRecord();
        }

        if(!(instruction.equals("RESW ")||instruction.equals("RESB "))&&(tRecordSkipped==0)&&textRecordLength>27){
            textRecord.insert(7,String.format("%02X",Integer.parseInt(Integer.toString(Integer.parseInt(lc.getLocCtr(),16)-Integer.parseInt(firstInstructionAddress,16))),16));
            newTRecord();
        }
//        System.out.println("TEXT RECORD LENGTH "+textRecordLength);
//        System.out.println("ENNNNDD"+textRecords);
//        System.out.println("Text Record -> "+textRecord);
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
            sym.addSymbol(label,lc.getLocCtr(),textRecords);
            if(sym.cut){
                cutTRecord();
                newTRecord();
                sym.cut = false;
            }
            insertToSYMTAB(label,lc.getLocCtr());
        }

//        System.out.println("Text Record length ->"+textRecordLength);
//        System.out.println("Text Record After ->"+textRecord);

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
            maskingBitBinary += "1";
            if(format == 1){
                textRecordLength +=1;
                objectcodeHex = opcode;
                textRecord.append(objectcodeHex);
            }else if (format ==3) {
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
                    String address = sym.getOperand(operand.substring(0,6),lc.getLocCtr(),textRecords);
                    String addressBinary = String.format("%015d",Long.parseLong(Long.toBinaryString(Long.parseLong(address,16))));
                    String opjectcodeBinary = opcodeIndex + addressBinary;
                    objectcodeHex = Integer.toHexString(Integer.parseInt(opjectcodeBinary,2)).toUpperCase();
                    textRecord.append(objectcodeHex);
//                    System.out.println("INDEX OBJECT CODE ->"+objectcodeHex);
                } else{ //Normal Format  3 object code
                    String address = sym.getOperand(operand.substring(0,6),lc.getLocCtr(),textRecords);
                    objectcodeHex = opcode + address;
                    textRecord.append(objectcodeHex);
                }
            }
        }

        System.out.println(lc.getLocCtr()+"\t"+label+"\t"+instruction+"\t"+operand+"\t"+objectcodeHex);
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
        System.out.println("Text Record After ->"+textRecord);
        System.out.println("================================================================================");
    }
    private void endRecordAssembler(int row) throws IOException{
        cutTRecord();
        System.out.println("Text Records After ->"+textRecords);
        System.out.println("ENDDD at : "+lc.getLocCtr()); // calculate the length of the program and append it to hRecord
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
            System.out.println(sym.getSymbolTable());
            System.out.println(textRecords);
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