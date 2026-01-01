package spl.lae;
import java.io.IOException;
import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
        try{
            InputParser parser = new InputParser();
            ComputationNode tasks =  parser.parse(args[1]);
            LinearAlgebraEngine engine = new LinearAlgebraEngine(Integer.parseInt(args[0]));
            ComputationNode result = engine.run(tasks);
            if(result != null)
                OutputWriter.write(result.getMatrix(),args[2]);
        }
        catch (Exception e){
            OutputWriter.write(e.getMessage(), args[2]);
            System.exit(1);
        }
    }
}