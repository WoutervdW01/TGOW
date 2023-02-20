package nl.tgow.datastructures;

public class Program {

    public static void main(String[] args){
        Stapel myStack = new Stapel();

        myStack.duw("Alice");
        myStack.duw("Bob");
        myStack.duw("Eve");

        System.out.println(myStack.pak());
        System.out.println(myStack.pak());

        myStack.duw("Floris");

        myStack.duw(1);
        myStack.duw(23.40);

        System.out.println(myStack.pak());
        System.out.println(myStack.pak());
    }
}
