/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.exsio.ck.comparator;

import java.util.Collection;
import pl.exsio.ck.model.Entry;

public class ComparisonResultImpl implements ComparisonResult {

    private final Collection<Entry> found;

    private final Collection<String> notFound;

    public ComparisonResultImpl(Collection<Entry> found, Collection<String> notFound) {
        this.found = found;
        this.notFound = notFound;
    }

    @Override
    public Collection<Entry> getFound() {
        return this.found;
    }

    @Override
    public Collection<String> getNotFound() {
        return this.notFound;
    }

}