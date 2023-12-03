import java.util.HashMap;
import java.util.Map;

public class symbolTable {
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

    public void addSymbol(String symbol, String location){
        for (String sym : symbolTab.keySet()) // if symbol matches the address, return symbol
            if(symbolTab.get(sym).equals(location))
                return;
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
