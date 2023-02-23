package nl.tgow.datastructures;

public class Stapel<T> {
    private Node boven;
    private Node onder;

    public Stapel(){
        this.boven = null;
        this.onder = null;
    }

    private class Node{
        private T obj;
        private Node volgende;
        private Node vorige;

        public Node(T obj){
            this.obj = obj;
            this.volgende = null;
            this.vorige = null;
        }

        public void zetVolgende(Node volgende){
            this.volgende = volgende;
        }

        public Node getVolgende(){
            return this.volgende;
        }

        public T getObj(){
            return this.obj;
        }
    }

    public void duw(T obj){
        Node NieuweNode = new Node(obj);
        NieuweNode.zetVolgende(this.boven);
        this.boven = NieuweNode;
    }

    public T pak(){
        if(this.boven == null){
            return null;
        }
        Node boven = this.boven;
        this.boven = boven.getVolgende();
        return boven.getObj();
    }

    public int lengte(){
        if(this.boven == null)
            return 0;
        Node huidig = this.boven;
        int count = 1;
        while(huidig.getVolgende() != null){
            huidig = huidig.getVolgende();
            count++;
        }
        return count;
    }
}
