import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class onepassasm {
    opcode op = new opcode();
    symbolTable sym = new symbolTable();
    locationCounter lc = new locationCounter();
    private ArrayList<String> assemblyCode = new ArrayList<>();
    private String AssemblyFilePath = "src/in.txt";
    private String HTEFilePath = "src/objectcode.txt";
    private String SymbolTableFilePath = "src/symbolTable.txt";
    private BufferedReader AsmReader;
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
        String startingAddress = assemblyCode.get(row).substring(12,16);
        insertToSYMTAB(programName,startingAddress);
        lc.initializeLocCtr(Integer.parseInt(startingAddress,16));
        HteWriter.write("H"+String.format("%6s", programName).replace(' ', 'X')+String.format("%06d",Integer.parseInt(lc.getLocCtr()))+String.format("%06X\n",0));
    }
    private void textRecordAssembler(int row) throws IOException {
        String label = assemblyCode.get(row).substring(0,6).toUpperCase();
        String instruction = assemblyCode.get(row).substring(6,11).toUpperCase();
        String opcode = op.getOpOpcode(instruction);
        int format = op.getOpFormat(instruction);

        System.out.println(lc.getLocCtr());

        if(label.equals("      "))
            label = "      ";
        else{
            sym.addSymbol(label,lc.getLocCtr());
            insertToSYMTAB(label,lc.getLocCtr());
            System.out.println(sym.getSymbolTable());
        }
        System.out.println(lc.getLocCtr()+"\t"+label+"\t"+instruction+"\t"+opcode+"\t"+format);

        switch (instruction) {
            case "BYTE " -> {
                if (assemblyCode.get(row).charAt(12) == 'C')
                    lc.incrementLocCtr_Byte(assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 1).length());
                else if (assemblyCode.get(row).charAt(12) == 'X')
                    lc.incrementLocCtr_Byte((assemblyCode.get(row).substring(14, assemblyCode.get(row).length() - 1).length()) / 2);
            }
            case "WORD " -> lc.incrementLocCtr_Word();
            case "RESW " -> lc.incrementLocCtr_RSW(Integer.parseInt(assemblyCode.get(row).substring(12)));
            case "RESB " -> lc.incrementLocCtr_Byte(Integer.parseInt(assemblyCode.get(row).substring(12)));
        }

    }
    private void endRecordAssembler(int row) throws IOException{

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
