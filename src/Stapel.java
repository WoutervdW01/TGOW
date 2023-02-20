public class Stapel {
    private Node boven;
    private Node onder;

    public Stapel(){
        this.boven = null;
        this.onder = null;
    }

    private class Node{
        private Object obj;
        private Node volgende;
        private Node vorige;

        public Node(Object obj){
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

        public Object getObj(){
            return this.obj;
        }
    }

    public void duw(Object obj){
        Node NieuweNode = new Node(obj);
        NieuweNode.zetVolgende(this.boven);
        this.boven = NieuweNode;
    }

    public Object pak(){
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
