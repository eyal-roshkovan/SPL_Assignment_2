package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // TODO: main
        InputParser parser = new InputParser();
        try{
            ComputationNode node = parser.parse("C:\\Users\\eyalr\\Desktop\\Software Engineering\\2nd year\\1st semester\\SPL\\SPL_Assignment_2\\LinearAlgebraEngine\\example.json");
            System.out.println(node);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}