package generation;
import java.io.*;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Random;

public class DataGeneration {

    public static void generateData(double MAX, double MIN, String category, int amount) throws IOException{
        String filepath = System.getProperty("user.dir");
        filepath +="\\data.txt";
        System.out.println(filepath);

        try{
            File file = new File(filepath);
            if(!file.exists()) {
                file.createNewFile();
                System.out.println("data.txt output complete.");
            }else{
                System.err.println("File existed.");
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            Random random = new Random();
            DecimalFormat df = new DecimalFormat( "0.0000000 ");
            for(int i=0;i<amount;i++) {
                String randDoubleLat = df.format((MIN + (MAX - MIN) * random.nextDouble()));
                String randDoubleLon = df.format((MIN + (MAX - MIN) * random.nextDouble()));
                bw.write("id" + i + " " + parseCat(category) + " " + randDoubleLat + " " + randDoubleLon);
                bw.newLine();
            }
            bw.close();
            fw.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String parseCat(String category) {
        String cat = null;
        switch (category) {
            case "restaurant":
            case "Restaurant":
            case "RESTAURANT":
                cat = "restaurant";
                break;
            case "education":
            case "Education":
            case "EDUCATION":
                cat = "education";
                break;
            case "hospital":
            case "Hospital":
            case "HOSPITAL":
                cat = "hospital";
                break;
            default:
                System.err.println("Unknown Category.");
                System.err.println(category);
        }
        return cat;
    }

    public static void main(String[] args) {

        try{
            if(args.length != 4){
                System.err.println("4 parameters are required.");
                throw new InputMismatchException();
            }else{
                double MAX = Double.parseDouble(args[0]);
                double MIN = Double.parseDouble(args[1]);
                String cat = args[2];
                int amount = Integer.parseInt(args[3]);
                generateData(MAX,MIN,cat,amount);
            }
        }catch (NumberFormatException | IOException | InputMismatchException e){
            e.printStackTrace();
        }
    }

}
