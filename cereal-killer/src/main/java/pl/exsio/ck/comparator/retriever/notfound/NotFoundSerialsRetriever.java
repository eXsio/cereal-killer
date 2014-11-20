
package pl.exsio.ck.comparator.retriever.notfound;

/**
 *
 * @author exsio
 */
public interface NotFoundSerialsRetriever {
    
    String[] getNotFoundSerialNumbers(String[] serials, String[] foundSerials);
}
