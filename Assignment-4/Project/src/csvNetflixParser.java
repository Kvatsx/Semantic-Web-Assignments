import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.ArrayList;

public class csvNetflixParser {
    public ArrayList<NetflixObject> parse(String filepath) {
        try {
            FileReader filereader = new FileReader(filepath);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            ArrayList<NetflixObject> Arr = new ArrayList<>();
            while ((nextRecord = csvReader.readNext()) != null)
            {
                NetflixObject temp = new NetflixObject();
                temp.setShow_id(nextRecord[0]);
                temp.setType(nextRecord[1]);
                temp.setTitle(nextRecord[2]);
                temp.setDirectors(nextRecord[3].split(", "));
                temp.setCast(nextRecord[4].split(", "));
                temp.setCountries(nextRecord[5].split(", "));
                temp.setDate_added(nextRecord[6]);
                temp.setRelease_year(nextRecord[7]);
                temp.setRating(nextRecord[8]);
                temp.setDuration(nextRecord[9]);
                temp.setListed_in(nextRecord[10].split(", "));
                temp.setDescription(nextRecord[11]);
                Arr.add(temp);
            }
            Arr.remove(0);
            return Arr;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}