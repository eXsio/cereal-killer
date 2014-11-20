package pl.exsio.ck.comparator.result;

/**
 *
 * @author exsio
 */
public class ComparisonResultImpl implements ComparisonResult {

    private final String[] found;

    private final String[] notFound;

    public ComparisonResultImpl(String[] found, String[] notFound) {
        this.found = found;
        this.notFound = notFound;
    }

    @Override
    public  String[] getFound() {
        return this.found;
    }

    @Override
    public  String[] getNotFound() {
        return this.notFound;
    }

}
