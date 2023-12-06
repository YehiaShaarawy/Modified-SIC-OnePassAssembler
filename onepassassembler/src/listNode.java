public class listNode {
    private String data;
    private listNode next;
    public listNode(){}
    public String getData() {
        return data;
    }
    public listNode getNext() {
        return next;
    }
    public void setNext(listNode next) {
        this.next = next;
    }
    public listNode(String data){
        this.data= data;
        this.next = null;
    }
    @Override
    public String toString() {
        return "listNode{" +
                "data='" + data + '\'' +
                ", next=" + next +
                '}';
    }
}
