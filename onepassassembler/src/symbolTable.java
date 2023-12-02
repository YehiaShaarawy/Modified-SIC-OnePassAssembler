import java.util.HashMap;
import java.util.Map;

public class symbolTable {
    private static Map<String,String> symbolTable = new HashMap<>();

    public static Map<String,String> getSymbolTable(){
        return symbolTable;
    }

    public static String checkSYMTAB(String location){
        for(String symbol : symbolTable.keySet()) {
            if(symbolTable.get(symbol) == location)
                return symbol;
        }
        return " ";
    }

    public static void addSymbol(String symbol, String location){
        for (String sym : symbolTable.keySet()) // if symbol matches the address, return symbol
            if(symbolTable.get(sym).equals(location))
                return;
        symbolTable.put(symbol, location);
    }
}
