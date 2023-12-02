public class locationCounter {
    opcode op = new opcode();
    private int locCtr;
    public String getLocCtr() {
        return Integer.toString(locCtr,16).toUpperCase();
    }
    public void initializeLocCtr(int startingAddress){
        locCtr = startingAddress;
    }
    public void incrementLocCtr(String opCode){
        if(op.getOpFormat(opCode)==1)
            locCtr+=1;
        else if(op.getOpFormat(opCode)==3)
            locCtr+=3;
    }
    public void incrementLocCtr_Byte(int length){
        locCtr+=length;
    }
    public void incrementLocCtr_Word(){
        locCtr+=3;
    }
    public void incrementLocCtr_RSW(int numOfWords){
        locCtr+=numOfWords*3;
    }
}
