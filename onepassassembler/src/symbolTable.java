import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class symbolTable{
    Boolean cut = false;
    listNode newNode = new listNode();
    listNode lastNode = new listNode();
    listNode currentNode;
    private static Map<String,listNode> symbolTab = new HashMap<>();
    public static Map<String,listNode> getSymbolTable(){
        return symbolTab;
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
            currentNode =symbolTab.get(symbol);
            while(currentNode!=null){ //Forward Referencing
                if(currentNode.getNext()!=null){
                    currentNode = currentNode.getNext();
                    cutRecord();
                    textRecords.add("T"+String.format("%06X",Integer.parseInt(currentNode.getData(),16))+"02000"+location);
//                    System.out.println("T^"+String.format("%06X",Integer.parseInt(currentNode.getData(),16))+"^02^"+location); //<-- TEXT RECORD
                }else{
                    break;
                }
            }
            symbolTab.put(symbol, new listNode(location));
        }
        symbolTab.put(symbol, new listNode(location));
    }
}
