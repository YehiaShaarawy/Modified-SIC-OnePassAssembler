import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class symbolTable{
    Boolean cut = false;
    listNode newNode = new listNode();
    listNode lastNode = new listNode();
    private static Map<String,listNode> symbolTab = new HashMap<>();

    public static Map<String,listNode> getSymbolTable(){
        return symbolTab;
    }


    public String checkSYMTAB(String location){
        for(String symbol : symbolTab.keySet()) {
            if(locationExistsInLinkedList(location, symbolTab.get(symbol)))
                return symbol;
        }
        return " ";
    }

    private static boolean locationExistsInLinkedList(String location, listNode head) {
        listNode current = head;
        while (current != null) {
            if (location.equals(current.getData())) {
                return true;  // location found in linked list
            }
            current = current.getNext(); // moves to the next node of the linked list
        }
        return false;  // location not found in linked list
    }

    private static void deleteLinkedListForSymbol(String symbol) {
        // Check if the symbol exists in the symbol table
        if (symbolTab.containsKey(symbol)) {
            // Retrieve the linked list associated with the symbol
            listNode head = symbolTab.get(symbol);
            // Delete the linked list by setting the head to null
            symbolTab.put(symbol, null);
        }
    }

    public void chcekLabel(String label, String location){
        if(symbolTab.containsKey(label)){
            System.out.println(symbolTab.get(label));
            if(symbolTab.get(label).getData()==""){
                symbolTab.put(label, new listNode(location));
                String textRecord = symbolTable.generateTextRecord(label);
                System.out.println(textRecord);
//                deleteLinkedListForSymbol(label);
            }
        }else {
            symbolTab.put(label, new listNode(location));
        }
    }

    public String getOperand(String operand, String location, List<String> textRecords){  //Forward Referencing
        if(symbolTab.containsKey(operand)){ //checking if operand is in the symbolTable
            if(symbolTab.get(operand).getData()!=null) // if so, it's data isn't null then return the data
                return symbolTab.get(operand).getData();
            else{ //if data is null, then create a new node with the location +1 for modification , getting the last node of that operand and connecting the last node t the new node
                newNode = new listNode(Integer.toHexString(Integer.parseInt(location,16)+1).toUpperCase());
                lastNode = getLastNode(symbolTab.get(operand));
                lastNode.setNext(newNode);
            }
//            System.out.println(getSymbolTable());
        }
        else{ //if operand isn't in the symbolTable, add it, and add a new node with location +1 to that operand
            symbolTab.put(operand, new listNode(null));
            newNode = new listNode(Integer.toHexString(Integer.parseInt(location,16)+1).toUpperCase());
            symbolTab.get(operand).setNext(newNode);
        }
        System.out.println(getSymbolTable());
        return "0000";
    }

    private listNode getLastNode(listNode node) {
        while (node.getNext() != null) {
            node = node.getNext();
        }
        return node;
    }

    public void cutRecord(){
        cut = true;
    }


    public void addSymbol(String symbol, String location, List<String> textRecords){
        for (String sym : symbolTab.keySet()) // if symbol matches the address, return symbol
            if(symbolTab.get(sym).equals(location))
                return;
        if(symbolTab.get(symbol)!=null&&symbolTab.get(symbol).getData()==null){ //Checking if label was in the linked list, if so, Creates A NEW T record -> "T^symbol second node in 6 bits^02^location"
            int nodeCount = 0;
            listNode currentNode =symbolTab.get(symbol);
            while(currentNode!=null){
                nodeCount++;
                if(currentNode.getNext()!=null){
                    currentNode = currentNode.getNext();
                    //Cut t record w
                    cutRecord();
                    textRecords.add("T^"+String.format("%06X",Integer.parseInt(currentNode.getData(),16))+"^02^"+location);
                    System.out.println("T^"+String.format("%06X",Integer.parseInt(currentNode.getData(),16))+"^02^"+location); //<-- TEXT RECORD
                }else{
                    break;
                }
            }
            symbolTab.put(symbol, new listNode(location));
        }
        symbolTab.put(symbol, new listNode(location));
    }

    public static String generateTextRecord(String symbol) {
        StringBuilder textRecordBuilder = new StringBuilder();
        // Check if the symbol exists in the symbol table
        if (symbolTab.containsKey(symbol)) {
            listNode current = symbolTab.get(symbol);
            while (current != null) {
                // Append the operand address to the text record
                textRecordBuilder.append(current.getData());
                // Move to the next node in the linked list
                current = current.getNext();
                // Add a space between operand addresses for clarity (adjust as needed)
                if (current != null) {
                    textRecordBuilder.append(" ");
                }
            }
        }
        // Convert the StringBuilder to a String and return the text record
        return textRecordBuilder.toString();
    }
}
