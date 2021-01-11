
package fetcher;

import com.google.gson.Gson;
import dto.CatFactDTO;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class CatFactFetcher {
    
    private static final String URL = "https://cat-fact.herokuapp.com/facts";
    
    public static String fetchCatFactParrallel (ExecutorService es, Gson GSON) throws InterruptedException, ExecutionException, TimeoutException{
        
       Callable<CatFactDTO[]> task = new Callable<CatFactDTO[]>() {
           @Override
           public CatFactDTO[] call() throws Exception {
               String fetchedData = utils.HttpUtils.fetchData(URL);           
               CatFactDTO[] catFactDTOArray = GSON.fromJson(fetchedData, CatFactDTO[].class);
               return catFactDTOArray;

           }
      
       };
        Future<CatFactDTO[]> futureCats = es.submit(task);
        CatFactDTO[] catFact = futureCats.get(2, TimeUnit.SECONDS);
        String catFactJSON = GSON.toJson(catFact);
        System.out.println(catFactJSON);
        return catFactJSON;
       
        
    }
    
   
    
    
}
